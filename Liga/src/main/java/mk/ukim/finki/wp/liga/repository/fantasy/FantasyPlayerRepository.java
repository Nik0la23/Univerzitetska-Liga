package mk.ukim.finki.wp.liga.repository.fantasy;

import mk.ukim.finki.wp.liga.model.FantasyPlayer;
import mk.ukim.finki.wp.liga.model.FantasyTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FantasyPlayerRepository extends JpaRepository<FantasyPlayer, Long> {
    List<FantasyPlayer> findAllByFantasyTeam(FantasyTeam team);
    List<FantasyPlayer> findAllByFantasyTeamIsNull();
    List<FantasyPlayer> findAllByOrderByPriceAsc();
    List<FantasyPlayer> findAllByOrderByFantasyPointsDesc();
    List<FantasyPlayer> findByPositionOrderByFantasyPointsDesc(String position);
} 