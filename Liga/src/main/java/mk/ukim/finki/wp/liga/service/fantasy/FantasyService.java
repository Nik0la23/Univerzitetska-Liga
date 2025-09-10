package mk.ukim.finki.wp.liga.service.fantasy;

import mk.ukim.finki.wp.liga.model.User;
import mk.ukim.finki.wp.liga.model.fantasy.FantasyPlayer;
import mk.ukim.finki.wp.liga.model.fantasy.FantasySport;
import mk.ukim.finki.wp.liga.model.fantasy.FantasyTeam;

import java.util.List;
import java.util.Optional;

public interface FantasyService {
    Optional<FantasyTeam> getTeamFor(User owner, FantasySport sport);
    FantasyTeam createTeam(User owner, FantasySport sport, String teamName);
    void spendBudget(FantasyTeam team, double amount);
    
    // Player purchase methods
    FantasyPlayer buyFootballPlayer(FantasyTeam team, Long playerId);
    FantasyPlayer buyBasketballPlayer(FantasyTeam team, Long playerId);
    FantasyPlayer buyVolleyballPlayer(FantasyTeam team, Long playerId);
    
    // Player sell methods
    void sellFootballPlayer(FantasyTeam team, Long fantasyPlayerId);
    void sellBasketballPlayer(FantasyTeam team, Long fantasyPlayerId);
    void sellVolleyballPlayer(FantasyTeam team, Long fantasyPlayerId);
    
    // Get bought players
    List<FantasyPlayer> getBoughtPlayers(FantasyTeam team);
    List<FantasyPlayer> getAssignedPlayers(FantasyTeam team);
    
    // Position assignment
    FantasyPlayer assignPlayerToPosition(FantasyTeam team, Long fantasyPlayerId, String position);
    FantasyPlayer removePlayerFromPosition(FantasyTeam team, String position);
    
    // Check if player is already bought
    boolean isPlayerAlreadyBought(FantasyTeam team, FantasySport sport, Long playerId);
    
    // Formation validation
    boolean canAddPlayerToFormation(FantasyTeam team, String playerPosition);
    int getPositionCount(FantasyTeam team, String position);
    
    // Fantasy points calculation
    void calculateAndAwardFantasyPoints(Long footballMatchId);
    double calculatePlayerFantasyPoints(Long footballPlayerId);
    void awardBudgetBonus(FantasyTeam team, double points);
    
    // Basketball fantasy points
    void calculateAndAwardBasketballFantasyPoints(Long basketballMatchId);
    double calculateBasketballPlayerFantasyPoints(Long basketballPlayerId);
    
    // Volleyball fantasy points
    void calculateAndAwardVolleyballFantasyPoints(Long volleyballMatchId);
    double calculateVolleyballPlayerFantasyPoints(Long volleyballPlayerId);
}



