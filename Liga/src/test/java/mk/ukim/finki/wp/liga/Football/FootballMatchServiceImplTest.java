package mk.ukim.finki.wp.liga.Football;

import mk.ukim.finki.wp.liga.model.Exceptions.InvalidFootballMatchException;
import mk.ukim.finki.wp.liga.model.Exceptions.InvalidFootballTeamException;
import mk.ukim.finki.wp.liga.model.FootballMatch;
import mk.ukim.finki.wp.liga.model.FootballTeam;
import mk.ukim.finki.wp.liga.repository.football.FootballMatchRepository;
import mk.ukim.finki.wp.liga.repository.football.FootballTeamRepository;
import mk.ukim.finki.wp.liga.service.fantasy.FantasyService;
import mk.ukim.finki.wp.liga.service.football.FootballTeamService;
import mk.ukim.finki.wp.liga.service.football.impl.FootballMatchServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FootballMatchServiceImplTest {

    // --- Mocks for all dependencies ---
    @Mock private FootballMatchRepository matchRepository;
    @Mock private FootballTeamRepository teamRepository;
    @Mock private FootballTeamService teamService;
    @Mock private FantasyService fantasyService;

    // --- The service under test ---
    @InjectMocks
    private FootballMatchServiceImpl footballMatchService;

    // --- Common Test Data ---
    private FootballTeam homeTeam;
    private FootballTeam awayTeam;
    private FootballMatch match;

    @BeforeEach
    void setUp() {
        homeTeam = new FootballTeam();
        homeTeam.setId(1L);
        homeTeam.setTeamName("Real Madrid");
        homeTeam.setFootballFixtures(new ArrayList<>());
        homeTeam.setFootballResults(new ArrayList<>());

        awayTeam = new FootballTeam();
        awayTeam.setId(2L);
        awayTeam.setTeamName("Barcelona");
        awayTeam.setFootballFixtures(new ArrayList<>());
        awayTeam.setFootballResults(new ArrayList<>());

        match = new FootballMatch(homeTeam, awayTeam, 2, 1, LocalDateTime.now(), false);
        match.setFootball_match_id(1L);
    }

    @Test
    void testFindById_WhenMatchExists() {
        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
        FootballMatch result = footballMatchService.findById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getFootball_match_id());
    }

    @Test
    void testFindById_WhenMatchDoesNotExist_ShouldThrowException() {
        when(matchRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(InvalidFootballMatchException.class, () -> footballMatchService.findById(99L));
    }

    @Test
    void testCreate_Success() {
        when(teamRepository.findById(1L)).thenReturn(Optional.of(homeTeam));
        when(teamRepository.findById(2L)).thenReturn(Optional.of(awayTeam));
        when(matchRepository.save(any(FootballMatch.class))).thenReturn(match);

        FootballMatch result = footballMatchService.create(homeTeam, awayTeam, 2, 1, LocalDateTime.now());

        assertNotNull(result);
        verify(matchRepository).save(any(FootballMatch.class));
    }

    @Test
    void testCreate_WhenTeamsAreSame_ShouldThrowException() {
        assertThrows(InvalidFootballMatchException.class, () -> footballMatchService.create(homeTeam, homeTeam, 2, 1, LocalDateTime.now()));
    }

    @Test
    void testCreate_WhenTeamNotFound_ShouldThrowException() {
        when(teamRepository.findById(1L)).thenReturn(Optional.of(homeTeam));
        when(teamRepository.findById(99L)).thenReturn(Optional.empty()); // Away team doesn't exist

        assertThrows(InvalidFootballTeamException.class, () -> {
            FootballTeam invalidAwayTeam = new FootballTeam();
            invalidAwayTeam.setId(99L);
            footballMatchService.create(homeTeam, invalidAwayTeam, 2, 1, LocalDateTime.now());
        });
    }

    @Test
    void testDelete_Success() {
        match.setEndTime(LocalDateTime.now().minusDays(1)); // Finished match
        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));

        footballMatchService.delete(1L);

        verify(matchRepository).delete(match);
    }

    @Test
    void testCreatePlayoffMatches_WhenEnoughTeams() {
        // Arrange
        List<FootballTeam> teams = LongStream.range(1, 9)
                .mapToObj(id -> new FootballTeam() {{ setId(id); }})
                .collect(Collectors.toList());

        when(matchRepository.findAllByIsPlayoffMatchTrue()).thenReturn(Collections.emptyList());
        when(teamRepository.findAllByOrderByTeamLeaguePointsDesc()).thenReturn(teams);
        when(matchRepository.save(any(FootballMatch.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        List<FootballMatch> result = footballMatchService.createPlayoffMatches();

        // Assert
        assertEquals(4, result.size());
        assertEquals(1L, result.get(0).getHomeTeam().getId()); // 1 vs 8
        assertEquals(8L, result.get(0).getAwayTeam().getId());
        verify(matchRepository, times(4)).save(any(FootballMatch.class));
    }

    @Test
    void testCreatePlayoffMatches_WhenNotEnoughTeams_ShouldThrowException() {
        when(teamRepository.findAllByOrderByTeamLeaguePointsDesc()).thenReturn(Collections.emptyList());
        assertThrows(RuntimeException.class, () -> footballMatchService.createPlayoffMatches());
    }

    @Test
    void testCreateSemiFinalMatches_WhenQuarterFinalsAreNotCompleted_ShouldThrowException() {
        // Arrange: one match is a draw (not completed)
        FootballMatch drawMatch = new FootballMatch(homeTeam, awayTeam, 2, 2, LocalDateTime.now(), true);
        when(matchRepository.findAllByIsPlayoffMatchTrue()).thenReturn(List.of(match, match, match, drawMatch));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> footballMatchService.createSemiFinalMatches());
    }

    @Test
    void testProcessMatchStats_HomeTeamWins() {
        // Arrange
        match.setHomeTeamPoints(3);
        match.setAwayTeamPoints(1);
        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));

        // Act
        footballMatchService.processMatchStats(1L);

        // Assert: Verify correct services were called for a home win
        verify(teamService).incrementMatchesPlayed(1L);
        verify(teamService).incrementMatchesPlayed(2L);
        verify(teamService).addWin(1L);
        verify(teamService).addLoss(2L);

        // FIX: Wrapped raw values in eq() matchers
        verify(teamService).addPoints(eq(1L), eq(3));
        verify(teamService, never()).addPoints(eq(2L), anyInt());

        verify(fantasyService).calculateAndAwardFantasyPoints(1L);
    }

    @Test
    void testProcessMatchStats_Draw() {
        // Arrange
        match.setHomeTeamPoints(2);
        match.setAwayTeamPoints(2);
        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));

        // Act
        footballMatchService.processMatchStats(1L);

        // Assert: Verify correct services were called for a draw
        verify(teamService).addDraw(1L);
        verify(teamService).addDraw(2L);
        verify(teamService).addPoints(1L, 1);
        verify(teamService).addPoints(2L, 1);
        verify(teamService, never()).addWin(anyLong());
        verify(teamService, never()).addLoss(anyLong());
    }

    @Test
    void testFinishMatch() {
        // Arrange
        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
        // Mock the dependent service calls to do nothing, as we are testing them separately
        doNothing().when(teamService).incrementMatchesPlayed(anyLong());
        doNothing().when(fantasyService).calculateAndAwardFantasyPoints(anyLong());

        // Act
        footballMatchService.finishMatch(1L);

        // Assert
        assertNotNull(match.getEndTime());
        verify(matchRepository).save(match);
        // Verify that processMatchStats was called internally by checking one of its calls
        verify(teamService, times(2)).incrementMatchesPlayed(anyLong());
    }
}