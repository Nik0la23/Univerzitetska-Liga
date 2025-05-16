package mk.ukim.finki.wp.liga.web.basketball;

import lombok.AllArgsConstructor;
import mk.ukim.finki.wp.liga.model.BasketballPlayer;
import mk.ukim.finki.wp.liga.model.BasketballTeam;
import mk.ukim.finki.wp.liga.model.FootballPlayer;
import mk.ukim.finki.wp.liga.model.FootballTeam;
import mk.ukim.finki.wp.liga.model.dtos.BasketballTeamStandings;
import mk.ukim.finki.wp.liga.model.dtos.TeamStandingsDTO;
import mk.ukim.finki.wp.liga.service.basketball.BasketballPlayerService;
import mk.ukim.finki.wp.liga.service.basketball.BasketballTeamService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/basketball/teams")
public class BasketballTeamController {

    private final BasketballTeamService basketballTeamService;
    private final BasketballPlayerService basketballPlayerService;

    @GetMapping()
    public String getFootballTeamsPage(@RequestParam(required = false) String error, Model model){
        if (error != null && !error.isEmpty()) {
            model.addAttribute("hasError", true);
            model.addAttribute("error", error);
        }
        List<BasketballTeam> basketballTeams = this.basketballTeamService.findAllOrderByPointsDesc();
        model.addAttribute("basketballTeams",basketballTeams);
        //model.addAttribute("bodyContent","football_teams");
        model.addAttribute("bodyContent","basketball/basketball_teams");
        return "/basketball/master_template";
    }

    @GetMapping("/team/{id}")
    public String getTeam(@PathVariable Long id, Model model){
        BasketballTeam team = basketballTeamService.findById(id);
        if (team != null) {
            model.addAttribute("team", team);
            model.addAttribute("players", team.getPlayers());
            List<BasketballPlayer> top5Players=basketballPlayerService.getTop5PlayersByTeam(id);
            model.addAttribute("topPlayers",top5Players);
        } else {
            model.addAttribute("hasError", true);
            model.addAttribute("error", "Team not found");
        }
        model.addAttribute("bodyContent","basketball/basketball_team_details");
        return "/basketball/master_template";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        BasketballTeam team = basketballTeamService.findById(id);
        if (team == null) {
            return "redirect:/basketball/teams";
        }
        model.addAttribute("team", team);
        model.addAttribute("bodyContent","basketball/edit_basketball_table");
        return "/basketball/master_template";
    }

    @PostMapping("/edit/{id}")
    public String editTeam(@PathVariable Long id,
                           @RequestParam String teamName,
                           @RequestParam(value = "logo", required = false) MultipartFile logo,
                           Model model) throws IOException {
        BasketballTeam existingTeam = basketballTeamService.findById(id);
        if (existingTeam == null) {
            return "redirect:/basketball/teams";
        }
        byte[] imageBytes = existingTeam.getLogo();
        if (logo != null && !logo.isEmpty()) {
            imageBytes = logo.getBytes();
        }
        basketballTeamService.saveTable(id, teamName,imageBytes);
        String imageUrl = "/basketball/teams/logo/" + id;
        model.addAttribute("teamLogoUrl", imageUrl);
        return "redirect:/basketball/teams";
    }
    
    @GetMapping("/logo/{id}")
@ResponseBody
public byte[] getTeamLogo(@PathVariable Long id) {
    BasketballTeam team = basketballTeamService.findById(id);
    return team.getLogo();
}

    @GetMapping("/add")
    public String showAddTeamForm(Model model) {
        List<BasketballPlayer> players = basketballPlayerService.listAllPlayers();
        model.addAttribute("players", players);
        model.addAttribute("bodyContent","basketball/add_basketball_team");
        return "/basketball/master_template";
    }

    @PostMapping("/add")
    public String addTeam(@RequestParam("teamName") String teamName,
                          @RequestParam(value = "logo", required = false) MultipartFile logo) throws IOException {
        byte[] imageBytes = null;
        if (logo != null && !logo.isEmpty()) {
            imageBytes = logo.getBytes();
        }
        // Create the team
        basketballTeamService.create(teamName, imageBytes);

        // Redirect to the teams list page
        return "redirect:/basketball/teams";
    }

    @GetMapping("/show/{id}")
    public String getTeamMatches(@PathVariable Long id, Model model) {
        BasketballTeam team = basketballTeamService.findById(id);
        if (team == null) {
            return "redirect:/basketball/teams";
        }
        model.addAttribute("team", team);
        model.addAttribute("fixtures", team.getBasketballFixtures());
        model.addAttribute("results", team.getBasketballResults());
        model.addAttribute("bodyContent","basketball/show_basketball_team_matches");
        return "/basketball/master_template";
    }

    @PostMapping("/delete/{id}")
    public String deleteTeam(@PathVariable Long id){
        basketballTeamService.delete(id);
        return "redirect:/basketball/teams";
    }
    @GetMapping("/standings")
    public String getStandings(Model model) {
        List<BasketballTeamStandings> standings = basketballTeamService.getStandings();
        model.addAttribute("standings", standings);
        model.addAttribute("bodyContent", "basketball/basketball_standings"); // This should match the template file name
        return "basketball/master_template"; // The master template should be in the same folder or adjust path if needed
    }


}
