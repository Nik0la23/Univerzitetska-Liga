package mk.ukim.finki.wp.liga.service.fantasy;

import mk.ukim.finki.wp.liga.model.FantasyPlayer;
import mk.ukim.finki.wp.liga.model.FootballMatch;
import mk.ukim.finki.wp.liga.model.MatchPerformance;

import java.util.List;

public interface MatchPerformanceService {
    
    List<MatchPerformance> findAll();
    
    MatchPerformance findById(Long id);
    
    MatchPerformance create(FantasyPlayer player, FootballMatch match);
    
    MatchPerformance update(Long id, Integer minutesPlayed, Integer goals, 
                           Integer assists, Integer saves, Integer yellowCards, 
                           Integer redCards);
    
    void delete(Long id);
    
    MatchPerformance findByPlayerAndMatch(FantasyPlayer player, FootballMatch match);
    
    List<MatchPerformance> findAllByPlayer(FantasyPlayer player);
    
    List<MatchPerformance> findAllByMatch(FootballMatch match);
    
    Integer calculateFantasyPoints(MatchPerformance performance);
    
    void updatePlayerFantasyPoints(FantasyPlayer player);
} 