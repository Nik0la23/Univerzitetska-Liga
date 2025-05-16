package mk.ukim.finki.wp.liga.repository.basketball;

import mk.ukim.finki.wp.liga.model.BasketballMatch;
import mk.ukim.finki.wp.liga.model.BasketballTeam;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BasketballMatchRepository extends JpaRepository<BasketballMatch, Long> {
List<BasketballMatch> findAllByHomeTeamOrAwayTeam(BasketballTeam homeTeam, BasketballTeam awayTeam);
    List<BasketballMatch> findAllByIsPlayoffMatchTrue();
    void deleteAllByIsPlayoffMatchTrue();
    @EntityGraph(attributePaths = {"homeTeam.players", "awayTeam.players"})
    Optional<BasketballMatch> findById(Long id);
}
