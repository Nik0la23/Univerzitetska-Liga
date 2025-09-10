package mk.ukim.finki.wp.liga.service.basketball.impl;

import lombok.AllArgsConstructor;
import mk.ukim.finki.wp.liga.model.*;
import mk.ukim.finki.wp.liga.model.Exceptions.InvalidBasketballTeamException;
import mk.ukim.finki.wp.liga.model.dtos.BasketballTeamStandings;
import mk.ukim.finki.wp.liga.repository.basketball.BasketballTeamRepository;
import mk.ukim.finki.wp.liga.service.basketball.BasketballTeamService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BasketballTeamServiceImpl implements BasketballTeamService {

    private final BasketballTeamRepository basketballTeamRepository;


    @Override
    @Transactional(readOnly = true)
    public List<BasketballTeam> listAllTeams() {
        return basketballTeamRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public BasketballTeam findById(Long id) {
        return basketballTeamRepository.findById(id).orElseThrow(InvalidBasketballTeamException::new);
    }

    @Override
    @Transactional
    public BasketballTeam create(String teamName, byte[] logo) {
        BasketballTeam team = new BasketballTeam(teamName, logo);
        return basketballTeamRepository.save(team);
    }

    @Override
    @Transactional
    public BasketballTeam update(Long id, String teamName, List<BasketballPlayer> players, byte[] logo) {
        BasketballTeam team = basketballTeamRepository.findById(id).orElseThrow(InvalidBasketballTeamException::new);
        team.setTeamName(teamName);
        team.setPlayers(players);
        team.setLogo(logo);
        return basketballTeamRepository.save(team);
    }

    @Override
    @Transactional
    public BasketballTeam delete(Long id) {
        BasketballTeam team = basketballTeamRepository.findById(id).orElseThrow(InvalidBasketballTeamException::new);
        basketballTeamRepository.delete(team);
        return team;
    }

    @Override
    @Transactional
    public BasketballTeam addFixtures(Long id, List<BasketballMatch> fixtures) {
        BasketballTeam team = basketballTeamRepository.findById(id).orElseThrow(InvalidBasketballTeamException::new);

        team.getBasketballFixtures().addAll(fixtures);
        return basketballTeamRepository.save(team);
    }

//    @Override
//    @Transactional
//    public BasketballTeam updateStats(Long id) {
//        BasketballTeam team = basketballTeamRepository.findById(id).orElseThrow(InvalidBasketballTeamException::new);
//        int wins = 0;
//        int loses = 0;
//        int leaguePoints = 0;
//
//        for (BasketballMatch match : team.getBasketballResults()) {
//            if (match.getEndTime().isBefore(java.time.LocalDateTime.now())) { // Match has been played
//                boolean isHomeTeam = match.getHomeTeam().equals(team);
//                int teamPoints = isHomeTeam ? match.getHomeTeamPoints() : match.getAwayTeamPoints();
//                int opponentPoints = isHomeTeam ? match.getAwayTeamPoints() : match.getHomeTeamPoints();
//
//                if (teamPoints > opponentPoints) {
//                    wins++;
//                    leaguePoints += 3; // Assuming a win gives 3 points
//                } else {
//                    loses++;
//                }
//            }
//        }
//
//        team.setTeamMatchesPlayed(wins + loses);
//        team.setTeamWins(wins);
//        team.setTeamLoses(loses);
//        team.setTeamLeaguePoints(leaguePoints);
//
//        return basketballTeamRepository.save(team);
//    }

    @Override
    @Transactional(readOnly = true)
    public BasketballTeam findByName(String teamName) {
        return basketballTeamRepository.findBasketballTeamByTeamName(teamName).get(0);
    }

    @Override
    @Transactional
    public BasketballTeam saveTable(Long id, String teamName, byte [] logo) {
        BasketballTeam bt = basketballTeamRepository.findById(id).orElseThrow(InvalidBasketballTeamException::new);
        bt.setTeamName(teamName);
        if(logo!=null && logo.length>0){
            bt.setLogo(logo);
        }
        return basketballTeamRepository.save(bt);
    }

    @Override
    @Transactional
    public List<BasketballTeam> findAllOrderByPointsDesc() {
        return basketballTeamRepository.findAllByOrderByTeamLeaguePointsDesc();
    }
    @Override
    @Transactional
    public void incrementMatchesPlayed(Long teamId) {
        BasketballTeam team = basketballTeamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid team ID"));
        team.setTeamMatchesPlayed(team.getTeamMatchesPlayed() + 1);
        basketballTeamRepository.save(team);
    }

    @Override
    @Transactional
    public void addWin(Long teamId) {
        BasketballTeam team = basketballTeamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid team ID"));

        team.setTeamWins(team.getTeamWins() + 1);
        basketballTeamRepository.save(team);
    }

    @Override
    @Transactional
    public void addLoss(Long teamId) {
        BasketballTeam team = basketballTeamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid team ID"));

        team.setTeamLoses(team.getTeamLoses() + 1);
        basketballTeamRepository.save(team);
    }

//    @Override
//    @Transactional
//    public void addDraw(Long teamId) {
//        BasketballTeam team = basketballTeamRepository.findById(teamId)
//                .orElseThrow(() -> new IllegalArgumentException("Invalid team ID"));
//
//        team.setTeamDraws(team.getTeamDraws() + 1);
//        basketballTeamRepository.save(team);
//    }
    @Override
    @Transactional
    public void addPoints(Long teamId, int points) {
        BasketballTeam team = basketballTeamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid team ID"));

        team.setTeamLeaguePoints(team.getTeamLeaguePoints() + points);
        basketballTeamRepository.save(team);
    }
    @Override
    @Transactional
    public List<BasketballTeamStandings> getStandings() {
        List<BasketballTeam> teams = basketballTeamRepository.findAll();

        return teams.stream()
                .map(team -> new BasketballTeamStandings(
                        team.getTeamName(),
                        team.getTeamMatchesPlayed(),
                        team.getTeamWins(),
                        team.getTeamLoses(),
                        team.getLastFiveMatches()))
                .sorted(Comparator.comparingInt(BasketballTeamStandings::getWins).reversed())
                .collect(Collectors.toList());
    }

}
