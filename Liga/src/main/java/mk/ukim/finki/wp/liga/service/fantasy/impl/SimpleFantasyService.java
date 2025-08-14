package mk.ukim.finki.wp.liga.service.fantasy.impl;

import lombok.RequiredArgsConstructor;
import mk.ukim.finki.wp.liga.model.BasketballPlayer;
import mk.ukim.finki.wp.liga.model.FootballPlayer;
import mk.ukim.finki.wp.liga.model.User;
import mk.ukim.finki.wp.liga.model.VolleyballPlayer;
import mk.ukim.finki.wp.liga.model.fantasy.FantasyPlayer;
import mk.ukim.finki.wp.liga.model.fantasy.FantasySport;
import mk.ukim.finki.wp.liga.model.fantasy.FantasyTeam;
import mk.ukim.finki.wp.liga.repository.basketball.BasketballPlayerRepository;
import mk.ukim.finki.wp.liga.repository.fantasy.FantasyPlayerRepository;
import mk.ukim.finki.wp.liga.repository.fantasy.FantasyTeamRepository;
import mk.ukim.finki.wp.liga.repository.football.FootballPlayerRepository;
import mk.ukim.finki.wp.liga.repository.volleyball.VolleyballPlayerRepository;
import mk.ukim.finki.wp.liga.repository.UserRepository;
import mk.ukim.finki.wp.liga.service.fantasy.FantasyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SimpleFantasyService implements FantasyService {
    private final FantasyTeamRepository fantasyTeamRepository;
    private final FantasyPlayerRepository fantasyPlayerRepository;
    private final FootballPlayerRepository footballPlayerRepository;
    private final BasketballPlayerRepository basketballPlayerRepository;
    private final VolleyballPlayerRepository volleyballPlayerRepository;
    private final UserRepository userRepository;

    @Override
    public Optional<FantasyTeam> getTeamFor(User owner, FantasySport sport) {
        return fantasyTeamRepository.findByOwnerAndSport(owner, sport);
    }

    @Override
    public FantasyTeam createTeam(User owner, FantasySport sport, String teamName) {
        // Ensure we have a managed User entity
        User managedUser = userRepository.findById(owner.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Check if user already has a team for this sport
        Optional<FantasyTeam> existingTeam = fantasyTeamRepository.findByOwnerAndSport(managedUser, sport);
        if (existingTeam.isPresent()) {
            throw new RuntimeException("You already have a fantasy team for this sport");
        }
        
        FantasyTeam team = new FantasyTeam(managedUser, sport, teamName);
        return fantasyTeamRepository.save(team);
    }

    @Override
    public void spendBudget(FantasyTeam team, double amount) {
        double newSpent = team.getBudgetSpent() + amount;
        if (newSpent > team.getBudgetTotal()) {
            throw new RuntimeException("Not enough budget");
        }
        team.setBudgetSpent(newSpent);
        fantasyTeamRepository.save(team);
    }

    @Override
    @Transactional
    public FantasyPlayer buyFootballPlayer(FantasyTeam team, Long playerId) {
        FootballPlayer player = footballPlayerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found"));
        
        if (isPlayerAlreadyBought(team, FantasySport.FOOTBALL, playerId)) {
            throw new RuntimeException("Player already bought");
        }
        
        double price = player.getPrice() != null ? player.getPrice() : 5.0;
        if (team.getBudgetSpent() + price > team.getBudgetTotal()) {
            throw new RuntimeException("Insufficient budget");
        }
        
        spendBudget(team, price);
        FantasyPlayer fantasyPlayer = new FantasyPlayer(team, player, price);
        return fantasyPlayerRepository.save(fantasyPlayer);
    }

    @Override
    @Transactional
    public FantasyPlayer buyBasketballPlayer(FantasyTeam team, Long playerId) {
        BasketballPlayer player = basketballPlayerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found"));
        
        if (isPlayerAlreadyBought(team, FantasySport.BASKETBALL, playerId)) {
            throw new RuntimeException("Player already bought");
        }
        
        double price = player.getPrice() != null ? player.getPrice() : 5.0;
        if (team.getBudgetSpent() + price > team.getBudgetTotal()) {
            throw new RuntimeException("Insufficient budget");
        }
        
        spendBudget(team, price);
        FantasyPlayer fantasyPlayer = new FantasyPlayer(team, player, price);
        return fantasyPlayerRepository.save(fantasyPlayer);
    }

    @Override
    @Transactional
    public FantasyPlayer buyVolleyballPlayer(FantasyTeam team, Long playerId) {
        VolleyballPlayer player = volleyballPlayerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found"));
        
        if (isPlayerAlreadyBought(team, FantasySport.VOLLEYBALL, playerId)) {
            throw new RuntimeException("Player already bought");
        }
        
        double price = player.getPrice() != null ? player.getPrice() : 5.0;
        if (team.getBudgetSpent() + price > team.getBudgetTotal()) {
            throw new RuntimeException("Insufficient budget");
        }
        
        spendBudget(team, price);
        FantasyPlayer fantasyPlayer = new FantasyPlayer(team, player, price);
        return fantasyPlayerRepository.save(fantasyPlayer);
    }

    @Override
    public List<FantasyPlayer> getBoughtPlayers(FantasyTeam team) {
        return fantasyPlayerRepository.findByFantasyTeam(team);
    }

    @Override
    public List<FantasyPlayer> getAssignedPlayers(FantasyTeam team) {
        return fantasyPlayerRepository.findByFantasyTeamAndAssignedPositionIsNotNull(team);
    }

    @Override
    @Transactional
    public FantasyPlayer assignPlayerToPosition(FantasyTeam team, Long fantasyPlayerId, String position) {
        FantasyPlayer fantasyPlayer = fantasyPlayerRepository.findById(fantasyPlayerId)
                .orElseThrow(() -> new RuntimeException("Fantasy player not found"));
        
        if (!fantasyPlayer.getFantasyTeam().getId().equals(team.getId())) {
            throw new RuntimeException("Player does not belong to this team");
        }
        
        // Check if position is already occupied
        Optional<FantasyPlayer> existingPlayer = fantasyPlayerRepository.findByFantasyTeamAndAssignedPosition(team, position);
        if (existingPlayer.isPresent()) {
            existingPlayer.get().setAssignedPosition(null);
            fantasyPlayerRepository.save(existingPlayer.get());
        }
        
        fantasyPlayer.setAssignedPosition(position);
        return fantasyPlayerRepository.save(fantasyPlayer);
    }

    @Override
    @Transactional
    public FantasyPlayer removePlayerFromPosition(FantasyTeam team, String position) {
        Optional<FantasyPlayer> fantasyPlayer = fantasyPlayerRepository.findByFantasyTeamAndAssignedPosition(team, position);
        if (fantasyPlayer.isPresent()) {
            fantasyPlayer.get().setAssignedPosition(null);
            return fantasyPlayerRepository.save(fantasyPlayer.get());
        }
        return null;
    }

    @Override
    public boolean isPlayerAlreadyBought(FantasyTeam team, FantasySport sport, Long playerId) {
        switch (sport) {
            case FOOTBALL:
                return fantasyPlayerRepository.existsByFantasyTeamAndFootballPlayerId(team, playerId);
            case BASKETBALL:
                return fantasyPlayerRepository.existsByFantasyTeamAndBasketballPlayerId(team, playerId);
            case VOLLEYBALL:
                return fantasyPlayerRepository.existsByFantasyTeamAndVolleyballPlayerId(team, playerId);
            default:
                return false;
        }
    }
}



