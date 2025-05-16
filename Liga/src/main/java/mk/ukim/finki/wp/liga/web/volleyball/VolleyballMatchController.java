package mk.ukim.finki.wp.liga.web.volleyball;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import mk.ukim.finki.wp.liga.model.*;
import mk.ukim.finki.wp.liga.model.dtos.FootballPlayerDTO;
import mk.ukim.finki.wp.liga.model.dtos.VolleyballPlayerDTO;
import mk.ukim.finki.wp.liga.service.volleyball.VolleyballMatchService;
import mk.ukim.finki.wp.liga.service.volleyball.VolleyballPlayerMatchStatsService;
import mk.ukim.finki.wp.liga.service.volleyball.VolleyballPlayerService;
import mk.ukim.finki.wp.liga.service.volleyball.VolleyballTeamService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@AllArgsConstructor
@RequestMapping({"/volleyball/matches","/volleyball"})
public class VolleyballMatchController {
    private final VolleyballMatchService volleyballMatchService;
    private final VolleyballPlayerService volleyballPlayerService;
    private final VolleyballTeamService volleyballTeamService;
    private final VolleyballPlayerMatchStatsService volleyballPlayerMatchStatsService;

    @GetMapping
    public String showAllMatches(Model model) {
        List<VolleyballMatch> volleyballMatches = volleyballMatchService.listAllVolleyballMatches();
        Map<LocalDate, List<VolleyballMatch>> groupedMatches = volleyballMatchService.groupMatchesByDate(volleyballMatches);
        model.addAttribute("groupedMatches", groupedMatches);
        model.addAttribute("bodyContent","volleyball/volleyball_matches");
        return "volleyball/master_template";
    }

    @GetMapping("/details/{id}")
    public String showMatchDetails(@PathVariable Long id, Model model) {
        VolleyballMatch match = volleyballMatchService.findById(id);
        match.setHomeTeam(volleyballTeamService.findById(match.getHomeTeam().getVolleyball_team_id()));
        match.setAwayTeam(volleyballTeamService.findById(match.getAwayTeam().getVolleyball_team_id()));
        model.addAttribute("match", match);
        model.addAttribute("bodyContent","volleyball/volleyball_match_details");
        return "volleyball/master_template";
    }

    @GetMapping("/results/details/{id}")
    public String showMatchResultsDetails(@PathVariable Long id, Model model) {
        VolleyballMatch match = volleyballMatchService.findById(id);
        match.setHomeTeam(volleyballTeamService.findById(match.getHomeTeam().getVolleyball_team_id()));
        match.setAwayTeam(volleyballTeamService.findById(match.getAwayTeam().getVolleyball_team_id()));
        model.addAttribute("match", match);
        model.addAttribute("bodyContent","volleyball/volleyball_match_results_details");
        return "volleyball/master_template";
    }

    @GetMapping("/results/details/stats/{id}")
    public String showMatchDetailsAndStats(@PathVariable Long id, Model model) {
        VolleyballMatch match = volleyballMatchService.findById(id);
        match.setHomeTeam(volleyballTeamService.findById(match.getHomeTeam().getVolleyball_team_id()));
        match.setAwayTeam(volleyballTeamService.findById(match.getAwayTeam().getVolleyball_team_id()));
        VolleyballTeam homeTeam = match.getHomeTeam();
        VolleyballTeam awayTeam = match.getAwayTeam();

        List<VolleyballPlayer> homePlayers = homeTeam.getPlayers();
        List<VolleyballPlayer> awayPlayers = awayTeam.getPlayers();

        int homePoints = 0;
        int homeBlocks = 0;
        int homeAssists = 0;
        int homeServings = 0;

        for(VolleyballPlayer player : homePlayers){
            homePoints += player.getScoredPoints();
            homeAssists += player.getAssists();
            homeBlocks += player.getBlocks();
            homeServings += player.getServings();
        }

        int awayPoints = 0;
        int awayBlocks = 0;
        int awayAssists = 0;
        int awayServings = 0;

        for(VolleyballPlayer player : awayPlayers){
            awayPoints += player.getScoredPoints();
            awayAssists += player.getAssists();
            awayBlocks += player.getBlocks();
            awayServings += player.getServings();
        }

        model.addAttribute("match", match);
        model.addAttribute("homeTeam",homeTeam);
        model.addAttribute("awayTeam",awayTeam);
        model.addAttribute("homePoints",homePoints);
        model.addAttribute("homeBlocks",homeBlocks);
        model.addAttribute("homeAssists",homeAssists);
        model.addAttribute("homeServings",homeServings);
        model.addAttribute("awayServings",awayServings);
        model.addAttribute("awayBlocks",awayBlocks);
        model.addAttribute("awayPoints",awayPoints);
        model.addAttribute("awayAssists",awayAssists);
        model.addAttribute("bodyContent","volleyball/volleyball_match_details_stats");
        return "volleyball/master_template";
    }

    @GetMapping("/fixtures")
    public String showFixtures(Model model) {

        List<VolleyballMatch> fixtures = volleyballMatchService.listAllVolleyballMatches().stream()
                .filter(match -> match.getEndTime().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList());
        model.addAttribute("fixtures", fixtures);
        model.addAttribute("bodyContent","volleyball/volleyball_fixtures");
        return "volleyball/master_template";
    }

    @GetMapping("/results")
    public String showResults(Model model) {
        List<VolleyballMatch> results = volleyballMatchService.listAllVolleyballMatches().stream()
                .filter(match -> match.getEndTime().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList());
        //results.forEach(volleyballMatchService::updateTeamStatistics);
        model.addAttribute("results", results);
        model.addAttribute("bodyContent","volleyball/volleyball_results");
        return "volleyball/master_template";
    }

    @GetMapping("/live")
    public String showLive(Model model) {
        List<VolleyballMatch> live = volleyballMatchService.listAllVolleyballMatches().stream()
                .filter(match -> (match.getStartTime().isBefore(LocalDateTime.now()) && match.getEndTime().isAfter(LocalDateTime
                        .now())))
                .collect(Collectors.toList());
        model.addAttribute("live", live);
        model.addAttribute("bodyContent","volleyball/volleyball_live");
        return "volleyball/master_template";
    }

    @GetMapping("/add-form")
    public String addMatch(Model model) {
        List<VolleyballTeam> teams = volleyballTeamService.listAllTeams();
        model.addAttribute("teams", teams);
        model.addAttribute("bodyContent","volleyball/add_volleyball_match");
        return "volleyball/master_template";
    }

    @PostMapping("/add")
    public String saveMatch(
            @RequestParam Long homeTeamId,
            @RequestParam Long awayTeamId,
            @RequestParam int homeTeamPoints,
            @RequestParam int awayTeamPoints,
            @RequestParam String startTime) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime start = LocalDateTime.parse(startTime, formatter);

        VolleyballTeam homeTeam = volleyballTeamService.findById(homeTeamId);
        VolleyballTeam awayTeam = volleyballTeamService.findById(awayTeamId);

        volleyballMatchService.createAndAddToFixtures(homeTeam, awayTeam, homeTeamPoints, awayTeamPoints, start);
        return "redirect:/volleyball/matches";
    }

    @GetMapping("/edit-form/{id}")
    public String editMatch(@PathVariable Long id, Model model) {
        VolleyballMatch match = volleyballMatchService.findById(id);
        List<VolleyballTeam> teams = volleyballTeamService.listAllTeams();
        model.addAttribute("match", match);
        model.addAttribute("teams", teams);
        model.addAttribute("bodyContent","volleyball/edit_volleyball_match");
        return "volleyball/master_template";
    }

    @GetMapping("/edit_live/{id}")
    public String editLiveMatch(@PathVariable Long id, Model model) throws JsonProcessingException {
        VolleyballMatch match = volleyballMatchService.findById(id);

        List<VolleyballPlayer> players = match.getHomeTeam().getPlayers();
        players.addAll(match.getAwayTeam().getPlayers());
        List<VolleyballPlayerDTO> dtoPlayers= new ArrayList<>();
        for (VolleyballPlayer player : players) {
            VolleyballPlayerDTO dtoPlayer=new VolleyballPlayerDTO();
            dtoPlayer.setVolleyball_player_id(player.getVolleyball_player_id());
            dtoPlayer.setName(player.getName());
            dtoPlayer.setSurname(player.getSurname());
            dtoPlayer.setTeamId(player.getTeam().getVolleyball_team_id());
            dtoPlayers.add(dtoPlayer);
        }
        model.addAttribute("match", match);
        List<VolleyballTeam> teams = new ArrayList<>();
        teams.add(match.getHomeTeam());
        teams.add(match.getAwayTeam());
        model.addAttribute("teams", teams);
        model.addAttribute("players", players);
        model.addAttribute("dtoPlayers",dtoPlayers);
        model.addAttribute("playersHome", match.getHomeTeam().getPlayers());
        model.addAttribute("playersAway", match.getAwayTeam().getPlayers());

        model.addAttribute("bodyContent","volleyball/edit_live_volleyball_match");
        return "volleyball/master_template";
    }

    @PostMapping("/edit_live")
    public String editLiveMatchPost(@RequestParam Long playerId,
                                    @RequestParam Long volleyballMatchId,
                                    @RequestParam int points,
                                    @RequestParam int assists,
                                    @RequestParam int blocks,
                                    @RequestParam int servings,
                                    @RequestParam int currentSet) {

        VolleyballPlayer player = volleyballPlayerService.findById(playerId);
        VolleyballMatch volleyballMatch = volleyballMatchService.findById(volleyballMatchId);

        // Update player match stats
        VolleyballPlayerMatchStats existingPlayerScored = volleyballPlayerMatchStatsService.findByPlayerAndVolleyballMatch(player, volleyballMatch);
        if (existingPlayerScored != null) {
            existingPlayerScored.setServings(existingPlayerScored.getServings() + servings);
            existingPlayerScored.setBlocks(existingPlayerScored.getBlocks() + blocks);
            existingPlayerScored.setAssists(existingPlayerScored.getAssists() + assists);
            existingPlayerScored.setScoredPoints(existingPlayerScored.getScoredPoints() + points);
            volleyballPlayerMatchStatsService.save(existingPlayerScored);
        } else {
            VolleyballPlayerMatchStats newPlayerScored = new VolleyballPlayerMatchStats(player, volleyballMatch, 0, 0, 0, 0);
            newPlayerScored.setServings(servings);
            newPlayerScored.setBlocks(blocks);
            newPlayerScored.setAssists(assists);
            newPlayerScored.setScoredPoints(points);
            volleyballPlayerMatchStatsService.save(newPlayerScored);
        }

        // Update player statistics
        volleyballPlayerService.addAppearances(playerId);
        volleyballPlayerService.addAssists(playerId, assists);
        volleyballPlayerService.addPoints(playerId, points);
        volleyballPlayerService.addBlocks(playerId, blocks);
        volleyballPlayerService.addServings(playerId, servings);

        // Update team and match statistics
        VolleyballTeam team = volleyballTeamService.listAllTeams().stream()
                .filter(t -> t.getPlayers().contains(player))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Player's team not found"));

        volleyballTeamService.updateStats(team.getVolleyball_team_id());
        volleyballMatchService.updateLiveStats(volleyballMatchId, points, playerId, currentSet);

        return "redirect:/volleyball/matches";
    }

    @PostMapping("/edit")
    public String updateMatch(
            @RequestParam Long id,
            @RequestParam Long homeTeamId,
            @RequestParam Long awayTeamId,
            @RequestParam int homeTeamPoints,
            @RequestParam int awayTeamPoints,
            @RequestParam int homeTeamSet1Points,
            @RequestParam int homeTeamSet2Points,
            @RequestParam int homeTeamSet3Points,
            @RequestParam int homeTeamSet4Points,
            @RequestParam int homeTeamSet5Points,
            @RequestParam int awayTeamSet1Points,
            @RequestParam int awayTeamSet2Points,
            @RequestParam int awayTeamSet3Points,
            @RequestParam int awayTeamSet4Points,
            @RequestParam int awayTeamSet5Points,
            @RequestParam String startTime,
            @RequestParam String endTime) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime start = LocalDateTime.parse(startTime, formatter);
        LocalDateTime end = LocalDateTime.parse(endTime, formatter);

        VolleyballTeam homeTeam = volleyballTeamService.findById(homeTeamId);
        VolleyballTeam awayTeam = volleyballTeamService.findById(awayTeamId);

        volleyballMatchService.update(id, homeTeam, awayTeam, 
            homeTeamPoints, awayTeamPoints,
            homeTeamSet1Points, homeTeamSet2Points, homeTeamSet3Points, homeTeamSet4Points, homeTeamSet5Points,
            awayTeamSet1Points, awayTeamSet2Points, awayTeamSet3Points, awayTeamSet4Points, awayTeamSet5Points,
            start, end);
            
        return "redirect:/volleyball/matches";
    }

    @PostMapping("/delete/{id}")
    public String deleteMatch(@PathVariable Long id) {
        volleyballMatchService.delete(id);
        return "redirect:/volleyball/matches";
    }

    @GetMapping("/{teamId}/players")
    public List<VolleyballPlayer> getPlayersByTeam(@PathVariable Long teamId) {
        VolleyballTeam team = volleyballTeamService.findById(teamId);
        return team.getPlayers();
    }

    @GetMapping("/playoffs/init")
    public String initializePlayoffMatches() {
        volleyballMatchService.createPlayoffMatches();
        return "redirect:/volleyball/matches/playoffs"; // Redirect to the list of playoff matches
    }

    @GetMapping("/playoffs")
    public String getPlayoffMatches(Model model) {
        List<VolleyballMatch> matches = volleyballMatchService.listPlayoffMatches();
        model.addAttribute("matches", matches);
        model.addAttribute("bodyContent","volleyball/volleyball_playoff_bracket");
        return "volleyball/master_template"; // The name of your Thymeleaf template for the playoff view
    }

    @GetMapping("/playoffs/edit/{id}")
    public String editPlayoffMatch(@PathVariable Long id, Model model) {
        VolleyballMatch match = volleyballMatchService.findById(id);
        List<VolleyballTeam> teams = volleyballTeamService.listAllTeams();
        VolleyballTeam homeTeam = match.getHomeTeam();
        VolleyballTeam awayTeam = match.getAwayTeam();
        model.addAttribute("match", match);
        model.addAttribute("teams", teams);
        model.addAttribute("homeScorers", homeTeam.getPlayers());
        model.addAttribute("awayScorers", awayTeam.getPlayers());
        model.addAttribute("bodyContent", "volleyball/edit_volleyball_playoff_match");
        return "volleyball/master_template";
    }

    @PostMapping("/playoffs/edit")
    public String updatePlayoffMatchPoints(
            @RequestParam Long id,
            @RequestParam Long homeTeamId,
            @RequestParam Long awayTeamId,
            @RequestParam int homeTeamPoints,
            @RequestParam int awayTeamPoints,
            @RequestParam(value = "homeScorers", required = false) List<VolleyballPlayer> homeScorers,
            @RequestParam(value = "awayScorers", required = false) List<VolleyballPlayer> awayScorers) {

        VolleyballTeam homeTeam = volleyballTeamService.findById(homeTeamId);
        VolleyballTeam awayTeam = volleyballTeamService.findById(awayTeamId);

        volleyballMatchService.updatePlayoffMatchPoints(id, homeTeam, awayTeam, homeTeamPoints, awayTeamPoints, homeScorers, awayScorers);

        return "redirect:/volleyball/matches/playoffs";
    }

    @GetMapping("/playoffs/semi-finals_Init")
    public String initializeSemiFinalMatches() {
        volleyballMatchService.createSemiFinalMatches();
        return "redirect:/matches/playoffs"; // Redirect to view the updated playoff bracket
    }
    @GetMapping("/playoffs/finals_Init")
    public String initializeFinalMatches() {
        volleyballMatchService.createFinalMatch();
        return "redirect:/matches/playoffs"; // Redirect to view the updated playoff bracket
    }
    @PostMapping("/finish/{id}")
    public String finishMatch(@PathVariable Long id)
    {
        try {
            VolleyballMatch match = volleyballMatchService.findById(id);
            volleyballMatchService.finishMatch(id);
            //footballMatchService.updateTeamStatistics(match);
            return "redirect:/volleyball/matches/results";

        } catch (Exception e) {
            return "redirect:/matches/error";
        }
    }
}
