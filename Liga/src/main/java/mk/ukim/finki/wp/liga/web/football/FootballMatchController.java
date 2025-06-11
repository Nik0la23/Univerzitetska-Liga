package mk.ukim.finki.wp.liga.web.football;

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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@AllArgsConstructor
@RequestMapping({"/football/matches","/football"})
public class FootballMatchController {

    private final FootballMatchService footballMatchService;
    private final FootballTeamService footballTeamService;
    private final FootballPlayerService footballPlayerService;
    private final FootballPlayerScoredService footballPlayerScoredService;

    @GetMapping
    public String showAllMatches(Model model) {
        List<FootballMatch> matches = footballMatchService.listAllFootballMatches();
        Map<LocalDate, List<FootballMatch>> groupedMatches = footballMatchService.groupMatchesByDate(matches);
        model.addAttribute("groupedMatches", groupedMatches);
        model.addAttribute("bodyContent", "football/football_matches");
        return "/football/master_template";
    }

    @GetMapping("/details/{id}")
    public String showMatchDetails(@PathVariable Long id, Model model) {
        FootballMatch match = footballMatchService.findById(id);
        match.setHomeTeam(footballTeamService.findById(match.getHomeTeam().getId()));
        match.setAwayTeam(footballTeamService.findById(match.getAwayTeam().getId()));
        model.addAttribute("match", match);
        model.addAttribute("bodyContent", "football/football_match_details");
        return "/football/master_template";
    }

    @GetMapping("/results/details/{id}")
    public String showMatchResultsDetails(@PathVariable Long id, Model model) {
        FootballMatch match = footballMatchService.findById(id);
        match.setHomeTeam(footballTeamService.findById(match.getHomeTeam().getId()));
        match.setAwayTeam(footballTeamService.findById(match.getAwayTeam().getId()));
        model.addAttribute("match", match);
        model.addAttribute("bodyContent", "football/football_match_results_details");
        return "/football/master_template";
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
        int homeGoals = 0, homeSaves = 0, homeAssists = 0;
        for (FootballPlayer player : homePlayers) {
            homeGoals += player.getGoals();
            homeSaves += player.getSaves();
            homeAssists += player.getAssists();
        }
        int awayGoals = 0, awaySaves = 0, awayAssists = 0;
        for (FootballPlayer player : awayPlayers) {
            awayGoals += player.getGoals();
            awaySaves += player.getSaves();
            awayAssists += player.getAssists();
        }
        model.addAttribute("match", match);
        model.addAttribute("homeTeam", homeTeam);
        model.addAttribute("awayTeam", awayTeam);
        model.addAttribute("homeGoals", homeGoals);
        model.addAttribute("homeSaves", homeSaves);
        model.addAttribute("homeAssists", homeAssists);
        model.addAttribute("awayGoals", awayGoals);
        model.addAttribute("awaySaves", awaySaves);
        model.addAttribute("awayAssists", awayAssists);
        model.addAttribute("bodyContent", "football/football_match_details_stats");
        return "/football/master_template";
    }

    @GetMapping("/fixtures")
    public String showFixtures(Model model) {
        List<FootballMatch> fixtures = footballMatchService.listAllFootballMatches().stream()
                .filter(match -> match.getEndTime().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList());
        model.addAttribute("fixtures", fixtures);
        model.addAttribute("bodyContent", "football_fixtures");
        return "master_template";
    }

    @GetMapping("/results")
    public String showResults(Model model) {
        List<FootballMatch> results = footballMatchService.listAllFootballMatches().stream()
                .filter(match -> match.getEndTime().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList());
        model.addAttribute("results", results);
        model.addAttribute("bodyContent", "football_results");
        return "master_template";
    }

    @GetMapping("/live")
    public String showLive(Model model) {
        List<FootballMatch> live = footballMatchService.listAllFootballMatches().stream()
                .filter(match -> match.getStartTime().isBefore(LocalDateTime.now()) && match.getEndTime().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList());
        model.addAttribute("live", live);
        model.addAttribute("bodyContent", "football_live");
        return "master_template";
    }

    @GetMapping("/add-form")
    public String showAddMatchForm(Model model) {
        List<FootballTeam> teams = footballTeamService.listAllTeams();
        model.addAttribute("teams", teams);
        model.addAttribute("bodyContent", "football/add_football_match");
        return "/football/master_template";
    }

    @PostMapping("/add")
    public String saveMatch(@RequestParam Long homeTeamId,
                           @RequestParam Long awayTeamId,
                           @RequestParam int homeTeamPoints,
                           @RequestParam int awayTeamPoints,
                           @RequestParam String startTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime start = LocalDateTime.parse(startTime, formatter);
        FootballTeam homeTeam = footballTeamService.findById(homeTeamId);
        FootballTeam awayTeam = footballTeamService.findById(awayTeamId);
        footballMatchService.createAndAddToFixtures(homeTeam, awayTeam, homeTeamPoints, awayTeamPoints, start);
        return "redirect:/football/matches";
    }

    @GetMapping("/edit-form/{id}")
    public String editMatch(@PathVariable Long id, Model model) {
        FootballMatch match = footballMatchService.findById(id);
        List<FootballTeam> teams = footballTeamService.listAllTeams();
        model.addAttribute("match", match);
        model.addAttribute("teams", teams);
        model.addAttribute("bodyContent", "football/edit_football_match");
        return "/football/master_template";
    }

    @GetMapping("/edit_live/{id}")
    public String editLiveMatch(@PathVariable Long id, Model model) {
        FootballMatch match = footballMatchService.findById(id);
        List<FootballPlayer> players = match.getHomeTeam().getPlayers();
        players.addAll(match.getAwayTeam().getPlayers());
        List<FootballPlayerDTO> dtoPlayers = new ArrayList<>();
        for (FootballPlayer player : players) {
            FootballPlayerDTO dtoPlayer = new FootballPlayerDTO();
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
        model.addAttribute("dtoPlayers", dtoPlayers);
        model.addAttribute("playersHome", match.getHomeTeam().getPlayers());
        model.addAttribute("playersAway", match.getAwayTeam().getPlayers());
        model.addAttribute("bodyContent", "football/edit_live_football_match");
        return "/football/master_template";
    }

    @PostMapping("/edit")
    public String updateMatch(@RequestParam Long id,
                             @RequestParam Long homeTeamId,
                             @RequestParam Long awayTeamId,
                             @RequestParam int homeTeamPoints,
                             @RequestParam int awayTeamPoints,
                             @RequestParam String startTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime start = LocalDateTime.parse(startTime, formatter);
        FootballTeam homeTeam = footballTeamService.findById(homeTeamId);
        FootballTeam awayTeam = footballTeamService.findById(awayTeamId);
        footballMatchService.update(id, homeTeam, awayTeam, homeTeamPoints, awayTeamPoints, start);
        return "redirect:/football/matches";
    }

    @PostMapping("/delete/{id}")
    public String deleteMatch(@PathVariable Long id) {
        footballMatchService.delete(id);
        return "redirect:/football/matches";
    }

    @GetMapping("/{teamId}/players")
    public List<FootballPlayer> getPlayersByTeam(@PathVariable Long teamId) {
        FootballTeam team = footballTeamService.findById(teamId);
        return team.getPlayers();
    }

    @GetMapping("/playoffs/init")
    public String initializePlayoffMatches(Model model, RedirectAttributes redirectAttributes) {
        try {
            footballMatchService.createPlayoffMatches();
            return "redirect:/football/matches/playoffs";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/football/matches/playoffs";
        }
    }

    @GetMapping("/playoffs")
    public String getPlayoffMatches(Model model) {
        List<FootballMatch> matches = footballMatchService.listPlayoffMatches();
        model.addAttribute("matches", matches);
        model.addAttribute("bodyContent", "football/playoff_bracket");
        return "/football/master_template";
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
        model.addAttribute("bodyContent", "football/edit_playoff_match");
        return "/football/master_template";
    }

    @PostMapping("/playoffs/edit")
    public String updatePlayoffMatchPoints(@RequestParam Long id,
                                          @RequestParam Long homeTeamId,
                                          @RequestParam Long awayTeamId,
                                          @RequestParam int homeTeamPoints,
                                          @RequestParam int awayTeamPoints,
                                          @RequestParam(value = "homeScorers", required = false) List<FootballPlayer> homeScorers,
                                          @RequestParam(value = "awayScorers", required = false) List<FootballPlayer> awayScorers) {
        FootballTeam homeTeam = footballTeamService.findById(homeTeamId);
        FootballTeam awayTeam = footballTeamService.findById(awayTeamId);
        footballMatchService.updatePlayoffMatchPoints(id, homeTeam, awayTeam, homeTeamPoints, awayTeamPoints, homeScorers, awayScorers);
        return "redirect:/football/matches/playoffs";
    }

    @GetMapping("/playoffs/semi-finals_Init")
    public String initializeSemiFinalMatches() {
        footballMatchService.createSemiFinalMatches();
        return "redirect:/football/matches/playoffs";
    }

    @GetMapping("/playoffs/finals_Init")
    public String initializeFinalMatches() {
        footballMatchService.createFinalMatch();
        return "redirect:/football/matches/playoffs";
    }

    @PostMapping("/finish/{id}")
    public String finishMatch(@PathVariable Long id) {
        try {
            footballMatchService.finishMatch(id);
            return "redirect:/football/matches/results";
        } catch (Exception e) {
            return "redirect:/football/matches/error";
        }
    }
}