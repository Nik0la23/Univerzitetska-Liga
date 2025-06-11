package mk.ukim.finki.wp.liga.service.fantasy;

import mk.ukim.finki.wp.liga.model.FantasyPlayer;
import mk.ukim.finki.wp.liga.model.FantasyTeam;
import mk.ukim.finki.wp.liga.model.User;

import java.util.List;

public interface FantasyTeamService {
    
    List<FantasyTeam> findAll();
    
    FantasyTeam findById(Long id);
    
    FantasyTeam findByOwner(User owner);
    
    FantasyTeam create(String teamName, User owner, String formation);
    
    FantasyTeam update(Long id, String teamName, String formation);
    
    void delete(Long id);
    
    List<FantasyTeam> getLeaderboard();
    
    boolean isTeamNameTaken(String teamName);
    
    FantasyTeam addPlayer(Long teamId, Long playerId);
    
    FantasyTeam removePlayer(Long teamId, Long playerId);
    
    boolean isValidFormation(String formation);
    
    boolean hasEnoughBudget(FantasyTeam team, FantasyPlayer player);
    
    boolean isValidPlayerAddition(FantasyTeam team, FantasyPlayer player);
    
    void updateTeamPoints(Long teamId);
} 