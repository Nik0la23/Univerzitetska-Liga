package mk.ukim.finki.wp.liga.service.football;

import mk.ukim.finki.wp.liga.model.FootballMatch;
import mk.ukim.finki.wp.liga.model.FootballPlayer;
import mk.ukim.finki.wp.liga.model.FootballPlayerScored;
import mk.ukim.finki.wp.liga.model.FootballTeam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface FootballMatchService {
    List<FootballMatch> listAllFootballMatches();


    FootballMatch findById(Long id);


    FootballMatch create(FootballTeam homeTeam, FootballTeam awayTeam, int homeTeamPoints, int awayTeamPoints, LocalDateTime startTime);


    FootballMatch update(Long id, FootballTeam homeTeam, FootballTeam awayTeam, int homeTeamPoints, int awayTeamPoints, LocalDateTime startTime);
    
    FootballMatch update(Long id, FootballTeam homeTeam, FootballTeam awayTeam, int homeTeamPoints, int awayTeamPoints, 
                        int homeTeamH1Points, int homeTeamH2Points, int awayTeamH1Points, int awayTeamH2Points, LocalDateTime startTime);


    FootballMatch delete(Long id);

    void updateTeamStatistics(FootballMatch match);

    FootballMatch createAndAddToFixtures(FootballTeam homeTeam, FootballTeam awayTeam, int homeTeamPoints, int awayTeamPoints, LocalDateTime startTime);
    List<FootballMatch> createPlayoffMatches();
    List<FootballMatch> listPlayoffMatches();
    FootballMatch updatePlayoffMatchPoints(Long id, FootballTeam homeTeam, FootballTeam awayTeam, int homeTeamPoints, int awayTeamPoints, List<FootballPlayer> homeScore, List<FootballPlayer> awayScore);
    List<FootballMatch> createSemiFinalMatches();
    List<FootballMatch> createFinalMatch();
    //boolean allTeamsHavePlayedFourMatches();
    void updateLiveStats(Long footballMatchId, int goalsScored, Long playerId);
    Map<LocalDate, List<FootballMatch>> groupMatchesByDate(List<FootballMatch> matches);
    void finishMatch(Long matchId);
    void processMatchStats(Long matchId);
}
