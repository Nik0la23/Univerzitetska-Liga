package mk.ukim.finki.wp.liga.repository.volleyball;

import mk.ukim.finki.wp.liga.model.VolleyballMatch;
import mk.ukim.finki.wp.liga.model.VolleyballPlayer;
import mk.ukim.finki.wp.liga.model.VolleyballPlayerMatchStats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VolleyballPlayerMatchStatsRepository extends JpaRepository<VolleyballPlayerMatchStats, Long> {
    List<VolleyballPlayerMatchStats> findByVolleyballMatch(VolleyballMatch volleyballMatch);
    VolleyballPlayerMatchStats findByIdAndVolleyballMatch(Long id, VolleyballMatch volleyballMatch);
    VolleyballPlayerMatchStats findByPlayerAndVolleyballMatch(VolleyballPlayer volleyballPlayer, VolleyballMatch volleyballMatch);
    List<VolleyballPlayerMatchStats> findByPlayer(VolleyballPlayer player);
}
