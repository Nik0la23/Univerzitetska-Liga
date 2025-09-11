package mk.ukim.finki.wp.liga.web.volleyball;

import lombok.AllArgsConstructor;
import mk.ukim.finki.wp.liga.model.FootballTeam;
import mk.ukim.finki.wp.liga.model.VolleyballPlayer;
import mk.ukim.finki.wp.liga.model.VolleyballTeam;
import mk.ukim.finki.wp.liga.model.dtos.TeamStandingsDTO;
import mk.ukim.finki.wp.liga.model.dtos.VolleyBallStandings;
import mk.ukim.finki.wp.liga.service.volleyball.VolleyballMatchService;
import mk.ukim.finki.wp.liga.service.volleyball.VolleyballPlayerService;
import mk.ukim.finki.wp.liga.service.volleyball.VolleyballTeamService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/volleyball/teams")
public class VolleyballTeamController {

    private final VolleyballPlayerService volleyballPlayerService;
    private final VolleyballTeamService volleyballTeamService;
    private final VolleyballMatchService volleyballMatchService;

    @GetMapping()
    public String getVolleyballTeamsPage(@RequestParam(required = false) String error, Model model) {
        if (error != null && !error.isEmpty()) {
            model.addAttribute("hasError", true);
            model.addAttribute("error", error);
        }
        List<VolleyballTeam> volleyballTeams = this.volleyballTeamService.findAllOrderByPointsDesc();
        model.addAttribute("volleyballTeams", volleyballTeams);
        model.addAttribute("bodyContent","volleyball/volleyball_teams");
        return "volleyball/master_template";
    }

    @GetMapping("/team/{id}")
    public String getTeam(@PathVariable Long id, Model model) {
        VolleyballTeam team = volleyballTeamService.findById(id);
        if (team != null) {
            model.addAttribute("team", team);
            model.addAttribute("players", team.getPlayers());
            List<VolleyballPlayer> top5Players=volleyballPlayerService.getTop5PlayersByTeam(id);
            model.addAttribute("topPlayers", top5Players);
        } else {
            model.addAttribute("hasError", true);
            model.addAttribute("error", "Team not found");
        }
        model.addAttribute("bodyContent","volleyball/volleyball_team_details");
        return "volleyball/master_template";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        VolleyballTeam team = volleyballTeamService.findById(id);
        if (team == null) {
            return "redirect:/volleyball/teams";
        }
        model.addAttribute("team", team);
        model.addAttribute("bodyContent","volleyball/edit_volleyball_team");
        return "volleyball/master_template";
    }

    @PostMapping("/edit/{id}")
    public String editTeam(@PathVariable Long id,
                           @RequestParam String teamName,
                           @RequestParam(value = "logo", required = false) MultipartFile logo,
                           Model model) throws IOException {
        VolleyballTeam existingTeam = volleyballTeamService.findById(id);
        if (existingTeam == null) {
            return "redirect:/volleyball/teams";
        }
        byte[] imageBytes = existingTeam.getLogo();
        if (logo != null && !logo.isEmpty()) {
            imageBytes = logo.getBytes();
        }
        volleyballTeamService.saveTable(id, teamName,imageBytes);
        String imageUrl = "/volleyball/teams/logo/" + id;
        model.addAttribute("teamLogoUrl", imageUrl);
        return "redirect:/volleyball/teams";
    }
    @GetMapping("/logo/{id}")
    public ResponseEntity<byte[]> getTeamLogo(@PathVariable Long id) {
        VolleyballTeam team = volleyballTeamService.findById(id);
        if (team != null && team.getLogo() != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            return new ResponseEntity<>(team.getLogo(), headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/add-form")
    public String showAddTeamForm(Model model) {
        List<VolleyballPlayer> players = volleyballPlayerService.listAllPlayers();
        model.addAttribute("players", players);
        model.addAttribute("bodyContent","volleyball/add_volleyball_team");
        return "volleyball/master_template";
    }

    @PostMapping("/add")
    public String addTeam(@RequestParam("teamName") String teamName,
                          @RequestParam(value = "logo", required = false) MultipartFile logo) throws IOException {
        byte[] imageBytes = null;
        if (logo != null && !logo.isEmpty()) {
            imageBytes = logo.getBytes();
        }

        volleyballTeamService.create(teamName, imageBytes);

        return "redirect:/volleyball/teams";
    }

    @GetMapping("/show/{id}")
    public String getTeamMatches(@PathVariable Long id, Model model) {
        VolleyballTeam team = volleyballTeamService.findById(id);
        if (team == null) {
            return "redirect:/volleyball/teams";
        }
        model.addAttribute("team", team);
        model.addAttribute("fixtures", team.getVolleyballFixtures());
        model.addAttribute("results", team.getVolleyballResults());
        model.addAttribute("bodyContent","volleyball/show_volleyball_team_matches");
        return "volleyball/master_template";
    }

    @PostMapping("/delete/{id}")
    public String deleteTeam(@PathVariable Long id, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", "Deleting teams is disabled. Teams are kept forever.");
        return "redirect:/volleyball/teams";
    }
    @GetMapping("/standings")
    public String getStandings(Model model) {
        List<VolleyBallStandings> standings = volleyballTeamService.getStandings();
        model.addAttribute("standings", standings);
        model.addAttribute("bodyContent","volleyball/volleyball_standings");
        return "volleyball/master_template";
    }
}