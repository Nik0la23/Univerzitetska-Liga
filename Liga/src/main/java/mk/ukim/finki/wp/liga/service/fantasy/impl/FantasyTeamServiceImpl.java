package mk.ukim.finki.wp.liga.service.fantasy.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import mk.ukim.finki.wp.liga.model.FantasyPlayer;
import mk.ukim.finki.wp.liga.model.FantasyTeam;
import mk.ukim.finki.wp.liga.model.User;
import mk.ukim.finki.wp.liga.repository.fantasy.FantasyTeamRepository;
import mk.ukim.finki.wp.liga.service.fantasy.FantasyPlayerService;
import mk.ukim.finki.wp.liga.service.fantasy.FantasyTeamService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FantasyTeamServiceImpl implements FantasyTeamService {

    private final FantasyTeamRepository fantasyTeamRepository;
    private final FantasyPlayerService fantasyPlayerService;
    
    private static final List<String> VALID_FORMATIONS = Arrays.asList(
            "4-4-2", "4-3-3", "4-5-1", "3-5-2", "3-4-3", "5-3-2", "5-4-1"
    );

    @Override
    @Transactional
    public List<FantasyTeam> findAll() {
        return fantasyTeamRepository.findAll();
    }

    @Override
    @Transactional
    public FantasyTeam findById(Long id) {
        return fantasyTeamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fantasy team not found"));
    }

    @Override
    @Transactional
    public FantasyTeam findByOwner(User owner) {
        return fantasyTeamRepository.findByOwner(owner)
                .orElseThrow(() -> new RuntimeException("Fantasy team not found for this owner"));
    }

    @Override
    @Transactional
    public FantasyTeam create(String teamName, User owner, String formation) {
        if (isTeamNameTaken(teamName)) {
            throw new RuntimeException("Team name is already taken");
        }
        
        if (!isValidFormation(formation)) {
            throw new RuntimeException("Invalid formation");
        }
        
        FantasyTeam team = new FantasyTeam();
        team.setTeamName(teamName);
        team.setOwner(owner);
        team.setFormation(formation);
        team.setBudgetTotal(100.0); // Default budget
        team.setBudgetSpent(0.0);
        team.setTotalPoints(0);
        
        return fantasyTeamRepository.save(team);
    }

    @Override
    @Transactional
    public FantasyTeam update(Long id, String teamName, String formation) {
        FantasyTeam team = findById(id);
        
        if (!team.getTeamName().equals(teamName) && isTeamNameTaken(teamName)) {
            throw new RuntimeException("Team name is already taken");
        }
        
        if (!isValidFormation(formation)) {
            throw new RuntimeException("Invalid formation");
        }
        
        team.setTeamName(teamName);
        team.setFormation(formation);
        
        return fantasyTeamRepository.save(team);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        FantasyTeam team = findById(id);
        // Remove all players from the team before deleting
        team.getPlayers().forEach(player -> fantasyPlayerService.removeFromTeam(player.getFootball_player_id()));
        fantasyTeamRepository.delete(team);
    }

    @Override
    @Transactional
    public List<FantasyTeam> getLeaderboard() {
        return fantasyTeamRepository.findAllByOrderByTotalPointsDesc();
    }

    @Override
    public boolean isTeamNameTaken(String teamName) {
        return fantasyTeamRepository.existsByTeamName(teamName);
    }

    @Override
    @Transactional
    public FantasyTeam addPlayer(Long teamId, Long playerId) {
        FantasyTeam team = findById(teamId);
        FantasyPlayer player = fantasyPlayerService.findById(playerId);
        
        if (!isValidPlayerAddition(team, player)) {
            throw new RuntimeException("Invalid player addition");
        }
        
        if (!hasEnoughBudget(team, player)) {
            throw new RuntimeException("Not enough budget");
        }
        
        team.addPlayer(player);
        return fantasyTeamRepository.save(team);
    }

    @Override
    @Transactional
    public FantasyTeam removePlayer(Long teamId, Long playerId) {
        FantasyTeam team = findById(teamId);
        FantasyPlayer player = fantasyPlayerService.findById(playerId);
        
        team.removePlayer(player);
        return fantasyTeamRepository.save(team);
    }

    @Override
    public boolean isValidFormation(String formation) {
        return VALID_FORMATIONS.contains(formation);
    }

    @Override
    public boolean hasEnoughBudget(FantasyTeam team, FantasyPlayer player) {
        return team.getBudgetSpent() + player.getPrice() <= team.getBudgetTotal();
    }

    @Override
    public boolean isValidPlayerAddition(FantasyTeam team, FantasyPlayer player) {
        if (player.getFantasyTeam() != null) {
            return false; // Player already belongs to a team
        }
        
        // Parse formation to get required number of players per position
        String[] formation = team.getFormation().split("-");
        Map<String, Long> currentPositionCounts = team.getPlayers().stream()
                .collect(Collectors.groupingBy(FantasyPlayer::getPosition, Collectors.counting()));
        
        // Check position limits based on formation
        switch (player.getPosition()) {
            case "GK":
                return currentPositionCounts.getOrDefault("GK", 0L) < 1;
            case "DEF":
                return currentPositionCounts.getOrDefault("DEF", 0L) < Integer.parseInt(formation[0]);
            case "MID":
                return currentPositionCounts.getOrDefault("MID", 0L) < Integer.parseInt(formation[1]);
            case "FWD":
                return currentPositionCounts.getOrDefault("FWD", 0L) < Integer.parseInt(formation[2]);
            default:
                return false;
        }
    }

    @Override
    @Transactional
    public void updateTeamPoints(Long teamId) {
        FantasyTeam team = findById(teamId);
        int totalPoints = team.getPlayers().stream()
                .mapToInt(FantasyPlayer::getPoints)
                .sum();
        team.setTotalPoints(totalPoints);
        fantasyTeamRepository.save(team);
    }
} 