package mk.ukim.finki.wp.liga.service.volleyball.impl;

import lombok.AllArgsConstructor;
import mk.ukim.finki.wp.liga.model.*;
import mk.ukim.finki.wp.liga.model.Exceptions.InvalidVolleyballMatchException;
import mk.ukim.finki.wp.liga.model.Exceptions.InvalidVolleyballTeamException;
import mk.ukim.finki.wp.liga.repository.volleyball.VolleyballMatchRepository;
import mk.ukim.finki.wp.liga.repository.volleyball.VolleyballPlayerRepository;
import mk.ukim.finki.wp.liga.repository.volleyball.VolleyballTeamRepository;
import mk.ukim.finki.wp.liga.service.volleyball.VolleyballMatchService;
import mk.ukim.finki.wp.liga.service.volleyball.VolleyballPlayerService;
import mk.ukim.finki.wp.liga.service.volleyball.VolleyballTeamService;
import mk.ukim.finki.wp.liga.service.fantasy.FantasyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class VolleyballMatchServiceImpl implements VolleyballMatchService {
    private final VolleyballMatchRepository matchRepository;
    private final VolleyballTeamRepository teamRepository;
    private final VolleyballPlayerRepository playerRepository;
    private final VolleyballTeamService teamService;
    private final VolleyballPlayerService playerService;
    private final FantasyService fantasyService;

    @Override
    @Transactional(readOnly = true)
    public List<VolleyballMatch> listAllVolleyballMatches() {
        return matchRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public VolleyballMatch findById(Long id) {
        return matchRepository.findById(id).orElseThrow(InvalidVolleyballMatchException::new);
    }

    @Override
    @Transactional
    public VolleyballMatch create(VolleyballTeam homeTeam, VolleyballTeam awayTeam, int homeTeamPoints, int awayTeamPoints, LocalDateTime startTime) {
        VolleyballTeam home = teamRepository.findById(homeTeam.getVolleyball_team_id()).orElseThrow(InvalidVolleyballTeamException::new);
        VolleyballTeam away = teamRepository.findById(awayTeam.getVolleyball_team_id()).orElseThrow(InvalidVolleyballMatchException::new);
        VolleyballMatch vm = new VolleyballMatch(home, away, homeTeamPoints, awayTeamPoints, startTime, false);
        return matchRepository.save(vm);
    }

    @Override
    @Transactional
    public VolleyballMatch update(Long id, VolleyballTeam homeTeam, VolleyballTeam awayTeam, 
            int homeTeamPoints, int awayTeamPoints,
            int homeTeamSet1Points, int homeTeamSet2Points, int homeTeamSet3Points, int homeTeamSet4Points, int homeTeamSet5Points,
            int awayTeamSet1Points, int awayTeamSet2Points, int awayTeamSet3Points, int awayTeamSet4Points, int awayTeamSet5Points,
            LocalDateTime startTime, LocalDateTime endTime) {
        VolleyballTeam home = teamRepository.findById(homeTeam.getVolleyball_team_id()).orElseThrow(InvalidVolleyballTeamException::new);
        VolleyballTeam away = teamRepository.findById(awayTeam.getVolleyball_team_id()).orElseThrow(InvalidVolleyballMatchException::new);
        VolleyballMatch vm = matchRepository.findById(id).orElseThrow(InvalidVolleyballMatchException::new);
        
        vm.setHomeTeam(home);
        vm.setAwayTeam(away);
        vm.setHomeTeamPoints(homeTeamPoints);
        vm.setAwayTeamPoints(awayTeamPoints);
        
        // Update set points for home team
        vm.setHomeTeamSet1Points(homeTeamSet1Points);
        vm.setHomeTeamSet2Points(homeTeamSet2Points);
        vm.setHomeTeamSet3Points(homeTeamSet3Points);
        vm.setHomeTeamSet4Points(homeTeamSet4Points);
        vm.setHomeTeamSet5Points(homeTeamSet5Points);
        
        // Update set points for away team
        vm.setAwayTeamSet1Points(awayTeamSet1Points);
        vm.setAwayTeamSet2Points(awayTeamSet2Points);
        vm.setAwayTeamSet3Points(awayTeamSet3Points);
        vm.setAwayTeamSet4Points(awayTeamSet4Points);
        vm.setAwayTeamSet5Points(awayTeamSet5Points);
        
        vm.setStartTime(startTime);
        vm.setEndTime(endTime);
        
        return matchRepository.save(vm);
    }

    @Override
    @Transactional
    public VolleyballMatch delete(Long id) {
        VolleyballMatch vm = matchRepository.findById(id).orElseThrow(InvalidVolleyballMatchException::new);
        if(vm.getEndTime().isBefore(LocalDateTime.now())){
            vm.getHomeTeam().getVolleyballResults().remove(vm);
            vm.getAwayTeam().getVolleyballResults().remove(vm);
        }
        else {
            vm.getHomeTeam().getVolleyballFixtures().remove(vm);
            vm.getAwayTeam().getVolleyballFixtures().remove(vm);
        }
        matchRepository.delete(vm);
        return vm;
    }

    @Override
    @Transactional
    public void updateTeamStatistics(VolleyballMatch match) {
        teamService.updateStats(match.getHomeTeam().getVolleyball_team_id());
        teamService.updateStats(match.getAwayTeam().getVolleyball_team_id());
    }

    @Override
    @Transactional
    public VolleyballMatch createAndAddToFixtures(VolleyballTeam homeTeam, VolleyballTeam awayTeam, int homeTeamPoints, int awayTeamPoints, LocalDateTime startTime) {
        VolleyballMatch match = this.create(homeTeam, awayTeam, homeTeamPoints, awayTeamPoints, startTime);

        if(startTime.plusHours(2).isAfter(LocalDateTime.now())) {
            homeTeam.getVolleyballFixtures().add(match);
            awayTeam.getVolleyballFixtures().add(match);
        } else if(startTime.plusHours(2).isBefore(LocalDateTime.now())){
            homeTeam.getVolleyballResults().add(match);
            awayTeam.getVolleyballResults().add(match);
        }

        teamRepository.save(homeTeam);
        teamRepository.save(awayTeam);

        return match;
    }

    @Override
    @Transactional
    public List<VolleyballMatch> createPlayoffMatches() {
        List<VolleyballMatch> existingPlayoffMatches = matchRepository.findAllByIsVolleyballPlayoffMatchTrue();

        // If matches exist, don't reinitialize, just return the existing ones
        if (!existingPlayoffMatches.isEmpty()) {
            return existingPlayoffMatches;
        }
        List<VolleyballTeam> teams = teamRepository.findAllByOrderByTeamLeaguePointsDesc();

        if (teams.size() < 8) {
            throw new RuntimeException("Not enough teams for playoffs");
        }

        // Clear existing playoff matches if needed
        matchRepository.deleteAllByIsVolleyballPlayoffMatchTrue();

        // Create the quarter-final matches
        List<VolleyballMatch> matches = new ArrayList<>();
        matches.add(createMatch(teams.get(0), teams.get(7), true)); // 1 vs 8
        matches.add(createMatch(teams.get(1), teams.get(6), true)); // 2 vs 7
        matches.add(createMatch(teams.get(2), teams.get(5), true)); // 3 vs 6
        matches.add(createMatch(teams.get(3), teams.get(4), true)); // 4 vs 5

        return matches;
    }

    private VolleyballMatch createMatch(VolleyballTeam homeTeam, VolleyballTeam awayTeam, boolean isPlayoffMatch) {
        LocalDateTime startTime = LocalDateTime.now().plusDays(1); // Set the start time for the match
        VolleyballMatch match = new VolleyballMatch(homeTeam, awayTeam, 0, 0, startTime, isPlayoffMatch);
        return matchRepository.save(match);
    }

    @Override
    //@Transactional(readOnly = true)
    public List<VolleyballMatch> listPlayoffMatches() {
        List<VolleyballTeam> teams = teamRepository.findAllByOrderByTeamLeaguePointsDesc();

        if (teams.size() < 8) {
            throw new RuntimeException("Not enough teams for playoffs");
        }

        // Fetch all matches involving the top 8 teams
        return matchRepository.findAllByIsVolleyballPlayoffMatchTrue();
    }

    @Override
    @Transactional
    public VolleyballMatch updatePlayoffMatchPoints(Long id, VolleyballTeam homeTeam, VolleyballTeam awayTeam, int homeTeamPoints, int awayTeamPoints, List<VolleyballPlayer> homeScorers, List<VolleyballPlayer> awayScorers) {
        VolleyballMatch match = matchRepository.findById(id).orElseThrow(InvalidVolleyballMatchException::new);
        match.setHomeTeam(homeTeam);
        match.setAwayTeam(awayTeam);
        match.setHomeTeamPoints(homeTeamPoints);
        match.setAwayTeamPoints(awayTeamPoints);
        match.setEndTime(LocalDateTime.now());

        // Distribute points to home and away players
        distributePoints(homeScorers, homeTeamPoints);
        distributePoints(awayScorers, awayTeamPoints);

        return matchRepository.save(match);
    }

    private void distributePoints(List<VolleyballPlayer> selectedPlayers, int totalPoints) {
        if (selectedPlayers == null || selectedPlayers.isEmpty()) {
            return;
        }

        int numberOfPlayers = selectedPlayers.size();

        // Edge case: If there is only one player, assign all points to that player
        if (numberOfPlayers == 1) {
            playerService.addPoints(selectedPlayers.get(0).getVolleyball_player_id(), totalPoints);
            return;
        }

        // Calculate points per player
        int pointsPerPlayer = totalPoints / numberOfPlayers;
        int remainingPoints = totalPoints % numberOfPlayers;

        // Distribute the points
        for (VolleyballPlayer selectedPlayer : selectedPlayers) {
            int pointsForThisPlayer = pointsPerPlayer;

            // Assign the remainder to the first few players
            if (remainingPoints > 0) {
                pointsForThisPlayer++;
                remainingPoints--;
            }

            // Use the addPoints method from Volleyball Player Service
            playerService.addPoints(selectedPlayer.getVolleyball_player_id(), pointsForThisPlayer);
        }
    }


    private VolleyballTeam getWinner(VolleyballMatch match) {
        if (match.getHomeTeamPoints() > match.getAwayTeamPoints()) {
            return match.getHomeTeam();
        } else if (match.getAwayTeamPoints() > match.getHomeTeamPoints()) {
            return match.getAwayTeam();
        } else {
            throw new RuntimeException("Match cannot end in a draw in playoffs");
        }
    }

    private boolean isMatchCompleted(VolleyballMatch match) {
        return match.getHomeTeamPoints() != match.getAwayTeamPoints();
    }

    @Override
    @Transactional
    public List<VolleyballMatch> createSemiFinalMatches() {
        List<VolleyballMatch> playoffMatches = matchRepository.findAllByIsVolleyballPlayoffMatchTrue();

        // Check if the final match already exists (total playoff matches should be 7 if final is created)
        if (playoffMatches.size() >= 6) {
            //System.out.println("Semi-final matches have already been created.");
            return playoffMatches.subList(4, 6); // Return existing final match
        }
        List<VolleyballMatch> quarterFinals = matchRepository.findAllByIsVolleyballPlayoffMatchTrue();

        // Ensure all quarter-final matches are completed
        if (quarterFinals.size() != 4 || quarterFinals.stream().anyMatch(match -> !isMatchCompleted(match))) {
            throw new RuntimeException("Quarter-finals are not fully completed yet.");
        }

        // Determine the winners of the quarter-finals
        VolleyballTeam semiFinalTeam1 = getWinner(quarterFinals.get(0));
        VolleyballTeam semiFinalTeam2 = getWinner(quarterFinals.get(1));
        VolleyballTeam semiFinalTeam3 = getWinner(quarterFinals.get(2));
        VolleyballTeam semiFinalTeam4 = getWinner(quarterFinals.get(3));

        // Create semi-final matches
        List<VolleyballMatch> semiFinals = new ArrayList<>();
        semiFinals.add(createMatch(semiFinalTeam1, semiFinalTeam4, true));
        semiFinals.add(createMatch(semiFinalTeam2, semiFinalTeam3, true));

        return semiFinals;
    }

    @Override
    @Transactional
    public List<VolleyballMatch> createFinalMatch() {
        List<VolleyballMatch> playoffMatches = matchRepository.findAllByIsVolleyballPlayoffMatchTrue();

        // Check if the final match already exists (total playoff matches should be 7 if final is created)
        if (playoffMatches.size() >= 7) {
            //System.out.println("The final match has already been created.");
            return playoffMatches.subList(6, 7); // Return existing final match
        }
        List<VolleyballMatch> semiFinals = matchRepository.findAllByIsVolleyballPlayoffMatchTrue();

        // Ensure all quarter-final matches are completed
        if (semiFinals.size() < 6) {
            throw new RuntimeException("There are not enough matches to have semi-finals and finals.");
        }
        VolleyballMatch semiFinal1 = semiFinals.get(semiFinals.size() - 2);
        VolleyballMatch semiFinal2 = semiFinals.get(semiFinals.size() - 1);

        // Ensure both semi-final matches are completed
        if (!isMatchCompleted(semiFinal1) || !isMatchCompleted(semiFinal2)) {
            throw new RuntimeException("Semi-finals are not fully completed yet.");
        }

        // Determine the winners of the quarter-finals
        VolleyballTeam finalist1 = getWinner(semiFinal1);
        VolleyballTeam finalist2 = getWinner(semiFinal2);

        // Create the final match
        List<VolleyballMatch> finals = new ArrayList<>();
        finals.add(createMatch(finalist1, finalist2, true));

        return finals;
    }

    @Override
    public void updateLiveStats(Long volleyballMatchId, int pointsScored, Long playerId, int currentSet) {
        VolleyballPlayer player = playerRepository.getReferenceById(playerId);
        VolleyballMatch match = matchRepository.findById(volleyballMatchId)
                .orElseThrow(() -> new RuntimeException("Match not found"));
        VolleyballTeam team = teamRepository.findAll().stream()
                .filter(t -> t.getPlayers().contains(player))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Team not found"));

        boolean isHomeTeam = match.getHomeTeam().equals(team);

        // Update the points for the specific set by adding to existing points
        switch (currentSet) {
            case 1:
                if (isHomeTeam) {
                    match.setHomeTeamSet1Points(match.getHomeTeamSet1Points() + pointsScored);
                } else {
                    match.setAwayTeamSet1Points(match.getAwayTeamSet1Points() + pointsScored);
                }
                break;
            case 2:
                if (isHomeTeam) {
                    match.setHomeTeamSet2Points(match.getHomeTeamSet2Points() + pointsScored);
                } else {
                    match.setAwayTeamSet2Points(match.getAwayTeamSet2Points() + pointsScored);
                }
                break;
            case 3:
                if (isHomeTeam) {
                    match.setHomeTeamSet3Points(match.getHomeTeamSet3Points() + pointsScored);
                } else {
                    match.setAwayTeamSet3Points(match.getAwayTeamSet3Points() + pointsScored);
                }
                break;
            case 4:
                if (isHomeTeam) {
                    match.setHomeTeamSet4Points(match.getHomeTeamSet4Points() + pointsScored);
                } else {
                    match.setAwayTeamSet4Points(match.getAwayTeamSet4Points() + pointsScored);
                }
                break;
            case 5:
                if (isHomeTeam) {
                    match.setHomeTeamSet5Points(match.getHomeTeamSet5Points() + pointsScored);
                } else {
                    match.setAwayTeamSet5Points(match.getAwayTeamSet5Points() + pointsScored);
                }
                break;
        }

        // Calculate total sets won
        int homeSetsWon = 0;
        int awaySetsWon = 0;

        // Check Set 1 (to 25, win by 2)
        int set1Result = determineSetWinner(1, match.getHomeTeamSet1Points(), match.getAwayTeamSet1Points());
        if (set1Result == 1) homeSetsWon++;
        else if (set1Result == -1) awaySetsWon++;

        // Check Set 2 (to 25, win by 2)
        int set2Result = determineSetWinner(2, match.getHomeTeamSet2Points(), match.getAwayTeamSet2Points());
        if (set2Result == 1) homeSetsWon++;
        else if (set2Result == -1) awaySetsWon++;

        // Check Set 3 (to 25, win by 2)
        int set3Result = determineSetWinner(3, match.getHomeTeamSet3Points(), match.getAwayTeamSet3Points());
        if (set3Result == 1) homeSetsWon++;
        else if (set3Result == -1) awaySetsWon++;

        // Check Set 4 (to 25, win by 2)
        int set4Result = determineSetWinner(4, match.getHomeTeamSet4Points(), match.getAwayTeamSet4Points());
        if (set4Result == 1) homeSetsWon++;
        else if (set4Result == -1) awaySetsWon++;

        // Check Set 5 (tie-break to 15, win by 2)
        int set5Result = determineSetWinner(5, match.getHomeTeamSet5Points(), match.getAwayTeamSet5Points());
        if (set5Result == 1) homeSetsWon++;
        else if (set5Result == -1) awaySetsWon++;

        // Update total sets won
        match.setHomeTeamPoints(homeSetsWon);
        match.setAwayTeamPoints(awaySetsWon);

        // Check if match is finished (one team has won 3 sets)
        if (homeSetsWon >= 3 || awaySetsWon >= 3) {
            match.setEndTime(LocalDateTime.now());
        }

        matchRepository.save(match);
    }

    private int determineSetWinner(int setIndex, int homePoints, int awayPoints) {
        int minPointsToWin = (setIndex == 5) ? 15 : 25;
        if (homePoints >= minPointsToWin && (homePoints - awayPoints) >= 2) {
            return 1;
        }
        if (awayPoints >= minPointsToWin && (awayPoints - homePoints) >= 2) {
            return -1;
        }
        return 0;
    }
    @Override
    @Transactional
    public Map<LocalDate, List<VolleyballMatch>> groupMatchesByDate(List<VolleyballMatch> matches) {
        Map<LocalDate, List<VolleyballMatch>> groupedMatches = matches.stream()
                .collect(Collectors.groupingBy(match -> match.getStartTime().toLocalDate()));

        // Sort the entries by date in descending order and collect into a LinkedHashMap
        return groupedMatches.entrySet()
                .stream().sorted(Map.Entry.<LocalDate, List<VolleyballMatch>>comparingByKey(Comparator.naturalOrder()))
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
        VolleyballMatch match = matchRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid match Id: " + matchId));
        match.setEndTime(LocalDateTime.now());
        match.getHomeTeam().getVolleyballFixtures().remove(match);
        match.getAwayTeam().getVolleyballFixtures().remove(match);
        matchRepository.save(match);
        processMatchStats(matchId);
    }
    @Override
    @Transactional
    public void processMatchStats(Long matchId) {
        VolleyballMatch match = matchRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid match ID"));

        VolleyballTeam homeTeam = match.getHomeTeam();
        VolleyballTeam awayTeam = match.getAwayTeam();

        // Increment matches played
        teamService.incrementMatchesPlayed(homeTeam.getVolleyball_team_id());
        teamService.incrementMatchesPlayed(awayTeam.getVolleyball_team_id());

        if (match.getHomeTeamPoints() > match.getAwayTeamPoints()) {
            // Home team wins
            teamService.addWin(homeTeam.getVolleyball_team_id());
            teamService.addLoss(awayTeam.getVolleyball_team_id());
            teamService.addPoints(homeTeam.getVolleyball_team_id(), 3);
        } else if (match.getHomeTeamPoints() < match.getAwayTeamPoints()) {
            // Away team wins
            teamService.addLoss(homeTeam.getVolleyball_team_id());
            teamService.addWin(awayTeam.getVolleyball_team_id());
            teamService.addPoints(awayTeam.getVolleyball_team_id(), 3);
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
        teamRepository.save(homeTeam);
        teamRepository.save(awayTeam);
        
        // Calculate and award fantasy points after match completion
        try {
            fantasyService.calculateAndAwardVolleyballFantasyPoints(matchId);
        } catch (Exception e) {
            // Log error but don't fail the match processing
            System.err.println("Error calculating volleyball fantasy points for match " + matchId + ": " + e.getMessage());
        }
    }

    private void updateTeamStats(VolleyballTeam team, boolean isWin) {
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
        teamRepository.save(team);
    }


}
