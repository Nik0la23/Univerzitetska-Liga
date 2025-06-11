package mk.ukim.finki.wp.liga.service.fantasy.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import mk.ukim.finki.wp.liga.model.FantasyPlayer;
import mk.ukim.finki.wp.liga.model.FantasyTeam;
import mk.ukim.finki.wp.liga.model.FootballPlayer;
import mk.ukim.finki.wp.liga.repository.fantasy.FantasyPlayerRepository;
import mk.ukim.finki.wp.liga.service.fantasy.FantasyPlayerService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class FantasyPlayerServiceImpl implements FantasyPlayerService {

    private final FantasyPlayerRepository fantasyPlayerRepository;

    @Override
    @Transactional
    public List<FantasyPlayer> findAll() {
        return fantasyPlayerRepository.findAll();
    }

    @Override
    @Transactional
    public FantasyPlayer findById(Long id) {
        return fantasyPlayerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fantasy player not found"));
    }

    @Override
    @Transactional
    public FantasyPlayer create(FootballPlayer basePlayer, Double price) {
        FantasyPlayer fantasyPlayer = new FantasyPlayer();
        
        // Copy properties from base player
        fantasyPlayer.setName(basePlayer.getName());
        fantasyPlayer.setSurname(basePlayer.getSurname());
        fantasyPlayer.setBirthdate(basePlayer.getBirthdate());
        fantasyPlayer.setIndex(basePlayer.getIndex());
        fantasyPlayer.setCity(basePlayer.getCity());
        fantasyPlayer.setPosition(basePlayer.getPosition());
        fantasyPlayer.setTeam(basePlayer.getTeam());
        
        // Set fantasy-specific properties
        fantasyPlayer.setPrice(price != null ? price : calculatePrice(basePlayer));
        fantasyPlayer.setFantasyPoints(0);
        
        return fantasyPlayerRepository.save(fantasyPlayer);
    }

    @Override
    @Transactional
    public FantasyPlayer update(Long id, Double price) {
        FantasyPlayer player = findById(id);
        player.setPrice(price);
        return fantasyPlayerRepository.save(player);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        fantasyPlayerRepository.deleteById(id);
    }

    @Override
    @Transactional
    public List<FantasyPlayer> findAllAvailablePlayers() {
        return fantasyPlayerRepository.findAllByFantasyTeamIsNull();
    }

    @Override
    @Transactional
    public List<FantasyPlayer> findAllByTeam(FantasyTeam team) {
        return fantasyPlayerRepository.findAllByFantasyTeam(team);
    }

    @Override
    @Transactional
    public List<FantasyPlayer> findAllByOrderByPrice() {
        return fantasyPlayerRepository.findAllByOrderByPriceAsc();
    }

    @Override
    @Transactional
    public List<FantasyPlayer> findTopPerformers() {
        return fantasyPlayerRepository.findAllByOrderByFantasyPointsDesc();
    }

    @Override
    @Transactional
    public List<FantasyPlayer> findTopPerformersByPosition(String position) {
        return fantasyPlayerRepository.findByPositionOrderByFantasyPointsDesc(position);
    }

    @Override
    @Transactional
    public void assignToTeam(Long playerId, FantasyTeam team) {
        FantasyPlayer player = findById(playerId);
        player.setFantasyTeam(team);
        fantasyPlayerRepository.save(player);
    }

    @Override
    @Transactional
    public void removeFromTeam(Long playerId) {
        FantasyPlayer player = findById(playerId);
        player.setFantasyTeam(null);
        fantasyPlayerRepository.save(player);
    }

    @Override
    public Double calculatePrice(FootballPlayer basePlayer) {
        // Base price calculation logic based on player statistics
        double basePrice = 5.0; // Minimum base price
        
        // Add value based on goals and assists
        basePrice += basePlayer.getGoals() * 0.5;
        basePrice += basePlayer.getAssists() * 0.3;
        
        // Add value based on appearances
        basePrice += basePlayer.getAppearances() * 0.1;
        
        // Add value for goalkeepers based on saves
        if ("GK".equals(basePlayer.getPosition())) {
            basePrice += basePlayer.getSaves() * 0.2;
        }
        
        return Math.round(basePrice * 10.0) / 10.0; // Round to 1 decimal place
    }
} 