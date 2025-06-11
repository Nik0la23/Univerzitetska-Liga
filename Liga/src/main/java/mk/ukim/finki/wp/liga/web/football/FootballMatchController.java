package mk.ukim.finki.wp.liga.web.football;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import mk.ukim.finki.wp.liga.model.FootballMatch;
import mk.ukim.finki.wp.liga.model.FootballPlayer;
import mk.ukim.finki.wp.liga.model.FootballPlayerScored;
import mk.ukim.finki.wp.liga.model.FootballTeam;
import mk.ukim.finki.wp.liga.model.dtos.FootballPlayerDTO;
import mk.ukim.finki.wp.liga.service.football.FootballMatchService;
import mk.ukim.finki.wp.liga.service.football.FootballPlayerScoredService;
import mk.ukim.finki.wp.liga.service.football.FootballPlayerService;
import mk.ukim.finki.wp.liga.service.football.FootballTeamService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Controller
@AllArgsConstructor
@RequestMapping({"/","/matches", "/football"})
public class FootballMatchController {

    private final FootballMatchService footballMatchService;
    private final FootballTeamService footballTeamService;
    private final FootballPlayerService footballPlayerService;
    private final FootballPlayerScoredService footballPlayerScoredService;

    @GetMapping
    public String showAllMatches(Model model) {
        List<FootballMatch> matches = footballMatchService.listAllFootballMatches(); // Assume this gets all matches
        Map<LocalDate, List<FootballMatch>> groupedMatches = footballMatchService.groupMatchesByDate(matches);
        model.addAttribute("groupedMatches", groupedMatches);
        model.addAttribute("bodyContent","football_matches");
        return "master_template";
    }

    @GetMapping("/details/{id}")
    public String showMatchDetails(@PathVariable Long id, Model model) {
        FootballMatch match = footballMatchService.findById(id);
        match.setHomeTeam(footballTeamService.findById(match.getHomeTeam().getId()));
        match.setAwayTeam(footballTeamService.findById(match.getAwayTeam().getId()));
        model.addAttribute("match", match);
        model.addAttribute("bodyContent","football_match_details");
        return "master_template";
    }

    @GetMapping("/results/details/{id}")
    public String showMatchResultsDetails(@PathVariable Long id, Model model) {
        FootballMatch match = footballMatchService.findById(id);
        match.setHomeTeam(footballTeamService.findById(match.getHomeTeam().getId()));
        match.setAwayTeam(footballTeamService.findById(match.getAwayTeam().getId()));
        model.addAttribute("match", match);
        model.addAttribute("bodyContent","football_match_results_details");
        return "master_template";
    }

    @GetMapping("/results/details/stats/{id}")
    public String showMatchDetailsAndStats(@PathVariable Long id, Model model) {
        FootballMatch match = footballMatchService.findById(id);
        match.setHomeTeam(footballTeamService.findById(match.getHomeTeam().getId()));
        match.setAwayTeam(footballTeamService.findById(match.getAwayTeam().getId()));
        FootballTeam homeTeam = match.getHomeTeam();
        FootballTeam awayTeam = match.getAwayTeam();

        List<FootballPlayer> homePlayers = homeTeam.getPlayers();
        List<FootballPlayer> awayPlayers = awayTeam.getPlayers();

        int homeGoals = 0;
        int homeSaves = 0;
        int homeAssists = 0;

        for(FootballPlayer player : homePlayers){
            homeGoals += player.getGoals();
            homeSaves += player.getSaves();
            homeAssists += player.getAssists();
        }

        int awayGoals = 0;
        int awaySaves = 0;
        int awayAssists = 0;

        for(FootballPlayer player : awayPlayers){
            awayGoals += player.getGoals();
            awaySaves += player.getSaves();
            awayAssists += player.getAssists();
        }

        model.addAttribute("match", match);
        model.addAttribute("homeTeam",homeTeam);
        model.addAttribute("awayTeam",awayTeam);
        model.addAttribute("homeGoals",homeGoals);
        model.addAttribute("homeSaves",homeSaves);
        model.addAttribute("homeAssists",homeAssists);
        model.addAttribute("awayGoals",awayGoals);
        model.addAttribute("awaySaves",awaySaves);
        model.addAttribute("awayAssists",awayAssists);
        model.addAttribute("bodyContent","football_match_details_stats");
        return "master_template";
    }

    @GetMapping("/fixtures")
    public String showFixtures(Model model) {

        List<FootballMatch> fixtures = footballMatchService.listAllFootballMatches().stream()
                .filter(match -> match.getEndTime().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList());
        model.addAttribute("fixtures", fixtures);
        model.addAttribute("bodyContent","football_fixtures");
        return "master_template";
    }

    @GetMapping("/results")
    public String showResults(Model model) {
        List<FootballMatch> results = footballMatchService.listAllFootballMatches().stream()
                .filter(match -> match.getEndTime().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList());
        //results.forEach(footballMatchService::updateTeamStatistics);
        model.addAttribute("results", results);
        model.addAttribute("bodyContent","football_results");
        return "master_template";
    }

    @GetMapping("/live")
    public String showLive(Model model) {
        List<FootballMatch> live = footballMatchService.listAllFootballMatches().stream()
                .filter(match -> (match.getStartTime().isBefore(LocalDateTime.now()) && match.getEndTime().isAfter(LocalDateTime
                        .now())))
                .collect(Collectors.toList());
        model.addAttribute("live", live);
        model.addAttribute("bodyContent","football_live");
        return "master_template";
    }

    @GetMapping("/add-form")
    public String addMatch(Model model) {
        List<FootballTeam> teams = footballTeamService.listAllTeams();
        model.addAttribute("teams", teams);
        model.addAttribute("bodyContent","add_football_match");
        return "master_template";
    }

    @PostMapping("/add")
    public String saveMatch(
            @RequestParam Long homeTeamId,
            @RequestParam Long awayTeamId,
            @RequestParam int homeTeamPoints,
            @RequestParam int awayTeamPoints,
            @RequestParam String startTime
    ) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime start = LocalDateTime.parse(startTime, formatter);

        FootballTeam homeTeam = footballTeamService.findById(homeTeamId);
        FootballTeam awayTeam = footballTeamService.findById(awayTeamId);

        footballMatchService.createAndAddToFixtures(homeTeam, awayTeam, homeTeamPoints, awayTeamPoints, start);
        return "redirect:/matches";
    }

    @GetMapping("/edit-form/{id}")
    public String editMatch(@PathVariable Long id, Model model) {
        FootballMatch match = footballMatchService.findById(id);
        List<FootballTeam> teams = footballTeamService.listAllTeams();
        model.addAttribute("match", match);
        model.addAttribute("teams", teams);
        model.addAttribute("bodyContent","edit_football_match");
        return "master_template";
    }

    @GetMapping("/edit_live/{id}")
    public String editLiveMatch(@PathVariable Long id, Model model){
        FootballMatch match = footballMatchService.findById(id);

        List<FootballPlayer> players = match.getHomeTeam().getPlayers();
        players.addAll(match.getAwayTeam().getPlayers());
        System.out.println(players);
        List<FootballPlayerDTO> dtoPlayers= new ArrayList<>();
        for (FootballPlayer player : players) {
            FootballPlayerDTO dtoPlayer=new FootballPlayerDTO();
            dtoPlayer.setFootball_player_id(player.getFootball_player_id());
            dtoPlayer.setName(player.getName());
            dtoPlayer.setSurname(player.getSurname());
            dtoPlayer.setTeamId(player.getTeam().getId());
            dtoPlayers.add(dtoPlayer);
        }
        model.addAttribute("match", match);
        List<FootballTeam> teams = new ArrayList<>();
        teams.add(match.getHomeTeam());
        teams.add(match.getAwayTeam());
        model.addAttribute("teams", teams);
        model.addAttribute("players", players);
        model.addAttribute("dtoPlayers",dtoPlayers);
        model.addAttribute("playersHome", match.getHomeTeam().getPlayers());
        model.addAttribute("playersAway", match.getAwayTeam().getPlayers());
        model.addAttribute("bodyContent","edit_live_football_match");
        return "master_template";
    }

    @PostMapping("/edit_live")
    public String editLiveMatchPost(@RequestParam Long playerId,
//                               @RequestParam LocalDateTime timeScored,
                               @RequestParam Long footballMatchId,
                               @RequestParam int goalsScored,
                               @RequestParam int assistsScored,
                               @RequestParam int saves) {


        FootballPlayer player = footballPlayerService.findById(playerId);
        FootballMatch footballMatch = footballMatchService.findById(footballMatchId);

        FootballPlayerScored existingPlayerScored = footballPlayerScoredService.findByPlayerAndMatch(player, footballMatch);
        if (existingPlayerScored != null) {
            existingPlayerScored.setSaves(existingPlayerScored.getSaves() + saves);
            existingPlayerScored.setGoalsScored(existingPlayerScored.getGoalsScored() + goalsScored);
            existingPlayerScored.setAssistsScored(existingPlayerScored.getAssistsScored() + assistsScored);
            footballPlayerScoredService.save(existingPlayerScored);
        } else {
            FootballPlayerScored newPlayerScored = new FootballPlayerScored(player, footballMatch);
            newPlayerScored.setSaves(saves);
            newPlayerScored.setGoalsScored(goalsScored);
            newPlayerScored.setAssistsScored(assistsScored);
            footballPlayerScoredService.save(newPlayerScored);
        }

        footballPlayerService.addAppearances(playerId);
        footballPlayerService.addAssists(playerId, assistsScored);
        footballPlayerService.addGoals(playerId, goalsScored);
        footballPlayerService.addSaves(playerId, saves);

        FootballTeam team = footballTeamService.listAllTeams().stream().filter(t -> t.getPlayers().contains(player)).findFirst().get();
        footballTeamService.updateStats(team.getId());
        footballMatchService.updateLiveStats(footballMatchId, goalsScored, playerId);

        return "redirect:/matches";
    }


    @PostMapping("/edit")
    public String updateMatch(
            @RequestParam Long id,
            @RequestParam Long homeTeamId,
            @RequestParam Long awayTeamId,
            @RequestParam int homeTeamPoints,
            @RequestParam int awayTeamPoints,
            @RequestParam String startTime
            ) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime start = LocalDateTime.parse(startTime, formatter);

        FootballTeam homeTeam = footballTeamService.findById(homeTeamId);
        FootballTeam awayTeam = footballTeamService.findById(awayTeamId);

        footballMatchService.update(id, homeTeam, awayTeam, homeTeamPoints, awayTeamPoints, start);
        return "redirect:/matches";
    }

    @PostMapping("/delete/{id}")
    public String deleteMatch(@PathVariable Long id) {
        footballMatchService.delete(id);
        return "redirect:/matches";
    }

    @GetMapping("/{teamId}/players")
    public List<FootballPlayer> getPlayersByTeam(@PathVariable Long teamId) {
        FootballTeam team = footballTeamService.findById(teamId);
        return team.getPlayers();
    }
    @GetMapping("/playoffs/init")
    public String initializePlayoffMatches(Model model,RedirectAttributes redirectAttributes) {
//        boolean allTeamsPlayedExactly4Matches = footballMatchService.allTeamsHavePlayedFourMatches();
//        model.addAttribute("allTeamsPlayedExactly4Matches", allTeamsPlayedExactly4Matches);
        try {
            footballMatchService.createPlayoffMatches();
            return "redirect:/matches/playoffs"; // Redirect to the list of playoff matches
        } catch (RuntimeException e) {
            // Handle the case where not all teams have played 4 matches
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/matches/playoffs"; // Redirect back to the playoff matches page with an error message
        }
    }

    @GetMapping("/playoffs")
    public String getPlayoffMatches(Model model) {
        List<FootballMatch> matches = footballMatchService.listPlayoffMatches();
        model.addAttribute("matches", matches);
       // boolean allTeamsPlayedExactly4Matches = footballMatchService.allTeamsHavePlayedFourMatches();
       // model.addAttribute("allTeamsPlayedExactly4Matches", allTeamsPlayedExactly4Matches);
        model.addAttribute("bodyContent","playoff_bracket");
        return "master_template";
    }

    @GetMapping("/playoffs/edit/{id}")
    public String editPlayoffMatch(@PathVariable Long id, Model model) {
        FootballMatch match = footballMatchService.findById(id);
        List<FootballTeam> teams = footballTeamService.listAllTeams();
        FootballTeam homeTeam = match.getHomeTeam();
        FootballTeam awayTeam = match.getAwayTeam();
        model.addAttribute("match", match);
        model.addAttribute("teams", teams);
        model.addAttribute("homeScorers", homeTeam.getPlayers());
        model.addAttribute("awayScorers", awayTeam.getPlayers());
        model.addAttribute("bodyContent","edit_playoff_match");
        return "master_template";
    }
    @PostMapping("/playoffs/edit")
    public String updatePlayoffMatchPoints(
            @RequestParam Long id,
            @RequestParam Long homeTeamId,
            @RequestParam Long awayTeamId,
            @RequestParam int homeTeamPoints,
            @RequestParam int awayTeamPoints,
            @RequestParam(value = "homeScorers", required = false) List<FootballPlayer> homeScorers,
            @RequestParam(value = "awayScorers", required = false) List<FootballPlayer> awayScorers) {

        FootballTeam homeTeam = footballTeamService.findById(homeTeamId);
        FootballTeam awayTeam = footballTeamService.findById(awayTeamId);

        footballMatchService.updatePlayoffMatchPoints(id, homeTeam, awayTeam, homeTeamPoints, awayTeamPoints, homeScorers, awayScorers);

        return "redirect:/matches/playoffs";
    }
    @GetMapping("/playoffs/semi-finals_Init")
    public String initializeSemiFinalMatches() {
        footballMatchService.createSemiFinalMatches();
        return "redirect:/matches/playoffs";
    }
    @GetMapping("/playoffs/finals_Init")
    public String initializeFinalMatches() {
        footballMatchService.createFinalMatch();
        return "redirect:/matches/playoffs";
    }
    @PostMapping("/finish/{id}")
    public String finishMatch(@PathVariable Long id) {
        try {
            FootballMatch match = footballMatchService.findById(id);
            footballMatchService.finishMatch(id);
            //footballMatchService.updateTeamStatistics(match);
            return "redirect:/matches/results";

        } catch (Exception e) {
            return "redirect:/matches/error";
        }
    }
//    @PostMapping("/details/{id}/process-stats")
//    public String processMatchStats(@PathVariable("id") Long matchId, Model model) {
//        footballMatchService.processMatchStats(matchId);
//        return "redirect:/matches/results"; // Redirect back to the results page after processing
//    }

}



