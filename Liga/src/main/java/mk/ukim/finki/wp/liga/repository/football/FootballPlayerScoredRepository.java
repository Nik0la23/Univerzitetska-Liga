package mk.ukim.finki.wp.liga.repository.football;

import mk.ukim.finki.wp.liga.model.FootballMatch;
import mk.ukim.finki.wp.liga.model.FootballPlayer;
import mk.ukim.finki.wp.liga.model.FootballPlayerScored;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FootballPlayerScoredRepository extends JpaRepository<FootballPlayerScored, Long> {
    FootballPlayerScored findFootballPlayerScoredByPlayerAndFootballMatch(FootballPlayer player, FootballMatch match);
    List<FootballPlayerScored> findByPlayer(FootballPlayer player);
}
