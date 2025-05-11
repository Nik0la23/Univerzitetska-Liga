package mk.ukim.finki.wp.liga.service.basketball;

import mk.ukim.finki.wp.liga.model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface BasketballMatchService {
    List<BasketballMatch> listAllBasketballMatches();
    BasketballMatch findById(Long id);
    BasketballMatch create(BasketballTeam homeTeam, BasketballTeam awayTeam, int homeTeamPoints, int awayTeamPoints, LocalDateTime startTime);
    BasketballMatch update(Long id, BasketballTeam homeTeam, BasketballTeam awayTeam, int homeTeamPoints, int awayTeamPoints, LocalDateTime startTime);
    BasketballMatch updateQuarterPoints(Long id, int quarter, int homeTeamPoints, int awayTeamPoints);
    Map<Integer, int[]> getQuarterPoints(Long id);
    BasketballMatch delete(Long id);
    //void updateTeamStatistics(BasketballMatch match);

    BasketballMatch createAndAddToFixtures(BasketballTeam homeTeam, BasketballTeam awayTeam, int homeTeamPoints, int awayTeamPoints, LocalDateTime startTime);
    List<BasketballMatch> createPlayoffMatches();
    List<BasketballMatch> listPlayoffMatches();
    BasketballMatch updatePlayoffMatchPoints(Long id, BasketballTeam homeTeam, BasketballTeam awayTeam, int homeTeamPoints, int awayTeamPoints, List<BasketballPlayer> homeScorers, List<BasketballPlayer> awayScorers);
    List<BasketballMatch> createSemiFinalMatches();
    List<BasketballMatch> createFinalMatch();

    void updateLiveStats(Long basketballMatchId, int pointsScored, Long playerId);
    Map<LocalDate, List<BasketballMatch>> groupMatchesByDate(List<BasketballMatch> matches);
    void finishMatch(Long matchId);
    public void processMatchStats(Long matchId);

    }
