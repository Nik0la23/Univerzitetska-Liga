package mk.ukim.finki.wp.liga.service.fantasy.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import mk.ukim.finki.wp.liga.model.FantasyPlayer;
import mk.ukim.finki.wp.liga.model.FootballMatch;
import mk.ukim.finki.wp.liga.model.MatchPerformance;
import mk.ukim.finki.wp.liga.repository.fantasy.MatchPerformanceRepository;
import mk.ukim.finki.wp.liga.service.fantasy.MatchPerformanceService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class MatchPerformanceServiceImpl implements MatchPerformanceService {

    private final MatchPerformanceRepository matchPerformanceRepository;

    @Override
    @Transactional
    public List<MatchPerformance> findAll() {
        return matchPerformanceRepository.findAll();
    }

    @Override
    @Transactional
    public MatchPerformance findById(Long id) {
        return matchPerformanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Match performance not found"));
    }

    @Override
    @Transactional
    public MatchPerformance create(FantasyPlayer player, FootballMatch match) {
        MatchPerformance performance = new MatchPerformance();
        performance.setPlayer(player);
        performance.setMatch(match);
        return matchPerformanceRepository.save(performance);
    }

    @Override
    @Transactional
    public MatchPerformance update(Long id, Integer minutesPlayed, Integer goals,
                                  Integer assists, Integer saves, Integer yellowCards,
                                  Integer redCards) {
        MatchPerformance performance = findById(id);
        performance.setMinutesPlayed(minutesPlayed);
        performance.setGoals(goals);
        performance.setAssists(assists);
        performance.setSaves(saves);
        performance.setYellowCards(yellowCards);
        performance.setRedCards(redCards);
        
        // Calculate and set fantasy points
        performance.setFantasyPoints(calculateFantasyPoints(performance));
        
        // Update player's total fantasy points
        updatePlayerFantasyPoints(performance.getPlayer());
        
        return matchPerformanceRepository.save(performance);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        MatchPerformance performance = findById(id);
        matchPerformanceRepository.delete(performance);
        updatePlayerFantasyPoints(performance.getPlayer());
    }

    @Override
    @Transactional
    public MatchPerformance findByPlayerAndMatch(FantasyPlayer player, FootballMatch match) {
        return matchPerformanceRepository.findByPlayerAndMatch(player, match)
                .orElseThrow(() -> new RuntimeException("Match performance not found"));
    }

    @Override
    @Transactional
    public List<MatchPerformance> findAllByPlayer(FantasyPlayer player) {
        return matchPerformanceRepository.findAllByPlayer(player);
    }

    @Override
    @Transactional
    public List<MatchPerformance> findAllByMatch(FootballMatch match) {
        return matchPerformanceRepository.findAllByMatch(match);
    }

    @Override
    public Integer calculateFantasyPoints(MatchPerformance performance) {
        return (performance.getMinutesPlayed() >= 60 ? 2 : (performance.getMinutesPlayed() >= 1 ? 1 : 0)) + // Playing time points
               (performance.getGoals() * 5) +                                               // Goals
               (performance.getAssists() * 3) +                                            // Assists
               (performance.getSaves() * 2) -                                             // Saves (for goalkeepers)
               (performance.getYellowCards() * 1) -                                       // Yellow card deduction
               (performance.getRedCards() * 3);                                          // Red card deduction
    }

    @Override
    @Transactional
    public void updatePlayerFantasyPoints(FantasyPlayer player) {
        // Calculate total fantasy points from all matches
        int totalPoints = findAllByPlayer(player).stream()
                .mapToInt(MatchPerformance::getFantasyPoints)
                .sum();
        
        // Add base points from appearances, goals, assists, and saves
        totalPoints += player.getAppearances() * 2 +
                      player.getGoals() * 5 +
                      player.getAssists() * 3 +
                      player.getSaves() * 2;
        
        player.setFantasyPoints(totalPoints);
    }
} 