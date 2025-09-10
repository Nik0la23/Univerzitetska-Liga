package mk.ukim.finki.wp.liga.service.volleyball.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import mk.ukim.finki.wp.liga.model.Exceptions.InvalidVolleyballPlayerException;
import mk.ukim.finki.wp.liga.model.Exceptions.InvalidVolleyballTeamException;
import mk.ukim.finki.wp.liga.model.VolleyballPlayer;
import mk.ukim.finki.wp.liga.model.VolleyballPlayerMatchStats;
import mk.ukim.finki.wp.liga.model.VolleyballTeam;
import mk.ukim.finki.wp.liga.model.fantasy.FantasyPlayer;
import mk.ukim.finki.wp.liga.repository.fantasy.FantasyPlayerRepository;
import mk.ukim.finki.wp.liga.repository.volleyball.VolleyballPlayerMatchStatsRepository;
import mk.ukim.finki.wp.liga.repository.volleyball.VolleyballPlayerRepository;
import mk.ukim.finki.wp.liga.repository.volleyball.VolleyballTeamRepository;
import mk.ukim.finki.wp.liga.service.volleyball.VolleyballPlayerService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class VolleyballPlayerServiceImpl implements VolleyballPlayerService {

    private final VolleyballPlayerRepository volleyballPlayerRepository;
    private final VolleyballTeamRepository volleyballTeamRepository;
    private final FantasyPlayerRepository fantasyPlayerRepository;
    private final VolleyballPlayerMatchStatsRepository volleyballPlayerMatchStatsRepository;

    @Override
    public List<VolleyballPlayer> listAllPlayers() {
        return volleyballPlayerRepository.findAll();
    }

    @Override
    public VolleyballPlayer findById(Long id) {
        return volleyballPlayerRepository.findById(id).orElseThrow(InvalidVolleyballPlayerException::new);
    }

    @Override
    @Transactional
    public VolleyballPlayer create(byte[] image, String name, String surname, Date birthdate, int index, String city, String position, VolleyballTeam team) {
        VolleyballTeam playerTeam;
        if (team != null)
            playerTeam = volleyballTeamRepository.findById(team.getVolleyball_team_id()).orElseThrow(InvalidVolleyballTeamException::new);
        else
            playerTeam = null;
        VolleyballPlayer player = new VolleyballPlayer(image, name, surname, birthdate, index, city, position, playerTeam);
        return volleyballPlayerRepository.save(player);
    }

    @Override
    @Transactional
    public VolleyballPlayer update(Long id, byte[] image, String name, String surname, Date birthdate, int index, String city, String position, VolleyballTeam team) {
        VolleyballPlayer p = this.findById(id);
        if (image != null && image.length > 0)
            p.setImage(image);
        p.setName(name);
        p.setSurname(surname);
        p.setBirthdate(birthdate);
        p.setIndex(index);
        p.setCity(city);
        p.setPosition(position);
        VolleyballTeam t = volleyballTeamRepository.findById(team.getVolleyball_team_id()).orElseThrow(InvalidVolleyballTeamException::new);
        p.setTeam(t);
        return volleyballPlayerRepository.save(p);
    }

    @Override
    @Transactional
    public VolleyballPlayer update(Long id, byte[] image, String name, String surname, Date birthdate, int index, String city, String position, VolleyballTeam team, int appearances, int servings, int assists, int scoredPoints, int blocks, Double price) {
        VolleyballPlayer p = this.findById(id);
        if (image != null && image.length > 0)
            p.setImage(image);
        p.setName(name);
        p.setSurname(surname);
        p.setBirthdate(birthdate);
        p.setIndex(index);
        p.setCity(city);
        p.setPosition(position);
        VolleyballTeam t = volleyballTeamRepository.findById(team.getVolleyball_team_id()).orElseThrow(InvalidVolleyballTeamException::new);
        p.setTeam(t);
        p.setAppearances(appearances);
        p.setServings(servings);
        p.setAssists(assists);
        p.setScoredPoints(scoredPoints);
        p.setBlocks(blocks);
        
        // Calculate dynamic price based on stats
        p.calculateDynamicPrice();
        
        return volleyballPlayerRepository.save(p);
    }

    @Override
    @Transactional
    public VolleyballPlayer delete(Long id) {
        VolleyballPlayer p = this.findById(id);
        
        // First, delete any fantasy players that reference this volleyball player
        List<FantasyPlayer> fantasyPlayers = fantasyPlayerRepository.findAll().stream()
                .filter(fp -> fp.getVolleyballPlayer() != null && fp.getVolleyballPlayer().getVolleyball_player_id().equals(id))
                .collect(Collectors.toList());
        
        if (!fantasyPlayers.isEmpty()) {
            fantasyPlayerRepository.deleteAll(fantasyPlayers);
        }
        
        // Second, delete any match stats that reference this volleyball player
        List<VolleyballPlayerMatchStats> matchStats = volleyballPlayerMatchStatsRepository.findByPlayer(p);
        if (!matchStats.isEmpty()) {
            volleyballPlayerMatchStatsRepository.deleteAll(matchStats);
        }
        
        // Now delete the volleyball player
        volleyballPlayerRepository.delete(p);
        
        return p;
    }

    @Override
    public VolleyballPlayer addAppearances(Long id) {
        VolleyballPlayer p = this.findById(id);
        int appearances = p.getAppearances();
        p.setAppearances(appearances + 1);
        return volleyballPlayerRepository.save(p);
    }

    @Override
    public VolleyballPlayer addPoints(Long id, int pointsToAdd) {
        VolleyballPlayer p = this.findById(id);
        p.setScoredPoints(p.getScoredPoints() + pointsToAdd);
        return volleyballPlayerRepository.save(p);
    }

    @Override
    public VolleyballPlayer addAssists(Long id, int assistsToAdd) {
        VolleyballPlayer p = this.findById(id);
        p.setAssists(p.getAssists() + assistsToAdd);
        return volleyballPlayerRepository.save(p);
    }

    @Override
    public VolleyballPlayer addServings(Long id, int servingsToAdd) {
        VolleyballPlayer p = this.findById(id);
        p.setAssists(p.getServings() + servingsToAdd);
        return volleyballPlayerRepository.save(p);
    }

    @Override
    public VolleyballPlayer addBlocks(Long id, int blocksToAdd) {
        VolleyballPlayer p = this.findById(id);
        p.setBlocks(p.getBlocks() + blocksToAdd);
        return volleyballPlayerRepository.save(p);
    }

    @Override
    public List<VolleyballPlayer> getPlayersByIds(List<Long> ids) {
        return volleyballPlayerRepository.findAllById(ids);
    }

    @Override
    public List<VolleyballPlayer> getTop5Players() {
        return this.volleyballPlayerRepository.findAll()
                .stream().sorted((p1, p2) -> {
                    int score1 = p1.getTotalPoints();
                    int score2 = p2.getTotalPoints();
                    return Integer.compare(score2, score1);
                })
                .limit(5)
                .collect(Collectors.toList());
    }

    @Override
    public List<VolleyballPlayer> getTop5PlayersByTeam(Long teamId) {
        return this.volleyballPlayerRepository.findByTeamId(teamId)
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
    public void recalculateAllPlayerPrices() {
        List<VolleyballPlayer> allPlayers = volleyballPlayerRepository.findAll();
        for (VolleyballPlayer player : allPlayers) {
            player.calculateDynamicPrice();
            volleyballPlayerRepository.save(player);
        }
    }

}
