package mk.ukim.finki.wp.liga.repository.fantasy;

import mk.ukim.finki.wp.liga.model.fantasy.FantasyPlayer;
import mk.ukim.finki.wp.liga.model.fantasy.FantasyTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FantasyPlayerRepository extends JpaRepository<FantasyPlayer, Long> {
    List<FantasyPlayer> findByFantasyTeam(FantasyTeam fantasyTeam);
    List<FantasyPlayer> findByFantasyTeamAndAssignedPositionIsNotNull(FantasyTeam fantasyTeam);
    Optional<FantasyPlayer> findByFantasyTeamAndAssignedPosition(FantasyTeam fantasyTeam, String assignedPosition);
    
    @Query("SELECT CASE WHEN COUNT(fp) > 0 THEN true ELSE false END FROM FantasyPlayer fp WHERE fp.fantasyTeam = :team AND fp.footballPlayer.football_player_id = :playerId")
    boolean existsByFantasyTeamAndFootballPlayerId(@Param("team") FantasyTeam fantasyTeam, @Param("playerId") Long playerId);
    
    @Query("SELECT CASE WHEN COUNT(fp) > 0 THEN true ELSE false END FROM FantasyPlayer fp WHERE fp.fantasyTeam = :team AND fp.basketballPlayer.basketball_player_id = :playerId")
    boolean existsByFantasyTeamAndBasketballPlayerId(@Param("team") FantasyTeam fantasyTeam, @Param("playerId") Long playerId);
    
    @Query("SELECT CASE WHEN COUNT(fp) > 0 THEN true ELSE false END FROM FantasyPlayer fp WHERE fp.fantasyTeam = :team AND fp.volleyballPlayer.volleyball_player_id = :playerId")
    boolean existsByFantasyTeamAndVolleyballPlayerId(@Param("team") FantasyTeam fantasyTeam, @Param("playerId") Long playerId);
}
