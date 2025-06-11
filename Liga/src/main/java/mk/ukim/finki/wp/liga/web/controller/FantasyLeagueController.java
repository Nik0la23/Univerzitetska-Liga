package mk.ukim.finki.wp.liga.web.controller;

import lombok.AllArgsConstructor;
import mk.ukim.finki.wp.liga.model.FantasyPlayer;
import mk.ukim.finki.wp.liga.model.FantasyTeam;
import mk.ukim.finki.wp.liga.model.FootballTeam;
import mk.ukim.finki.wp.liga.model.User;
import mk.ukim.finki.wp.liga.service.fantasy.FantasyPlayerService;
import mk.ukim.finki.wp.liga.service.fantasy.FantasyTeamService;
import mk.ukim.finki.wp.liga.service.football.FootballTeamService;
import mk.ukim.finki.wp.liga.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/fantasy")
@AllArgsConstructor
public class FantasyLeagueController {

    private final FantasyTeamService fantasyTeamService;
    private final FantasyPlayerService fantasyPlayerService;
    private final FootballTeamService footballTeamService;
    private final UserService userService;

    @GetMapping
    public String getFantasyLeaguePage(Model model, Authentication authentication) {
        // Get username from authentication
        String username = authentication.getName();
        
        // Get the actual User entity
        User currentUser = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Get or create user's fantasy team
        FantasyTeam userTeam;
        try {
            userTeam = fantasyTeamService.findByOwner(currentUser);
        } catch (RuntimeException e) {
            // Create new team for first-time users
            userTeam = fantasyTeamService.create("Team " + currentUser.getUsername(), currentUser, "4-4-2");
        }
        
        // Get all available players
        List<FantasyPlayer> availablePlayers = fantasyPlayerService.findAllAvailablePlayers();
        
        // Get all football teams for filtering
        List<FootballTeam> footballTeams = footballTeamService.listAllTeams();
        
        // Get user's owned players
        List<FantasyPlayer> ownedPlayers = fantasyPlayerService.findAllByTeam(userTeam);
        
        model.addAttribute("userTeam", userTeam);
        model.addAttribute("availablePlayers", availablePlayers);
        model.addAttribute("footballTeams", footballTeams);
        model.addAttribute("ownedPlayers", ownedPlayers);
        model.addAttribute("positions", List.of("GK", "DEF", "MID", "FWD"));
        
        return "fantasy/main";
    }

    @GetMapping("/players")
    @ResponseBody
    public List<FantasyPlayer> getFilteredPlayers(
            @RequestParam(required = false) Long teamId,
            @RequestParam(required = false) String position) {
        
        List<FantasyPlayer> players = fantasyPlayerService.findAll();
        
        if (teamId != null) {
            players = players.stream()
                    .filter(p -> p.getTeam() != null && p.getTeam().getId().equals(teamId))
                    .toList();
        }
        
        if (position != null && !position.isEmpty()) {
            players = players.stream()
                    .filter(p -> p.getPosition().equals(position))
                    .toList();
        }
        
        return players;
    }

    @PostMapping("/buy/{playerId}")
    public ResponseEntity<?> buyPlayer(
            @PathVariable Long playerId,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            User currentUser = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            FantasyTeam userTeam = fantasyTeamService.findByOwner(currentUser);
            return ResponseEntity.ok(fantasyTeamService.addPlayer(userTeam.getId(), playerId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/sell/{playerId}")
    public ResponseEntity<?> sellPlayer(
            @PathVariable Long playerId,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            User currentUser = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            FantasyTeam userTeam = fantasyTeamService.findByOwner(currentUser);
            return ResponseEntity.ok(fantasyTeamService.removePlayer(userTeam.getId(), playerId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/formation")
    public ResponseEntity<?> updateFormation(
            @RequestParam String formation,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            User currentUser = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            FantasyTeam userTeam = fantasyTeamService.findByOwner(currentUser);
            return ResponseEntity.ok(fantasyTeamService.update(userTeam.getId(), userTeam.getTeamName(), formation));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/leaderboard")
    public String getLeaderboard(Model model) {
        List<FantasyTeam> leaderboard = fantasyTeamService.getLeaderboard();
        model.addAttribute("leaderboard", leaderboard);
        return "fantasy/leaderboard";
    }
} 