package mk.ukim.finki.wp.liga.service.volleyball;

import mk.ukim.finki.wp.liga.model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface VolleyballMatchService {
    List<VolleyballMatch> listAllVolleyballMatches();


    VolleyballMatch findById(Long id);


    VolleyballMatch create(VolleyballTeam homeTeam, VolleyballTeam awayTeam, int homeTeamPoints, int awayTeamPoints, LocalDateTime startTime);


    VolleyballMatch update(Long id, VolleyballTeam homeTeam, VolleyballTeam awayTeam, int homeTeamPoints, int awayTeamPoints, LocalDateTime startTime);


    VolleyballMatch delete(Long id);

    void updateTeamStatistics(VolleyballMatch match);

    VolleyballMatch createAndAddToFixtures(VolleyballTeam homeTeam, VolleyballTeam awayTeam, int homeTeamPoints, int awayTeamPoints, LocalDateTime startTime);
    List<VolleyballMatch> createPlayoffMatches();
    List<VolleyballMatch> listPlayoffMatches();
    VolleyballMatch updatePlayoffMatchPoints(Long id, VolleyballTeam homeTeam, VolleyballTeam awayTeam, int homeTeamPoints, int awayTeamPoints, List<VolleyballPlayer> homeScorers, List<VolleyballPlayer> awayScorers);
    List<VolleyballMatch> createSemiFinalMatches();
    List<VolleyballMatch> createFinalMatch();
    void updateLiveStats(Long volleyballMatchId, int pointsScored, Long playerId);
    void finishMatch(Long matchId);
    public void processMatchStats(Long matchId);

        Map<LocalDate, List<VolleyballMatch>> groupMatchesByDate(List<VolleyballMatch> matches);
}
