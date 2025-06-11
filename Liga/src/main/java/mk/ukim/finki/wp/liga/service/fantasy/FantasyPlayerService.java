package mk.ukim.finki.wp.liga.service.fantasy;

import mk.ukim.finki.wp.liga.model.FantasyPlayer;
import mk.ukim.finki.wp.liga.model.FantasyTeam;
import mk.ukim.finki.wp.liga.model.FootballPlayer;

import java.util.List;

public interface FantasyPlayerService {
    
    List<FantasyPlayer> findAll();
    
    FantasyPlayer findById(Long id);
    
    FantasyPlayer create(FootballPlayer basePlayer, Double price);
    
    FantasyPlayer update(Long id, Double price);
    
    void delete(Long id);
    
    List<FantasyPlayer> findAllAvailablePlayers();
    
    List<FantasyPlayer> findAllByTeam(FantasyTeam team);
    
    List<FantasyPlayer> findAllByOrderByPrice();
    
    List<FantasyPlayer> findTopPerformers();
    
    List<FantasyPlayer> findTopPerformersByPosition(String position);
    
    void assignToTeam(Long playerId, FantasyTeam team);
    
    void removeFromTeam(Long playerId);
    
    Double calculatePrice(FootballPlayer basePlayer);
} 