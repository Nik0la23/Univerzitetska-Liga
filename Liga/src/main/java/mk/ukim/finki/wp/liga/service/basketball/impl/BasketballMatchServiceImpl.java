package mk.ukim.finki.wp.liga.service.basketball.impl;

import lombok.AllArgsConstructor;
import mk.ukim.finki.wp.liga.model.*;
import mk.ukim.finki.wp.liga.model.Exceptions.InvalidBasketballMatchException;
import mk.ukim.finki.wp.liga.model.Exceptions.InvalidBasketballTeamException;
import mk.ukim.finki.wp.liga.model.Exceptions.InvalidFootballMatchException;
import mk.ukim.finki.wp.liga.repository.basketball.BasketballMatchRepository;
import mk.ukim.finki.wp.liga.repository.basketball.BasketballPlayerRepository;
import mk.ukim.finki.wp.liga.repository.basketball.BasketballTeamRepository;
import mk.ukim.finki.wp.liga.service.basketball.BasketballMatchService;
import mk.ukim.finki.wp.liga.service.basketball.BasketballPlayerService;
import mk.ukim.finki.wp.liga.service.basketball.BasketballTeamService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BasketballMatchServiceImpl implements BasketballMatchService {

    private final BasketballMatchRepository basketballMatchRepository;
    private final BasketballTeamRepository basketballTeamRepository;
    private final BasketballPlayerRepository basketballPlayerRepository;
    private final BasketballTeamService basketballTeamService;
    private final BasketballPlayerService basketballPlayerService;


    @Override
    @Transactional(readOnly = true)
    public List<BasketballMatch> listAllBasketballMatches() {
        return basketballMatchRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public BasketballMatch findById(Long id) {
        return basketballMatchRepository.findById(id).orElseThrow(InvalidBasketballMatchException::new);
    }

    @Override
    @Transactional
    public BasketballMatch create(BasketballTeam homeTeam, BasketballTeam awayTeam, int homeTeamPoints, int awayTeamPoints, LocalDateTime startTime) {
        BasketballTeam home = basketballTeamRepository.findById(homeTeam.getId()).orElseThrow(InvalidBasketballTeamException::new);
        BasketballTeam away = basketballTeamRepository.findById(awayTeam.getId()).orElseThrow(InvalidBasketballTeamException::new);
        BasketballMatch bm = new BasketballMatch(home, away, homeTeamPoints, awayTeamPoints, startTime, false);
        return basketballMatchRepository.save(bm);
    }

    @Override
    @Transactional
    public BasketballMatch update(Long id, BasketballTeam homeTeam, BasketballTeam awayTeam, int homeTeamPoints, int awayTeamPoints, LocalDateTime startTime) {
        BasketballMatch bm = this.findById(id);
        BasketballTeam home = basketballTeamRepository.findById(homeTeam.getId()).orElseThrow(InvalidBasketballTeamException::new);
        BasketballTeam away = basketballTeamRepository.findById(awayTeam.getId()).orElseThrow(InvalidBasketballTeamException::new);
        bm.setHomeTeam(home);
        bm.setAwayTeam(away);
        bm.setHomeTeamPoints(homeTeamPoints);
        bm.setAwayTeamPoints(awayTeamPoints);
        bm.setStartTime(startTime);
        return basketballMatchRepository.save(bm);
    }

    @Override
    @Transactional
    public BasketballMatch updateQuarterPoints(Long id, int quarter, int homeTeamPoints, int awayTeamPoints) {
        BasketballMatch match = this.findById(id);
        
        switch (quarter) {
            case 1:
                match.setHomeTeamQ1Points(homeTeamPoints);
                match.setAwayTeamQ1Points(awayTeamPoints);
                break;
            case 2:
                match.setHomeTeamQ2Points(homeTeamPoints);
                match.setAwayTeamQ2Points(awayTeamPoints);
                break;
            case 3:
                match.setHomeTeamQ3Points(homeTeamPoints);
                match.setAwayTeamQ3Points(awayTeamPoints);
                break;
            case 4:
                match.setHomeTeamQ4Points(homeTeamPoints);
                match.setAwayTeamQ4Points(awayTeamPoints);
                break;
            default:
                throw new IllegalArgumentException("Invalid quarter number: " + quarter);
        }
        
        // Update total points
        int totalHomePoints = match.getHomeTeamQ1Points() + match.getHomeTeamQ2Points() + 
                            match.getHomeTeamQ3Points() + match.getHomeTeamQ4Points();
        int totalAwayPoints = match.getAwayTeamQ1Points() + match.getAwayTeamQ2Points() + 
                            match.getAwayTeamQ3Points() + match.getAwayTeamQ4Points();
        
        match.setHomeTeamPoints(totalHomePoints);
        match.setAwayTeamPoints(totalAwayPoints);
        
        return basketballMatchRepository.save(match);
    }

    @Override
    public Map<Integer, int[]> getQuarterPoints(Long id) {
        BasketballMatch match = this.findById(id);
        Map<Integer, int[]> quarterPoints = new HashMap<>();
        
        quarterPoints.put(1, new int[]{match.getHomeTeamQ1Points(), match.getAwayTeamQ1Points()});
        quarterPoints.put(2, new int[]{match.getHomeTeamQ2Points(), match.getAwayTeamQ2Points()});
        quarterPoints.put(3, new int[]{match.getHomeTeamQ3Points(), match.getAwayTeamQ3Points()});
        quarterPoints.put(4, new int[]{match.getHomeTeamQ4Points(), match.getAwayTeamQ4Points()});
        
        return quarterPoints;
    }

    @Override
    @Transactional
    public BasketballMatch delete(Long id) {
        BasketballMatch bm = basketballMatchRepository.findById(id).orElseThrow(InvalidBasketballMatchException::new);
        if(bm.getEndTime().isBefore(LocalDateTime.now())){
            bm.getHomeTeam().getBasketballResults().remove(bm);
            bm.getAwayTeam().getBasketballResults().remove(bm);
        }
        else {
            bm.getHomeTeam().getBasketballFixtures().remove(bm);
            bm.getAwayTeam().getBasketballFixtures().remove(bm);
        }
        basketballMatchRepository.delete(bm);
        return bm;
    }

//    @Override
//    @Transactional
//    public void updateTeamStatistics(BasketballMatch match) {
//        basketballTeamService.updateStats(match.getHomeTeam().getId());
//        basketballTeamService.updateStats(match.getAwayTeam().getId());
//    }

    @Override
    @Transactional
    public BasketballMatch createAndAddToFixtures(BasketballTeam homeTeam, BasketballTeam awayTeam, int homeTeamPoints, int awayTeamPoints, LocalDateTime startTime) {
        BasketballMatch match = this.create(homeTeam, awayTeam, homeTeamPoints, awayTeamPoints, startTime);

        if(startTime.plusHours(2).isAfter(LocalDateTime.now())) {
            homeTeam.getBasketballFixtures().add(match);
            awayTeam.getBasketballFixtures().add(match);
        } else if(startTime.plusHours(2).isBefore(LocalDateTime.now())){
            homeTeam.getBasketballResults().add(match);
            awayTeam.getBasketballResults().add(match);
        }


        basketballTeamRepository.save(homeTeam);
        basketballTeamRepository.save(awayTeam);

        return match;
    }

    @Override
    @Transactional
    public List<BasketballMatch> createPlayoffMatches() {
        List<BasketballMatch> existingPlayoffMatches = basketballMatchRepository.findAllByIsPlayoffMatchTrue();

        // If matches exist, don't reinitialize, just return the existing ones
        if (!existingPlayoffMatches.isEmpty()) {
            return existingPlayoffMatches;
        }
        List<BasketballTeam> teams = basketballTeamRepository.findAllByOrderByTeamLeaguePointsDesc();

        if (teams.size() < 8) {
            throw new RuntimeException("Not enough teams for playoffs to be created");
        }

        // Clear existing playoff matches if needed
        basketballMatchRepository.deleteAllByIsPlayoffMatchTrue();

        // Create the quarter-final matches
        List<BasketballMatch> matches = new ArrayList<>();
        matches.add(createMatch(teams.get(0), teams.get(7), true)); // 1 vs 8
        matches.add(createMatch(teams.get(1), teams.get(6), true)); // 2 vs 7
        matches.add(createMatch(teams.get(2), teams.get(5), true)); // 3 vs 6
        matches.add(createMatch(teams.get(3), teams.get(4), true)); // 4 vs 5

        return matches;
    }

    private BasketballMatch createMatch(BasketballTeam homeTeam, BasketballTeam awayTeam, boolean isPlayoffMatch) {
        LocalDateTime startTime = LocalDateTime.now().plusDays(1); // Set the start time for the match
        BasketballMatch match = new BasketballMatch(homeTeam, awayTeam, 0, 0, startTime, isPlayoffMatch);
        return basketballMatchRepository.save(match);
    }

    @Override
    public List<BasketballMatch> listPlayoffMatches() {
        List<BasketballTeam> teams = basketballTeamRepository.findAllByOrderByTeamLeaguePointsDesc();

        if (teams.size() < 8) {
            throw new RuntimeException("Not enough teams for playoffs to be created");
        }

        // Fetch all matches involving the top 8 teams
        return basketballMatchRepository.findAllByIsPlayoffMatchTrue();
    }

    @Override
    @Transactional
    public BasketballMatch updatePlayoffMatchPoints(Long id, BasketballTeam homeTeam, BasketballTeam awayTeam, int homeTeamPoints, int awayTeamPoints, List<BasketballPlayer> homeScorers, List<BasketballPlayer> awayScorers) {
        BasketballMatch match = basketballMatchRepository.findById(id).orElseThrow(InvalidBasketballMatchException::new);
        match.setHomeTeam(homeTeam);
        match.setAwayTeam(awayTeam);
        match.setHomeTeamPoints(homeTeamPoints);
        match.setAwayTeamPoints(awayTeamPoints);
        distributeStats(homeScorers, homeTeamPoints);
        distributeStats(awayScorers, awayTeamPoints);
        return basketballMatchRepository.save(match);
    }
    private void distributeStats(List<BasketballPlayer> selectedPlayers, int totalBaskets) {
        if (selectedPlayers == null || selectedPlayers.isEmpty()) {
            return;
        }

        int numberOfPlayers = selectedPlayers.size();

        // Edge case: If there is only one player, assign all stats to that player
        if (numberOfPlayers == 1) {
            basketballPlayerService.addStats(selectedPlayers.get(0).getBasketball_player_id(), totalBaskets);
            return;
        }

        // Calculate stats per player
        int basketsPerPlayer = totalBaskets / numberOfPlayers;

        // Handle remaining stats
        int remainingBaskets = totalBaskets % numberOfPlayers;

        // Distribute the stats
        for (BasketballPlayer selectedPlayer : selectedPlayers) {
            int basketsForThisPlayer = basketsPerPlayer;

            // Distribute remaining stats across the first few players
            if (remainingBaskets > 0) {
                basketsForThisPlayer++;
                remainingBaskets--;
            }
            // Use the addStats method from BasketballPlayerService to update stats
            basketballPlayerService.addStats(selectedPlayer.getBasketball_player_id(), basketsForThisPlayer);
        }
    }

    private BasketballTeam getWinner(BasketballMatch match) {
        if (match.getHomeTeamPoints() > match.getAwayTeamPoints()) {
            return match.getHomeTeam();
        } else if (match.getAwayTeamPoints() > match.getHomeTeamPoints()) {
            return match.getAwayTeam();
        } else {
            throw new RuntimeException("Match cannot end in a draw in playoffs");
        }
    }
    private boolean isMatchCompleted(BasketballMatch match) {
        return match.getHomeTeamPoints() != match.getAwayTeamPoints();
    }

    @Override
    @Transactional
    public List<BasketballMatch> createSemiFinalMatches() {
        List<BasketballMatch> playoffMatches = basketballMatchRepository.findAllByIsPlayoffMatchTrue();

        // Check if the final match already exists (total playoff matches should be 7 if final is created)
        if (playoffMatches.size() >= 6) {
            //System.out.println("Semi-final matches have already been created.");
            return playoffMatches.subList(4, 6); // Return existing final match
        }
        List<BasketballMatch> quarterFinals = basketballMatchRepository.findAllByIsPlayoffMatchTrue();

        // Ensure all quarter-final matches are completed
        if (quarterFinals.size() != 4 || quarterFinals.stream().anyMatch(match -> !isMatchCompleted(match))) {
            throw new RuntimeException("Quarter-finals are not fully completed yet.");
        }

        // Determine the winners of the quarter-finals
        BasketballTeam semiFinalTeam1 = getWinner(quarterFinals.get(0));
        BasketballTeam semiFinalTeam2 = getWinner(quarterFinals.get(1));
        BasketballTeam semiFinalTeam3 = getWinner(quarterFinals.get(2));
        BasketballTeam semiFinalTeam4 = getWinner(quarterFinals.get(3));

        // Create semi-final matches
        List<BasketballMatch> semiFinals = new ArrayList<>();
        semiFinals.add(createMatch(semiFinalTeam1, semiFinalTeam4, true));
        semiFinals.add(createMatch(semiFinalTeam2, semiFinalTeam3, true));

        return semiFinals;
    }

    @Override
    @Transactional
    public List<BasketballMatch> createFinalMatch() {
        List<BasketballMatch> playoffMatches = basketballMatchRepository.findAllByIsPlayoffMatchTrue();

        // Check if the final match already exists (total playoff matches should be 7 if final is created)
        if (playoffMatches.size() >= 7) {
            //System.out.println("The final match has already been created.");
            return playoffMatches.subList(6, 7); // Return existing final match
        }
        List<BasketballMatch> semiFinals = basketballMatchRepository.findAllByIsPlayoffMatchTrue();

        // Ensure all quarter-final matches are completed
        if (semiFinals.size() < 6) {
            throw new RuntimeException("There are not enough matches to have semi-finals and finals.");
        }
        BasketballMatch semiFinal1 = semiFinals.get(semiFinals.size() - 2);
        BasketballMatch semiFinal2 = semiFinals.get(semiFinals.size() - 1);

        // Ensure both semi-final matches are completed
        if (!isMatchCompleted(semiFinal1) || !isMatchCompleted(semiFinal2)) {
            throw new RuntimeException("Semi-finals are not fully completed yet.");
        }

        // Determine the winners of the quarter-finals
        BasketballTeam finalist1 = getWinner(semiFinal1);
        BasketballTeam finalist2 = getWinner(semiFinal2);

        // Create the final match
        List<BasketballMatch> finals = new ArrayList<>();
        finals.add(createMatch(finalist1, finalist2, true));

        return finals;
    }

    @Override
    public void updateLiveStats(Long basketballMatchId, int pointsScored, Long playerId) {
        BasketballPlayer player = basketballPlayerRepository.getReferenceById(playerId);
        BasketballMatch match = basketballMatchRepository.findById(basketballMatchId).get();
        BasketballTeam team = basketballTeamRepository.findAll().stream().filter(t -> t.getPlayers().contains(player)).findFirst().get();

        BasketballTeam homeOrAway = match.getHomeTeam();
        int ptsHome=match.getHomeTeamPoints();
        int ptsAway=match.getAwayTeamPoints();
        if (team != homeOrAway) {
            match.setAwayTeamPoints(pointsScored + ptsAway);
        } else {
            match.setHomeTeamPoints(pointsScored + ptsHome);
        }

        basketballMatchRepository.save(match);
    }
    @Override
    @Transactional
    public  Map<LocalDate, List<BasketballMatch>> groupMatchesByDate(List<BasketballMatch> matches) {
        Map<LocalDate, List<BasketballMatch>> groupedMatches = matches.stream()
                .collect(Collectors.groupingBy(match -> match.getStartTime().toLocalDate()));

        // Sort the entries by date in descending order and collect into a LinkedHashMap
        return groupedMatches.entrySet()
                .stream()
                .sorted(Map.Entry.<LocalDate, List<BasketballMatch>>comparingByKey(Comparator.naturalOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }
    @Override
    @Transactional
    public void finishMatch(Long matchId) {
        BasketballMatch match = basketballMatchRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid match Id:" + matchId));

        match.setEndTime(LocalDateTime.now());
        match.getHomeTeam().getBasketballFixtures().remove(match);
        match.getAwayTeam().getBasketballFixtures().remove(match);
        processMatchStats(matchId);
        basketballMatchRepository.save(match);
    }
    @Override
    @Transactional
    public void processMatchStats(Long matchId) {
        BasketballMatch match = basketballMatchRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid match ID"));

        BasketballTeam homeTeam = match.getHomeTeam();
        BasketballTeam awayTeam = match.getAwayTeam();

        // Increment matches played
        basketballTeamService.incrementMatchesPlayed(homeTeam.getId());
        basketballTeamService.incrementMatchesPlayed(awayTeam.getId());


        if (match.getHomeTeamPoints() > match.getAwayTeamPoints()) {
            // Home team wins
            basketballTeamService.addWin(homeTeam.getId());
            basketballTeamService.addLoss(awayTeam.getId());
            basketballTeamService.addPoints(homeTeam.getId(), 3);
        } else if (match.getHomeTeamPoints() < match.getAwayTeamPoints()) {
            // Away team wins
            basketballTeamService.addLoss(homeTeam.getId());
            basketballTeamService.addWin(awayTeam.getId());
            basketballTeamService.addPoints(awayTeam.getId(), 3);
        }

        if (match.getHomeTeamPoints() > match.getAwayTeamPoints()) {
            homeTeam.addMatchResult("W");
            awayTeam.addMatchResult("L");
        } else if (match.getHomeTeamPoints() < match.getAwayTeamPoints()) {
            homeTeam.addMatchResult("L");
            awayTeam.addMatchResult("W");
        } else {
            homeTeam.addMatchResult("D");
            awayTeam.addMatchResult("D");
        }
        basketballTeamRepository.save(homeTeam);
        basketballTeamRepository.save(awayTeam);
    }
    private void updateBasketballTeamStats(BasketballTeam team, boolean isWin) {
        // Increment the number of matches played
        team.setTeamMatchesPlayed(team.getTeamMatchesPlayed() + 1);

        // Add a win or a loss
        if (isWin) {
            team.setTeamWins(team.getTeamWins() + 1);
            team.setTeamLeaguePoints(team.getTeamLeaguePoints() + 3); // 3 points for a win
        } else {
            team.setTeamLoses(team.getTeamLoses() + 1);
            // No points for a loss
        }

        // Save the updated team
        basketballTeamRepository.save(team);
    }

}
