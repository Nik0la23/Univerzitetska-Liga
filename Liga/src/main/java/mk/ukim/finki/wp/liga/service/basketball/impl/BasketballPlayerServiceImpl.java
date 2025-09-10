package mk.ukim.finki.wp.liga.service.basketball.impl;

import lombok.AllArgsConstructor;
import mk.ukim.finki.wp.liga.model.BasketballPlayer;
import mk.ukim.finki.wp.liga.model.BasketballPlayerMatchStats;
import mk.ukim.finki.wp.liga.model.BasketballTeam;
import mk.ukim.finki.wp.liga.model.Exceptions.InvalidBasketballPlayerException;
import mk.ukim.finki.wp.liga.model.Exceptions.InvalidBasketballTeamException;
import mk.ukim.finki.wp.liga.model.fantasy.FantasyPlayer;
import mk.ukim.finki.wp.liga.repository.basketball.BasketballPlayerMatchStatsRepository;
import mk.ukim.finki.wp.liga.repository.basketball.BasketballPlayerRepository;
import mk.ukim.finki.wp.liga.repository.basketball.BasketballTeamRepository;
import mk.ukim.finki.wp.liga.repository.fantasy.FantasyPlayerRepository;
import mk.ukim.finki.wp.liga.service.basketball.BasketballPlayerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BasketballPlayerServiceImpl implements BasketballPlayerService {

    private final BasketballPlayerRepository basketballPlayerRepository;
    private final BasketballTeamRepository basketballTeamRepository;
    private final FantasyPlayerRepository fantasyPlayerRepository;
    private final BasketballPlayerMatchStatsRepository basketballPlayerMatchStatsRepository;

    @Override
    public List<BasketballPlayer> listAllPlayers() {
        return basketballPlayerRepository.findAll();
    }

    @Override
    public BasketballPlayer findById(Long id) {
        return basketballPlayerRepository.findById(id).orElseThrow(InvalidBasketballPlayerException::new);
    }

    @Override
    @Transactional
    public BasketballPlayer create(byte[] image, String name, String surname, Date birthdate, int index, String city, String position, BasketballTeam team) {
        BasketballTeam playerTeam;
        if(team!=null)
            playerTeam = basketballTeamRepository.findById(team.getId()).orElse(null);
        else playerTeam=null;
        BasketballPlayer player = new BasketballPlayer(image,name, surname, birthdate, index, city, position, playerTeam);
        return basketballPlayerRepository.save(player);
    }

    @Override
    @Transactional
    public BasketballPlayer update(Long id, byte[] image, String name, String surname, Date birthdate, int index, String city, String position, BasketballTeam team) {
        BasketballPlayer p = this.findById(id);
        if(image!=null && image.length>0)
            p.setImage(image);
            p.setName(name);
            p.setSurname(surname);
            p.setBirthdate(birthdate);
            p.setIndex(index);
            p.setCity(city);
            p.setPosition(position);
            BasketballTeam t = basketballTeamRepository.findById(team.getId()).orElseThrow(InvalidBasketballTeamException::new);
            p.setTeam(t);
        return basketballPlayerRepository.save(p);
    }

    @Override
    @Transactional
    public BasketballPlayer update(Long id, byte[] image, String name, String surname, Date birthdate, int index, String city, String position, BasketballTeam team, int appearances, int points, int assists, int rebounds, Double price) {
        BasketballPlayer p = this.findById(id);
        if(image!=null && image.length>0)
            p.setImage(image);
            p.setName(name);
            p.setSurname(surname);
            p.setBirthdate(birthdate);
            p.setIndex(index);
            p.setCity(city);
            p.setPosition(position);
            BasketballTeam t = basketballTeamRepository.findById(team.getId()).orElseThrow(InvalidBasketballTeamException::new);
            p.setTeam(t);
            p.setAppearances(appearances);
            p.setPoints(points);
            p.setAssists(assists);
            p.setRebounds(rebounds);
            
            // Calculate dynamic price based on stats
            p.calculateDynamicPrice();
            
        return basketballPlayerRepository.save(p);
    }

    @Override
    @Transactional
    public BasketballPlayer delete(Long id) {
        BasketballPlayer p = this.findById(id);
        
        // First, delete any fantasy players that reference this basketball player
        List<FantasyPlayer> fantasyPlayers = fantasyPlayerRepository.findAll().stream()
                .filter(fp -> fp.getBasketballPlayer() != null && fp.getBasketballPlayer().getBasketball_player_id().equals(id))
                .collect(Collectors.toList());
        
        if (!fantasyPlayers.isEmpty()) {
            fantasyPlayerRepository.deleteAll(fantasyPlayers);
        }
        
        // Second, delete any match stats that reference this basketball player
        List<BasketballPlayerMatchStats> matchStats = basketballPlayerMatchStatsRepository.findByPlayer(p);
        if (!matchStats.isEmpty()) {
            basketballPlayerMatchStatsRepository.deleteAll(matchStats);
        }
        
        // Now delete the basketball player
        basketballPlayerRepository.delete(p);
        
        return p;
    }

    @Override
    public BasketballPlayer addAppearances(Long Id) {
        BasketballPlayer p = this.findById(Id);
        int appearances = p.getAppearances();
        p.setAppearances(appearances+1);
        return basketballPlayerRepository.save(p);
    }

    @Override
    public BasketballPlayer addPoints(Long Id, int pointsToAdd) {
        BasketballPlayer p = this.findById(Id);
        int points = p.getPoints();
        p.setPoints(points+pointsToAdd);
        return basketballPlayerRepository.save(p);
    }

    @Override
    public BasketballPlayer addAssists(Long Id, int assistsToAdd) {
        BasketballPlayer p = this.findById(Id);
        int assists = p.getAssists();
        p.setAssists(assists+assistsToAdd);
        return basketballPlayerRepository.save(p);
    }

    @Override
    public BasketballPlayer addRebounds(Long Id, int reboundsToAdd) {
        BasketballPlayer p = this.findById(Id);
        int rebounds = p.getRebounds();
        p.setRebounds(rebounds+reboundsToAdd);
        return basketballPlayerRepository.save(p);
    }

    @Override
    public List<BasketballPlayer> getPlayersByIds(List<Long> ids) {
        return basketballPlayerRepository.findAllById(ids);
    }

    @Override
    public List<BasketballPlayer> getTop5Players() {
        return this.basketballPlayerRepository.findAll()
                .stream().sorted((p1,p2)->{
                    int score1 = p1.getTotalPoints();
                    int score2 = p2.getTotalPoints();
                    return Integer.compare(score2,score1);
                })
                .limit(5)
                .collect(Collectors.toList());
    }

    @Override
    public List<BasketballPlayer> getTop5PlayersByTeam(Long teamId) {
        return this.basketballPlayerRepository.findByTeamId(teamId)
                .stream()
                .sorted((p1, p2) -> {
                    int score1 = p1.getTotalPoints();
                    int score2 = p2.getTotalPoints();
                    return Integer.compare(score2, score1); // Descending order
                })
                .limit(5)
                .collect(Collectors.toList());
    }
    @Override
    @Transactional
    public void addStats(Long playerId, int basketsToAdd) {
        // Find the player by ID
        BasketballPlayer player = basketballPlayerRepository.findById(playerId).orElseThrow(InvalidBasketballPlayerException::new);
            // Add the provided stats to the player's existing stats
            player.setPoints(player.getPoints() + basketsToAdd);

            // Save the updated player
            basketballPlayerRepository.save(player);
        }

    @Override
    @Transactional
    public void recalculateAllPlayerPrices() {
        List<BasketballPlayer> allPlayers = basketballPlayerRepository.findAll();
        for (BasketballPlayer player : allPlayers) {
            player.calculateDynamicPrice();
            basketballPlayerRepository.save(player);
        }
    }
}
