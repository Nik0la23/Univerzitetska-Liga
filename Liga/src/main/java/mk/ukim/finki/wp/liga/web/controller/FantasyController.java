package mk.ukim.finki.wp.liga.web.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import mk.ukim.finki.wp.liga.model.User;
import mk.ukim.finki.wp.liga.model.fantasy.FantasySport;
import mk.ukim.finki.wp.liga.model.fantasy.FantasyTeam;
import mk.ukim.finki.wp.liga.service.basketball.BasketballPlayerService;
import mk.ukim.finki.wp.liga.service.basketball.BasketballTeamService;
import mk.ukim.finki.wp.liga.service.football.FootballPlayerService;
import mk.ukim.finki.wp.liga.service.football.FootballTeamService;
import mk.ukim.finki.wp.liga.service.volleyball.VolleyballPlayerService;
import mk.ukim.finki.wp.liga.service.volleyball.VolleyballTeamService;
import mk.ukim.finki.wp.liga.service.fantasy.FantasyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@Controller
@RequestMapping("/fantasy")
@RequiredArgsConstructor
public class FantasyController {

    private final FantasyService fantasyService;
    private final FootballPlayerService footballPlayerService;
    private final FootballTeamService footballTeamService;
    private final BasketballPlayerService basketballPlayerService;
    private final BasketballTeamService basketballTeamService;
    private final VolleyballPlayerService volleyballPlayerService;
    private final VolleyballTeamService volleyballTeamService;

    @GetMapping
    public String fantasyHome(@RequestParam(defaultValue = "FOOTBALL") FantasySport sport, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/auth/login";

        model.addAttribute("sport", sport);
        fantasyService.getTeamFor(user, sport).ifPresent(team -> {
            model.addAttribute("team", team);
            model.addAttribute("boughtPlayers", fantasyService.getBoughtPlayers(team));
            model.addAttribute("assignedPlayers", fantasyService.getAssignedPlayers(team));
        });

        // Always initialize boughtPlayerIds as empty list if no team exists
        java.util.List<Long> emptyList = new java.util.ArrayList<>();
        model.addAttribute("boughtPlayerIds", emptyList);
        
        fantasyService.getTeamFor(user, sport).ifPresent(team -> {
            // Add bought player IDs to check in template
            switch (sport) {
                case FOOTBALL -> model.addAttribute("boughtPlayerIds", 
                    fantasyService.getBoughtPlayers(team).stream()
                        .filter(fp -> fp.getFootballPlayer() != null)
                        .map(fp -> fp.getFootballPlayer().getFootball_player_id())
                        .collect(Collectors.toList()));
                case BASKETBALL -> model.addAttribute("boughtPlayerIds",
                    fantasyService.getBoughtPlayers(team).stream()
                        .filter(fp -> fp.getBasketballPlayer() != null)
                        .map(fp -> fp.getBasketballPlayer().getBasketball_player_id())
                        .collect(Collectors.toList()));
                case VOLLEYBALL -> model.addAttribute("boughtPlayerIds",
                    fantasyService.getBoughtPlayers(team).stream()
                        .filter(fp -> fp.getVolleyballPlayer() != null)
                        .map(fp -> fp.getVolleyballPlayer().getVolleyball_player_id())
                        .collect(Collectors.toList()));
            }
        });

        switch (sport) {
            case FOOTBALL -> {
                model.addAttribute("players", footballPlayerService.listAllPlayers());
                model.addAttribute("teams", footballTeamService.listAllTeams());
                model.addAttribute("bodyContent", "fantasy/fantasy_football");
            }
            case BASKETBALL -> {
                model.addAttribute("players", basketballPlayerService.listAllPlayers());
                model.addAttribute("teams", basketballTeamService.listAllTeams());
                model.addAttribute("bodyContent", "fantasy/fantasy_basketball");
            }
            case VOLLEYBALL -> {
                model.addAttribute("players", volleyballPlayerService.listAllPlayers());
                model.addAttribute("teams", volleyballTeamService.listAllTeams());
                model.addAttribute("bodyContent", "fantasy/fantasy_volleyball");
            }
        }
        return "master_template";
    }

    @PostMapping("/create")
    public String createTeam(@RequestParam(required = false) String sport, 
                           @RequestParam(required = false) String teamName, 
                           HttpSession session, Model model) {
        
        System.out.println("DEBUG: Received parameters - sport: " + sport + ", teamName: " + teamName);
        
        // Check if parameters are present
        if (sport == null || sport.trim().isEmpty()) {
            System.err.println("ERROR: Sport parameter is missing or empty");
            return "redirect:/fantasy?error=Sport parameter is required";
        }
        
        if (teamName == null || teamName.trim().isEmpty()) {
            System.err.println("ERROR: Team name parameter is missing or empty");
            return "redirect:/fantasy?error=Team name is required";
        }
        
        User user = (User) session.getAttribute("user");
        if (user == null) {
            System.err.println("No user in session - redirecting to login");
            return "redirect:/auth/login";
        }
        
        // Convert string to enum
        FantasySport fantasySport;
        try {
            fantasySport = FantasySport.valueOf(sport.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid sport parameter: " + sport);
            return "redirect:/fantasy?error=Invalid sport";
        }
        
        System.out.println("Creating fantasy team - User: " + user.getName() + ", Sport: " + fantasySport + ", Team: " + teamName);
        
        try {
            FantasyTeam createdTeam = fantasyService.createTeam(user, fantasySport, teamName);
            System.out.println("Successfully created fantasy team with ID: " + createdTeam.getId());
        } catch (Exception e) {
            // Handle error - could add flash attribute for error message
            System.err.println("Error creating fantasy team: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/fantasy?sport=" + fantasySport + "&error=" + e.getMessage();
        }
        return "redirect:/fantasy?sport=" + fantasySport;
    }

    @GetMapping("/football")
    public String fantasyFootball(HttpSession session, Model model) {
        return fantasyHome(FantasySport.FOOTBALL, session, model);
    }

    @GetMapping("/basketball")
    public String fantasyBasketball(HttpSession session, Model model) {
        return fantasyHome(FantasySport.BASKETBALL, session, model);
    }

    @GetMapping("/volleyball")
    public String fantasyVolleyball(HttpSession session, Model model) {
        return fantasyHome(FantasySport.VOLLEYBALL, session, model);
    }

    @PostMapping("/buy/football/{playerId}")
    public String buyFootballPlayer(@PathVariable Long playerId, HttpSession session, 
                                   org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/auth/login";
        
        try {
            fantasyService.getTeamFor(user, FantasySport.FOOTBALL).ifPresent(team -> 
                fantasyService.buyFootballPlayer(team, playerId));
            redirectAttributes.addFlashAttribute("successMessage", "Player bought successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/fantasy/football";
    }

    @PostMapping("/buy/basketball/{playerId}")
    public String buyBasketballPlayer(@PathVariable Long playerId, HttpSession session,
                                     org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/auth/login";
        
        try {
            fantasyService.getTeamFor(user, FantasySport.BASKETBALL).ifPresent(team -> 
                fantasyService.buyBasketballPlayer(team, playerId));
            redirectAttributes.addFlashAttribute("successMessage", "Player bought successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/fantasy/basketball";
    }

    @PostMapping("/buy/volleyball/{playerId}")
    public String buyVolleyballPlayer(@PathVariable Long playerId, HttpSession session,
                                     org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/auth/login";
        
        try {
            fantasyService.getTeamFor(user, FantasySport.VOLLEYBALL).ifPresent(team -> 
                fantasyService.buyVolleyballPlayer(team, playerId));
            redirectAttributes.addFlashAttribute("successMessage", "Player bought successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/fantasy/volleyball";
    }

    @PostMapping("/assign/{fantasyPlayerId}")
    public String assignPlayer(@PathVariable Long fantasyPlayerId, @RequestParam String position, 
                              @RequestParam FantasySport sport, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/auth/login";
        
        try {
            fantasyService.getTeamFor(user, sport).ifPresent(team -> 
                fantasyService.assignPlayerToPosition(team, fantasyPlayerId, position));
        } catch (RuntimeException e) {
            // Handle error
        }
        return "redirect:/fantasy/" + sport.toString().toLowerCase();
    }
    
    // Admin endpoint to manually calculate fantasy points for a specific match
    @PostMapping("/admin/calculate-points/{matchId}")
    public String calculateFantasyPoints(@PathVariable Long matchId, 
                                       org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        try {
            fantasyService.calculateAndAwardFantasyPoints(matchId);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Fantasy points calculated successfully for match " + matchId);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error calculating fantasy points: " + e.getMessage());
        }
        return "redirect:/fantasy/football";
    }
    
    // Admin endpoint to manually calculate basketball fantasy points
    @PostMapping("/admin/calculate-basketball-points/{matchId}")
    public String calculateBasketballFantasyPoints(@PathVariable Long matchId, 
                                                  org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        try {
            fantasyService.calculateAndAwardBasketballFantasyPoints(matchId);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Basketball fantasy points calculated successfully for match " + matchId);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error calculating basketball fantasy points: " + e.getMessage());
        }
        return "redirect:/fantasy/basketball";
    }
    
    // Admin endpoint to manually calculate volleyball fantasy points
    @PostMapping("/admin/calculate-volleyball-points/{matchId}")
    public String calculateVolleyballFantasyPoints(@PathVariable Long matchId, 
                                                   org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        try {
            fantasyService.calculateAndAwardVolleyballFantasyPoints(matchId);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Volleyball fantasy points calculated successfully for match " + matchId);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error calculating volleyball fantasy points: " + e.getMessage());
        }
        return "redirect:/fantasy/volleyball";
    }
}


