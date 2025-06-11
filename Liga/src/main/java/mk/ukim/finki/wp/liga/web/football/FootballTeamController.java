package mk.ukim.finki.wp.liga.web.football;

import lombok.AllArgsConstructor;
import mk.ukim.finki.wp.liga.model.FootballPlayer;
import mk.ukim.finki.wp.liga.model.FootballTeam;
import mk.ukim.finki.wp.liga.model.dtos.TeamStandingsDTO;
import mk.ukim.finki.wp.liga.service.football.FootballMatchService;
import mk.ukim.finki.wp.liga.service.football.FootballPlayerScoredService;
import mk.ukim.finki.wp.liga.service.football.FootballPlayerService;
import mk.ukim.finki.wp.liga.service.football.FootballTeamService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/football/teams")
public class FootballTeamController {

    private final FootballPlayerService footballPlayerService;
    private final FootballTeamService footballTeamService;
    private final FootballMatchService footballMatchService;
    private final FootballPlayerScoredService footballPlayerScoredService;

    @GetMapping
    public String getFootballTeamsPage(@RequestParam(required = false) String error, Model model) {
        if (error != null && !error.isEmpty()) {
            model.addAttribute("hasError", true);
            model.addAttribute("error", error);
        }
        List<FootballTeam> footballTeams = this.footballTeamService.findAllOrderByPointsDesc();
        model.addAttribute("footballTeams", footballTeams);
        model.addAttribute("bodyContent", "football/football_teams");
        return "football/master_template";
    }

    @GetMapping("/team/{id}")
    public String getTeam(@PathVariable Long id, Model model) {
        FootballTeam team = footballTeamService.findById(id);
        if (team != null) {
            model.addAttribute("team", team);
            model.addAttribute("players", team.getPlayers());
            List<FootballPlayer> top5Players = footballPlayerService.getTop5PlayersByTeam(id);
            model.addAttribute("topPlayers", top5Players);
        } else {
            model.addAttribute("hasError", true);
            model.addAttribute("error", "Team not found");
        }
        model.addAttribute("bodyContent", "football/football_team_details");
        return "football/master_template";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String showEditForm(@PathVariable Long id, Model model) {
        FootballTeam team = footballTeamService.findById(id);
        if (team == null) {
            return "redirect:/football/teams";
        }
        model.addAttribute("team", team);
        model.addAttribute("bodyContent", "edit_football_table");
        return "master_template";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String editTeam(@PathVariable Long id,
                           @RequestParam String teamName,
                           @RequestParam(value = "logo", required = false) MultipartFile logo,
                           Model model) throws IOException {
        FootballTeam existingTeam = footballTeamService.findById(id);
        byte[] imageBytes = existingTeam.getLogo();
        if (logo != null && !logo.isEmpty()) {
            try {
                imageBytes = logo.getBytes();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        footballTeamService.update(id, teamName, imageBytes);
        String imageUrl = "/football/teams/logo/" + id;
        model.addAttribute("teamLogoUrl", imageUrl);
        return "redirect:/football/teams";
    }

    @GetMapping("/logo/{id}")
    public ResponseEntity<byte[]> getTeamLogo(@PathVariable Long id) {
        FootballTeam team = footballTeamService.findById(id);
        if (team != null && team.getLogo() != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            return new ResponseEntity<>(team.getLogo(), headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/add")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String showAddTeamForm(Model model) {
        List<FootballPlayer> players = footballPlayerService.listAllPlayers();
        model.addAttribute("players", players);
        model.addAttribute("bodyContent", "add_football_team");
        return "master_template";
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String addTeam(@RequestParam("teamName") String teamName,
                          @RequestParam(value = "logo", required = false) MultipartFile logo) throws IOException {
        byte[] imageBytes = null;
        if (logo != null && !logo.isEmpty()) {
            try {
                imageBytes = logo.getBytes();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to read the image file", e);
            }
        }
        footballTeamService.create(teamName, imageBytes);
        return "redirect:/football/teams";
    }

    @GetMapping("/show/{id}")
    public String getTeamMatches(@PathVariable Long id, Model model) {
        FootballTeam team = footballTeamService.findById(id);
        if (team == null) {
            return "redirect:/football/teams";
        }
        model.addAttribute("team", team);
        model.addAttribute("fixtures", team.getFootballFixtures());
        model.addAttribute("results", team.getFootballResults());
        model.addAttribute("bodyContent", "show_football_team_matches");
        return "master_template";
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String deleteTeam(@PathVariable Long id) {
        footballTeamService.delete(id);
        return "redirect:/football/teams";
    }

    @GetMapping("/standings")
    public String getStandings(Model model) {
        List<TeamStandingsDTO> standings = footballTeamService.getStandings();
        model.addAttribute("standings", standings);
        model.addAttribute("bodyContent", "football_standings");
        return "master_template";
    }
}