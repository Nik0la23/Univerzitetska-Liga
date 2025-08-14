package mk.ukim.finki.wp.liga.repository.fantasy;

import mk.ukim.finki.wp.liga.model.User;
import mk.ukim.finki.wp.liga.model.fantasy.FantasySport;
import mk.ukim.finki.wp.liga.model.fantasy.FantasyTeam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FantasyTeamRepository extends JpaRepository<FantasyTeam, Long> {
    Optional<FantasyTeam> findByOwnerAndSport(User owner, FantasySport sport);
    boolean existsByTeamName(String teamName);
}



