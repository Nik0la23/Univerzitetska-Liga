package mk.ukim.finki.wp.liga.repository.fantasy;

import mk.ukim.finki.wp.liga.model.FantasyTeam;
import mk.ukim.finki.wp.liga.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FantasyTeamRepository extends JpaRepository<FantasyTeam, Long> {
    Optional<FantasyTeam> findByOwner(User owner);
    List<FantasyTeam> findAllByOrderByTotalPointsDesc();
    boolean existsByTeamName(String teamName);
} 