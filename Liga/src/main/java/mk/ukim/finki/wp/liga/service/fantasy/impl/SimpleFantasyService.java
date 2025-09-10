package mk.ukim.finki.wp.liga.service.fantasy.impl;

import lombok.RequiredArgsConstructor;
import mk.ukim.finki.wp.liga.model.BasketballPlayer;
import mk.ukim.finki.wp.liga.model.BasketballMatch;
import mk.ukim.finki.wp.liga.model.BasketballPlayerMatchStats;
import mk.ukim.finki.wp.liga.model.FootballPlayer;
import mk.ukim.finki.wp.liga.model.FootballMatch;
import mk.ukim.finki.wp.liga.model.FootballPlayerScored;
import mk.ukim.finki.wp.liga.model.User;
import mk.ukim.finki.wp.liga.model.VolleyballPlayer;
import mk.ukim.finki.wp.liga.model.VolleyballMatch;
import mk.ukim.finki.wp.liga.model.VolleyballPlayerMatchStats;
import mk.ukim.finki.wp.liga.model.fantasy.FantasyPlayer;
import mk.ukim.finki.wp.liga.model.fantasy.FantasySport;
import mk.ukim.finki.wp.liga.model.fantasy.FantasyTeam;
import mk.ukim.finki.wp.liga.repository.basketball.BasketballMatchRepository;
import mk.ukim.finki.wp.liga.repository.basketball.BasketballPlayerMatchStatsRepository;
import mk.ukim.finki.wp.liga.repository.basketball.BasketballPlayerRepository;
import mk.ukim.finki.wp.liga.repository.fantasy.FantasyPlayerRepository;
import mk.ukim.finki.wp.liga.repository.fantasy.FantasyTeamRepository;
import mk.ukim.finki.wp.liga.repository.football.FootballMatchRepository;
import mk.ukim.finki.wp.liga.repository.football.FootballPlayerRepository;
import mk.ukim.finki.wp.liga.repository.football.FootballPlayerScoredRepository;
import mk.ukim.finki.wp.liga.repository.volleyball.VolleyballMatchRepository;
import mk.ukim.finki.wp.liga.repository.volleyball.VolleyballPlayerMatchStatsRepository;
import mk.ukim.finki.wp.liga.repository.volleyball.VolleyballPlayerRepository;
import mk.ukim.finki.wp.liga.repository.UserRepository;
import mk.ukim.finki.wp.liga.service.fantasy.FantasyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SimpleFantasyService implements FantasyService {
    private final FantasyTeamRepository fantasyTeamRepository;
    private final FantasyPlayerRepository fantasyPlayerRepository;
    private final FootballPlayerRepository footballPlayerRepository;
    private final BasketballPlayerRepository basketballPlayerRepository;
    private final VolleyballPlayerRepository volleyballPlayerRepository;
    private final UserRepository userRepository;
    private final FootballMatchRepository footballMatchRepository;
    private final FootballPlayerScoredRepository footballPlayerScoredRepository;
    private final BasketballMatchRepository basketballMatchRepository;
    private final BasketballPlayerMatchStatsRepository basketballPlayerMatchStatsRepository;
    private final VolleyballMatchRepository volleyballMatchRepository;
    private final VolleyballPlayerMatchStatsRepository volleyballPlayerMatchStatsRepository;

    @Override
    public Optional<FantasyTeam> getTeamFor(User owner, FantasySport sport) {
        return fantasyTeamRepository.findByOwnerAndSport(owner, sport);
    }

    @Override
    public FantasyTeam createTeam(User owner, FantasySport sport, String teamName) {
        // Ensure we have a managed User entity
        User managedUser = userRepository.findById(owner.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Check if user already has a team for this sport
        Optional<FantasyTeam> existingTeam = fantasyTeamRepository.findByOwnerAndSport(managedUser, sport);
        if (existingTeam.isPresent()) {
            throw new RuntimeException("You already have a fantasy team for this sport");
        }
        
        FantasyTeam team = new FantasyTeam(managedUser, sport, teamName);
        return fantasyTeamRepository.save(team);
    }

    @Override
    public void spendBudget(FantasyTeam team, double amount) {
        double newSpent = team.getBudgetSpent() + amount;
        if (newSpent > team.getBudgetTotal()) {
            throw new RuntimeException("Not enough budget");
        }
        team.setBudgetSpent(newSpent);
        fantasyTeamRepository.save(team);
    }

    @Override
    @Transactional
    public FantasyPlayer buyFootballPlayer(FantasyTeam team, Long playerId) {
        FootballPlayer player = footballPlayerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found"));
        
        if (isPlayerAlreadyBought(team, FantasySport.FOOTBALL, playerId)) {
            throw new RuntimeException("Player already bought");
        }
        
        // Check formation constraints
        if (!canAddPlayerToFormation(team, player.getPosition())) {
            throw new RuntimeException("Formation constraint violated: Cannot add more " + player.getPosition() + " players");
        }
        
        double price = player.getPrice() != null ? player.getPrice() : 5.0;
        if (team.getBudgetSpent() + price > team.getBudgetTotal()) {
            throw new RuntimeException("Insufficient budget");
        }
        
        spendBudget(team, price);
        FantasyPlayer fantasyPlayer = new FantasyPlayer(team, player, price);
        return fantasyPlayerRepository.save(fantasyPlayer);
    }

    @Override
    @Transactional
    public FantasyPlayer buyBasketballPlayer(FantasyTeam team, Long playerId) {
        BasketballPlayer player = basketballPlayerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found"));
        
        if (isPlayerAlreadyBought(team, FantasySport.BASKETBALL, playerId)) {
            throw new RuntimeException("Player already bought");
        }
        
        // Check formation constraints for basketball
        if (!canAddPlayerToFormation(team, player.getPosition())) {
            throw new RuntimeException("Formation constraint violated: You can only have 1 " + player.getPosition() + " player");
        }
        
        double price = player.getPrice() != null ? player.getPrice() : 5.0;
        if (team.getBudgetSpent() + price > team.getBudgetTotal()) {
            throw new RuntimeException("Insufficient budget");
        }
        
        spendBudget(team, price);
        FantasyPlayer fantasyPlayer = new FantasyPlayer(team, player, price);
        return fantasyPlayerRepository.save(fantasyPlayer);
    }

    @Override
    @Transactional
    public FantasyPlayer buyVolleyballPlayer(FantasyTeam team, Long playerId) {
        VolleyballPlayer player = volleyballPlayerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found"));
        
        if (isPlayerAlreadyBought(team, FantasySport.VOLLEYBALL, playerId)) {
            throw new RuntimeException("Player already bought");
        }
        
        // Check formation constraints for volleyball
        if (!canAddPlayerToFormation(team, player.getPosition())) {
            java.util.Map<String, Integer> maxPositions = new java.util.HashMap<>();
            maxPositions.put("S", 1);
            maxPositions.put("OH", 2);
            maxPositions.put("MB", 2);
            maxPositions.put("OPP", 1);
            maxPositions.put("L", 1);
            maxPositions.put("DS", 1);
            Integer maxAllowed = maxPositions.get(player.getPosition());
            throw new RuntimeException("Formation constraint violated: You can only have " + maxAllowed + " " + player.getPosition() + " player(s)");
        }
        
        double price = player.getPrice() != null ? player.getPrice() : 5.0;
        if (team.getBudgetSpent() + price > team.getBudgetTotal()) {
            throw new RuntimeException("Insufficient budget");
        }
        
        spendBudget(team, price);
        FantasyPlayer fantasyPlayer = new FantasyPlayer(team, player, price);
        return fantasyPlayerRepository.save(fantasyPlayer);
    }

    @Override
    @Transactional
    public void sellFootballPlayer(FantasyTeam team, Long fantasyPlayerId) {
        FantasyPlayer fantasyPlayer = fantasyPlayerRepository.findById(fantasyPlayerId)
                .orElseThrow(() -> new RuntimeException("Fantasy player not found"));
        
        if (!fantasyPlayer.getFantasyTeam().getId().equals(team.getId())) {
            throw new RuntimeException("Player does not belong to this team");
        }
        
        if (fantasyPlayer.getFootballPlayer() == null) {
            throw new RuntimeException("This is not a football player");
        }
        
        // Get current market value of the player
        double currentValue = fantasyPlayer.getFootballPlayer().getPrice() != null ? 
                fantasyPlayer.getFootballPlayer().getPrice() : 5.0;
        
        // Add the current value back to the team's available budget
        // We do this by reducing budgetSpent (which increases available budget)
        team.setBudgetSpent(team.getBudgetSpent() - currentValue);
        fantasyTeamRepository.save(team);
        
        // Remove the fantasy player
        fantasyPlayerRepository.delete(fantasyPlayer);
    }

    @Override
    @Transactional
    public void sellBasketballPlayer(FantasyTeam team, Long fantasyPlayerId) {
        FantasyPlayer fantasyPlayer = fantasyPlayerRepository.findById(fantasyPlayerId)
                .orElseThrow(() -> new RuntimeException("Fantasy player not found"));
        
        if (!fantasyPlayer.getFantasyTeam().getId().equals(team.getId())) {
            throw new RuntimeException("Player does not belong to this team");
        }
        
        if (fantasyPlayer.getBasketballPlayer() == null) {
            throw new RuntimeException("This is not a basketball player");
        }
        
        // Get current market value of the player
        double currentValue = fantasyPlayer.getBasketballPlayer().getPrice() != null ? 
                fantasyPlayer.getBasketballPlayer().getPrice() : 5.0;
        
        // Add the current value to the team's budget
        team.setBudgetSpent(team.getBudgetSpent() - currentValue);
        fantasyTeamRepository.save(team);
        
        // Remove the fantasy player
        fantasyPlayerRepository.delete(fantasyPlayer);
    }

    @Override
    @Transactional
    public void sellVolleyballPlayer(FantasyTeam team, Long fantasyPlayerId) {
        FantasyPlayer fantasyPlayer = fantasyPlayerRepository.findById(fantasyPlayerId)
                .orElseThrow(() -> new RuntimeException("Fantasy player not found"));
        
        if (!fantasyPlayer.getFantasyTeam().getId().equals(team.getId())) {
            throw new RuntimeException("Player does not belong to this team");
        }
        
        if (fantasyPlayer.getVolleyballPlayer() == null) {
            throw new RuntimeException("This is not a volleyball player");
        }
        
        // Get current market value of the player
        double currentValue = fantasyPlayer.getVolleyballPlayer().getPrice() != null ? 
                fantasyPlayer.getVolleyballPlayer().getPrice() : 5.0;
        
        // Add the current value to the team's budget
        team.setBudgetSpent(team.getBudgetSpent() - currentValue);
        fantasyTeamRepository.save(team);
        
        // Remove the fantasy player
        fantasyPlayerRepository.delete(fantasyPlayer);
    }

    @Override
    public List<FantasyPlayer> getBoughtPlayers(FantasyTeam team) {
        return fantasyPlayerRepository.findByFantasyTeam(team);
    }

    @Override
    public List<FantasyPlayer> getAssignedPlayers(FantasyTeam team) {
        return fantasyPlayerRepository.findByFantasyTeamAndAssignedPositionIsNotNull(team);
    }

    @Override
    @Transactional
    public FantasyPlayer assignPlayerToPosition(FantasyTeam team, Long fantasyPlayerId, String position) {
        FantasyPlayer fantasyPlayer = fantasyPlayerRepository.findById(fantasyPlayerId)
                .orElseThrow(() -> new RuntimeException("Fantasy player not found"));
        
        if (!fantasyPlayer.getFantasyTeam().getId().equals(team.getId())) {
            throw new RuntimeException("Player does not belong to this team");
        }
        
        // Check if this specific position slot is already occupied
        Optional<FantasyPlayer> existingPlayer = fantasyPlayerRepository.findByFantasyTeamAndAssignedPosition(team, position);
        if (existingPlayer.isPresent()) {
            existingPlayer.get().setAssignedPosition(null);
            fantasyPlayerRepository.save(existingPlayer.get());
        }
        
        fantasyPlayer.setAssignedPosition(position);
        return fantasyPlayerRepository.save(fantasyPlayer);
    }

    @Override
    @Transactional
    public FantasyPlayer removePlayerFromPosition(FantasyTeam team, String position) {
        Optional<FantasyPlayer> fantasyPlayer = fantasyPlayerRepository.findByFantasyTeamAndAssignedPosition(team, position);
        if (fantasyPlayer.isPresent()) {
            fantasyPlayer.get().setAssignedPosition(null);
            return fantasyPlayerRepository.save(fantasyPlayer.get());
        }
        return null;
    }

    @Override
    public boolean isPlayerAlreadyBought(FantasyTeam team, FantasySport sport, Long playerId) {
        return switch (sport) {
            case FOOTBALL -> fantasyPlayerRepository.existsByFantasyTeamAndFootballPlayerId(team, playerId);
            case BASKETBALL -> fantasyPlayerRepository.existsByFantasyTeamAndBasketballPlayerId(team, playerId);
            case VOLLEYBALL -> fantasyPlayerRepository.existsByFantasyTeamAndVolleyballPlayerId(team, playerId);
        };
    }

    @Override
    public boolean canAddPlayerToFormation(FantasyTeam team, String playerPosition) {
        java.util.Map<String, Integer> maxPositions = new java.util.HashMap<>();
        
        // Define maximum players per position based on sport
        if (team.getSport() == FantasySport.FOOTBALL) {
            // 4-4-2 formation
            maxPositions.put("GK", 1);
            maxPositions.put("DEF", 4);
            maxPositions.put("MID", 4);
            maxPositions.put("FWD", 2);
        } else if (team.getSport() == FantasySport.BASKETBALL) {
            // Starting five
            maxPositions.put("PG", 1);
            maxPositions.put("SG", 1);
            maxPositions.put("SF", 1);
            maxPositions.put("PF", 1);
            maxPositions.put("C", 1);
        } else if (team.getSport() == FantasySport.VOLLEYBALL) {
            // 6-2 formation
            maxPositions.put("S", 1);
            maxPositions.put("OH", 2);
            maxPositions.put("MB", 2);
            maxPositions.put("OPP", 1);
            maxPositions.put("L", 1);
            maxPositions.put("DS", 1);
        }
        
        int currentCount = getPositionCount(team, playerPosition);
        Integer maxAllowed = maxPositions.get(playerPosition);
        
        return maxAllowed != null && currentCount < maxAllowed;
    }

    @Override
    public int getPositionCount(FantasyTeam team, String position) {
        List<FantasyPlayer> players = getBoughtPlayers(team);
        return (int) players.stream()
                .filter(fp -> {
                    if (fp.getFootballPlayer() != null) {
                        return position.equals(fp.getFootballPlayer().getPosition());
                    } else if (fp.getBasketballPlayer() != null) {
                        return position.equals(fp.getBasketballPlayer().getPosition());
                    } else if (fp.getVolleyballPlayer() != null) {
                        return position.equals(fp.getVolleyballPlayer().getPosition());
                    }
                    return false;
                })
                .count();
    }

    @Override
    @Transactional
    public void calculateAndAwardFantasyPoints(Long footballMatchId) {
        FootballMatch match = footballMatchRepository.findById(footballMatchId)
                .orElseThrow(() -> new RuntimeException("Football match not found"));
        
        // Get all player performances from this match
        List<FootballPlayerScored> playerPerformances = footballPlayerScoredRepository.findByFootballMatch(match);
        
        // For each player performance, calculate fantasy points and award to teams that own them
        for (FootballPlayerScored performance : playerPerformances) {
            FootballPlayer player = performance.getPlayer();
            double fantasyPoints = calculateFootballPlayerMatchFantasyPoints(performance);
            
            // Find all fantasy teams that own this player
            List<FantasyPlayer> fantasyPlayersOwningThisPlayer = fantasyPlayerRepository.findAll().stream()
                    .filter(fp -> fp.getFootballPlayer() != null && 
                                 fp.getFootballPlayer().getFootball_player_id().equals(player.getFootball_player_id()))
                    .toList();
            
            // Award points to each team that owns this player
            for (FantasyPlayer fantasyPlayer : fantasyPlayersOwningThisPlayer) {
                FantasyTeam team = fantasyPlayer.getFantasyTeam();
                
                // Add fantasy points to team's total
                team.setTotalPoints(team.getTotalPoints() + fantasyPoints);
                
                // Award budget bonus (points * 0.7 millions)
                awardBudgetBonus(team, fantasyPoints);
                
                // Update last modified time
                team.setLastUpdated(java.time.LocalDateTime.now());
                fantasyTeamRepository.save(team);
            }

            // Adjust player's price based on per-match performance and persist
            adjustFootballPlayerPrice(player, fantasyPoints);
            footballPlayerRepository.save(player);
        }
    }

    @Override
    public double calculatePlayerFantasyPoints(Long footballPlayerId) {
        FootballPlayer player = footballPlayerRepository.findById(footballPlayerId)
                .orElseThrow(() -> new RuntimeException("Football player not found"));
        
        // Get the latest match performance for this player
        // For now, we'll use the player's accumulated stats
        // In a real implementation, you'd want to track per-match performance
        
        String playerPosition = player.getPosition();
        double totalScore = 0.0;
        
        // Goal points (position-dependent)
        if ("GK".equals(playerPosition) || "DEF".equals(playerPosition)) {
            totalScore += player.getGoals() * 6;
        } else if ("MID".equals(playerPosition)) {
            totalScore += player.getGoals() * 5;
        } else if ("FWD".equals(playerPosition)) {
            totalScore += player.getGoals() * 4;
        }
        
        // Assist points
        totalScore += player.getAssists() * 3;
        
        // Goalkeeper specific points
        if ("GK".equals(playerPosition)) {
            // Penalty saves worth 5 points each (assuming all saves include penalty saves)
            // For simplicity, let's assume 10% of saves are penalty saves
            double penaltySaves = player.getSaves() * 0.1;
            totalScore += penaltySaves * 5;
            
            // Regular saves: 1 point per 3 saves
            totalScore += Math.floor(player.getSaves() / 3.0);
        }
        
        // Appearance bonus (1 point per appearance)
        totalScore += player.getAppearances();
        
        return totalScore;
    }

    private double calculateFootballPlayerMatchFantasyPoints(FootballPlayerScored performance) {
        FootballPlayer player = performance.getPlayer();
        String playerPosition = player.getPosition();
        double totalScore = 0.0;
        
        // Goal points (position-dependent)
        if ("GK".equals(playerPosition) || "DEF".equals(playerPosition)) {
            totalScore += performance.getGoalsScored() * 6;
        } else if ("MID".equals(playerPosition)) {
            totalScore += performance.getGoalsScored() * 5;
        } else if ("FWD".equals(playerPosition)) {
            totalScore += performance.getGoalsScored() * 4;
        }
        
        // Assist points
        totalScore += performance.getAssistsScored() * 3;
        
        // Goalkeeper specific points
        if ("GK".equals(playerPosition)) {
            // Regular saves: 1 point per 3 saves
            totalScore += Math.floor(performance.getSaves() / 3.0);
        }
        
        return totalScore;
    }

    private void adjustFootballPlayerPrice(FootballPlayer player, double matchPoints) {
        // Price adjustment tiers based on per-match fantasy points
        double delta;
        if (matchPoints >= 10) {
            delta = 0.6;
        } else if (matchPoints >= 8) {
            delta = 0.5;
        } else if (matchPoints >= 5) {
            delta = 0.3;
        } else if (matchPoints >= 3) {
            delta = 0.2;
        } else if (matchPoints > 0) {
            delta = 0.1;
        } else if (matchPoints == 0) {
            delta = -0.1;
        } else { // negative points (if introduced later)
            delta = -0.2;
        }

        double current = player.getPrice() != null ? player.getPrice() : 5.0;
        double updated = current + delta;
        // Enforce reasonable bounds
        if (updated < 0.5) updated = 0.5;
        if (updated > 50.0) updated = 50.0;
        // Round to 0.1 precision
        updated = Math.round(updated * 10.0) / 10.0;
        player.setPrice(updated);
    }

    private void adjustBasketballPlayerPrice(BasketballPlayer player, double matchPoints) {
        // Price adjustment tiers based on per-match fantasy points (identical to football)
        double delta;
        if (matchPoints >= 10) {
            delta = 0.6;
        } else if (matchPoints >= 8) {
            delta = 0.5;
        } else if (matchPoints >= 5) {
            delta = 0.3;
        } else if (matchPoints >= 3) {
            delta = 0.2;
        } else if (matchPoints > 0) {
            delta = 0.1;
        } else if (matchPoints == 0) {
            delta = -0.1;
        } else { // negative points (if introduced later)
            delta = -0.2;
        }

        double current = player.getPrice() != null ? player.getPrice() : 5.0;
        double updated = current + delta;
        // Enforce reasonable bounds
        if (updated < 0.5) updated = 0.5;
        if (updated > 50.0) updated = 50.0;
        // Round to 0.1 precision
        updated = Math.round(updated * 10.0) / 10.0;
        player.setPrice(updated);
    }

    private void adjustVolleyballPlayerPrice(VolleyballPlayer player, double matchPoints) {
        // Price adjustment tiers based on per-match fantasy points (identical to football)
        double delta;
        if (matchPoints >= 10) {
            delta = 0.6;
        } else if (matchPoints >= 8) {
            delta = 0.5;
        } else if (matchPoints >= 5) {
            delta = 0.3;
        } else if (matchPoints >= 3) {
            delta = 0.2;
        } else if (matchPoints > 0) {
            delta = 0.1;
        } else if (matchPoints == 0) {
            delta = -0.1;
        } else { // negative points (if introduced later)
            delta = -0.2;
        }

        double current = player.getPrice() != null ? player.getPrice() : 5.0;
        double updated = current + delta;
        // Enforce reasonable bounds
        if (updated < 0.5) updated = 0.5;
        if (updated > 50.0) updated = 50.0;
        // Round to 0.1 precision
        updated = Math.round(updated * 10.0) / 10.0;
        player.setPrice(updated);
    }

    @Override
    @Transactional
    public void awardBudgetBonus(FantasyTeam team, double points) {
        // Calculate bonus based on sport
        double multiplier = switch (team.getSport()) {
            case FOOTBALL -> 0.7;
            case BASKETBALL -> 0.3;
            case VOLLEYBALL -> 0.5;
        };
        
        double budgetBonus = points * multiplier;
        
        // Add to team's total budget (this is correct - fantasy points should increase total budget)
        team.setBudgetTotal(team.getBudgetTotal() + budgetBonus);
        
        fantasyTeamRepository.save(team);
    }

    @Override
    @Transactional
    public void calculateAndAwardBasketballFantasyPoints(Long basketballMatchId) {
        BasketballMatch match = basketballMatchRepository.findById(basketballMatchId)
                .orElseThrow(() -> new RuntimeException("Basketball match not found"));
        
        // Get all player performances from this match
        List<BasketballPlayerMatchStats> playerPerformances = basketballPlayerMatchStatsRepository.findByBasketballMatch(match);
        
        // For each player performance, calculate fantasy points and award to teams that own them
        for (BasketballPlayerMatchStats performance : playerPerformances) {
            BasketballPlayer player = performance.getPlayer();
            double fantasyPoints = calculateBasketballPlayerMatchFantasyPoints(performance);
            
            // Find all fantasy teams that own this player
            List<FantasyPlayer> fantasyPlayersOwningThisPlayer = fantasyPlayerRepository.findAll().stream()
                    .filter(fp -> fp.getBasketballPlayer() != null && 
                                 fp.getBasketballPlayer().getBasketball_player_id().equals(player.getBasketball_player_id()))
                    .toList();
            
            // Award points to each team that owns this player
            for (FantasyPlayer fantasyPlayer : fantasyPlayersOwningThisPlayer) {
                FantasyTeam team = fantasyPlayer.getFantasyTeam();
                
                // Add fantasy points to team's total
                team.setTotalPoints(team.getTotalPoints() + fantasyPoints);
                
                // Award budget bonus (points * 0.3 millions for basketball)
                awardBudgetBonus(team, fantasyPoints);
                
                // Update last modified time
                team.setLastUpdated(java.time.LocalDateTime.now());
                fantasyTeamRepository.save(team);
            }

            // Adjust player's price based on per-match performance and persist
            adjustBasketballPlayerPrice(player, fantasyPoints);
            basketballPlayerRepository.save(player);
        }
    }

    @Override
    public double calculateBasketballPlayerFantasyPoints(Long basketballPlayerId) {
        BasketballPlayer player = basketballPlayerRepository.findById(basketballPlayerId)
                .orElseThrow(() -> new RuntimeException("Basketball player not found"));
        
        // Basketball scoring: Point +1, Assist +1, Rebound +2
        double totalScore = 0.0;
        
        totalScore += player.getPoints(); // +1 per point
        totalScore += player.getAssists(); // +1 per assist  
        totalScore += player.getRebounds() * 2; // +2 per rebound
        
        // Appearance bonus
        totalScore += player.getAppearances();
        
        return totalScore;
    }

    private double calculateBasketballPlayerMatchFantasyPoints(BasketballPlayerMatchStats stats) {
        // Basketball scoring: Point +1, Assist +1, Rebound +2
        double totalScore = 0.0;
        
        totalScore += stats.getPointsScored(); // +1 per point
        totalScore += stats.getAssists(); // +1 per assist
        totalScore += stats.getRebounds() * 2; // +2 per rebound
        
        return totalScore;
    }

    @Override
    @Transactional
    public void calculateAndAwardVolleyballFantasyPoints(Long volleyballMatchId) {
        VolleyballMatch match = volleyballMatchRepository.findById(volleyballMatchId)
                .orElseThrow(() -> new RuntimeException("Volleyball match not found"));
        
        // Get all player performances from this match
        List<VolleyballPlayerMatchStats> playerPerformances = volleyballPlayerMatchStatsRepository.findByVolleyballMatch(match);
        
        // For each player performance, calculate fantasy points and award to teams that own them
        for (VolleyballPlayerMatchStats performance : playerPerformances) {
            VolleyballPlayer player = performance.getPlayer();
            double fantasyPoints = calculateVolleyballPlayerMatchFantasyPoints(performance);
            
            // Find all fantasy teams that own this player
            List<FantasyPlayer> fantasyPlayersOwningThisPlayer = fantasyPlayerRepository.findAll().stream()
                    .filter(fp -> fp.getVolleyballPlayer() != null && 
                                 fp.getVolleyballPlayer().getVolleyball_player_id().equals(player.getVolleyball_player_id()))
                    .toList();
            
            // Award points to each team that owns this player
            for (FantasyPlayer fantasyPlayer : fantasyPlayersOwningThisPlayer) {
                FantasyTeam team = fantasyPlayer.getFantasyTeam();
                
                // Add fantasy points to team's total
                team.setTotalPoints(team.getTotalPoints() + fantasyPoints);
                
                // Award budget bonus (points * 0.5 millions for volleyball)
                awardBudgetBonus(team, fantasyPoints);
                
                // Update last modified time
                team.setLastUpdated(java.time.LocalDateTime.now());
                fantasyTeamRepository.save(team);
            }

            // Adjust player's price based on per-match performance and persist
            adjustVolleyballPlayerPrice(player, fantasyPoints);
            volleyballPlayerRepository.save(player);
        }
    }

    @Override
    public double calculateVolleyballPlayerFantasyPoints(Long volleyballPlayerId) {
        VolleyballPlayer player = volleyballPlayerRepository.findById(volleyballPlayerId)
                .orElseThrow(() -> new RuntimeException("Volleyball player not found"));
        
        // Volleyball scoring: servings +1, assists +1, scored point +1, block +2
        double totalScore = 0.0;
        
        totalScore += player.getServings(); // +1 per serving
        totalScore += player.getAssists(); // +1 per assist
        totalScore += player.getScoredPoints(); // +1 per scored point
        totalScore += player.getBlocks() * 2; // +2 per block
        
        // Appearance bonus
        totalScore += player.getAppearances();
        
        return totalScore;
    }

    private double calculateVolleyballPlayerMatchFantasyPoints(VolleyballPlayerMatchStats stats) {
        // Volleyball scoring: servings +1, assists +1, scored point +1, block +2
        double totalScore = 0.0;
        
        totalScore += stats.getServings(); // +1 per serving
        totalScore += stats.getAssists(); // +1 per assist
        totalScore += stats.getScoredPoints(); // +1 per scored point
        totalScore += stats.getBlocks() * 2; // +2 per block
        
        return totalScore;
    }
}



