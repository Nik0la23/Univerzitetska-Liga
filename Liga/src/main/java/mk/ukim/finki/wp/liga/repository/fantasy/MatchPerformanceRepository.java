package mk.ukim.finki.wp.liga.repository.fantasy;

import mk.ukim.finki.wp.liga.model.FantasyPlayer;
import mk.ukim.finki.wp.liga.model.FootballMatch;
import mk.ukim.finki.wp.liga.model.MatchPerformance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchPerformanceRepository extends JpaRepository<MatchPerformance, Long> {
    Optional<MatchPerformance> findByPlayerAndMatch(FantasyPlayer player, FootballMatch match);
    List<MatchPerformance> findAllByPlayer(FantasyPlayer player);
    List<MatchPerformance> findAllByMatch(FootballMatch match);
} 