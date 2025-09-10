package mk.ukim.finki.wp.liga.Basketball;

import mk.ukim.finki.wp.liga.model.BasketballMatch;
import mk.ukim.finki.wp.liga.model.BasketballPlayer;
import mk.ukim.finki.wp.liga.model.BasketballTeam;
import mk.ukim.finki.wp.liga.model.Exceptions.InvalidBasketballMatchException;
import mk.ukim.finki.wp.liga.model.Exceptions.InvalidBasketballTeamException;
import mk.ukim.finki.wp.liga.repository.basketball.BasketballMatchRepository;
import mk.ukim.finki.wp.liga.repository.basketball.BasketballPlayerRepository;
import mk.ukim.finki.wp.liga.repository.basketball.BasketballTeamRepository;
import mk.ukim.finki.wp.liga.service.basketball.BasketballTeamService;
import mk.ukim.finki.wp.liga.service.basketball.impl.BasketballMatchServiceImpl;
import mk.ukim.finki.wp.liga.service.fantasy.FantasyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BasketballMatchTests {

    // --- Mocks for all dependencies ---
    @Mock
    private BasketballMatchRepository basketballMatchRepository;
    @Mock
    private BasketballTeamRepository basketballTeamRepository;
    @Mock
    private BasketballPlayerRepository basketballPlayerRepository;
    @Mock
    private BasketballTeamService basketballTeamService;
    @Mock
    private FantasyService fantasyService;

    // --- The service under test, with mocks injected ---
    @InjectMocks
    private BasketballMatchServiceImpl basketballMatchService;

    // --- Common test data ---
    private BasketballTeam homeTeam;
    private BasketballTeam awayTeam;
    private BasketballMatch match;
    private BasketballPlayer player;

    @BeforeEach
    void setUp() {
        homeTeam = new BasketballTeam();
        homeTeam.setId(1L);
        homeTeam.setTeamName("Home Team");
        homeTeam.setBasketballFixtures(new ArrayList<>());
        homeTeam.setBasketballResults(new ArrayList<>());

        awayTeam = new BasketballTeam();
        awayTeam.setId(2L);
        awayTeam.setTeamName("Away Team");
        awayTeam.setBasketballFixtures(new ArrayList<>());
        awayTeam.setBasketballResults(new ArrayList<>());

        match = new BasketballMatch(homeTeam, awayTeam, 100, 90, LocalDateTime.now(), false);
        match.setBasketball_match_id(1L);

        player = new BasketballPlayer();
        player.setBasketball_player_id(1L);
    }

    // --- Test Methods ---

    @Test
    void testFindByIdWithTeamsAndPlayers_WhenMatchFound() {
        when(basketballMatchRepository.findById(1L)).thenReturn(Optional.of(match));
        BasketballMatch result = basketballMatchService.findByIdWithTeamsAndPlayers(1L);
        assertNotNull(result);
        assertEquals(1L, result.getBasketball_match_id());
    }

    @Test
    void testFindByIdWithTeamsAndPlayers_WhenMatchNotFound_ShouldThrowException() {
        when(basketballMatchRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> basketballMatchService.findByIdWithTeamsAndPlayers(99L));
    }

    @Test
    void testListAllBasketballMatches() {
        when(basketballMatchRepository.findAll()).thenReturn(Collections.singletonList(match));
        List<BasketballMatch> results = basketballMatchService.listAllBasketballMatches();
        assertEquals(1, results.size());
        assertEquals(match, results.get(0));
    }

    @Test
    void testFindById_WhenMatchFound() {
        when(basketballMatchRepository.findById(1L)).thenReturn(Optional.of(match));
        BasketballMatch result = basketballMatchService.findById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getBasketball_match_id());
    }

    @Test
    void testFindById_WhenMatchNotFound_ShouldThrowException() {
        when(basketballMatchRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(InvalidBasketballMatchException.class, () -> basketballMatchService.findById(99L));
    }

    @Test
    void testCreate_WhenTeamsExist_ShouldSaveAndReturnMatch() {
        when(basketballTeamRepository.findById(1L)).thenReturn(Optional.of(homeTeam));
        when(basketballTeamRepository.findById(2L)).thenReturn(Optional.of(awayTeam));
        when(basketballMatchRepository.save(any(BasketballMatch.class))).thenReturn(match);

        BasketballMatch result = basketballMatchService.create(homeTeam, awayTeam, 100, 90, LocalDateTime.now());

        assertNotNull(result);
        assertEquals("Home Team", result.getHomeTeam().getTeamName());
        verify(basketballMatchRepository, times(1)).save(any(BasketballMatch.class));
    }

    @Test
    void testCreate_WhenTeamDoesNotExist_ShouldThrowException() {
        when(basketballTeamRepository.findById(1L)).thenReturn(Optional.of(homeTeam));
        when(basketballTeamRepository.findById(99L)).thenReturn(Optional.empty()); // Away team is invalid

        BasketballTeam invalidAwayTeam = new BasketballTeam();
        invalidAwayTeam.setId(99L);

        assertThrows(InvalidBasketballTeamException.class, () -> basketballMatchService.create(homeTeam, invalidAwayTeam, 100, 90, LocalDateTime.now()));
    }

    @Test
    void testUpdate_WhenMatchAndTeamsExist() {
        when(basketballMatchRepository.findById(1L)).thenReturn(Optional.of(match));
        when(basketballTeamRepository.findById(1L)).thenReturn(Optional.of(homeTeam));
        when(basketballTeamRepository.findById(2L)).thenReturn(Optional.of(awayTeam));
        when(basketballMatchRepository.save(any(BasketballMatch.class))).thenReturn(match);

        LocalDateTime newTime = LocalDateTime.now().plusDays(1);
        BasketballMatch result = basketballMatchService.update(1L, homeTeam, awayTeam, 110, 95, newTime);

        assertEquals(110, result.getHomeTeamPoints());
        assertEquals(95, result.getAwayTeamPoints());
        assertEquals(newTime, result.getStartTime());
        verify(basketballMatchRepository, times(1)).save(match);
    }

    @Test
    void testUpdateQuarterPoints() {
        when(basketballMatchRepository.findById(1L)).thenReturn(Optional.of(match));
        when(basketballMatchRepository.save(any(BasketballMatch.class))).thenReturn(match);

        // Update Q1
        basketballMatchService.updateQuarterPoints(1L, 1, 25, 20);
        assertEquals(25, match.getHomeTeamQ1Points());
        assertEquals(20, match.getAwayTeamQ1Points());

        // Update Q2 and check total points calculation
        basketballMatchService.updateQuarterPoints(1L, 2, 30, 25);
        assertEquals(30, match.getHomeTeamQ2Points());
        assertEquals(55, match.getHomeTeamPoints()); // 25 (Q1) + 30 (Q2)
        assertEquals(45, match.getAwayTeamPoints()); // 20 (Q1) + 25 (Q2)

        verify(basketballMatchRepository, times(2)).save(match);
    }

    @Test
    void testUpdateQuarterPoints_WithInvalidQuarter_ShouldThrowException() {
        when(basketballMatchRepository.findById(1L)).thenReturn(Optional.of(match));
        assertThrows(IllegalArgumentException.class, () -> basketballMatchService.updateQuarterPoints(1L, 5, 10, 10));
    }

    @Test
    void testGetQuarterPoints() {
        match.setHomeTeamQ1Points(25); match.setAwayTeamQ1Points(20);
        match.setHomeTeamQ2Points(30); match.setAwayTeamQ2Points(25);
        when(basketballMatchRepository.findById(1L)).thenReturn(Optional.of(match));

        Map<Integer, int[]> quarterPoints = basketballMatchService.getQuarterPoints(1L);

        assertArrayEquals(new int[]{25, 20}, quarterPoints.get(1));
        assertArrayEquals(new int[]{30, 25}, quarterPoints.get(2));
    }

    @Test
    void testDelete_WhenMatchExists() {
        match.setEndTime(LocalDateTime.now().minusDays(1)); // A past match
        when(basketballMatchRepository.findById(1L)).thenReturn(Optional.of(match));

        basketballMatchService.delete(1L);

        verify(basketballMatchRepository, times(1)).delete(match);
    }

    @Test
    void testCreatePlayoffMatches_WhenEnoughTeams() {
        List<BasketballTeam> teams = LongStream.range(1, 9)
                .mapToObj(id -> {
                    BasketballTeam t = new BasketballTeam();
                    t.setId(id);
                    t.setTeamName("Team " + id);
                    return t;
                })
                .collect(Collectors.toList());

        when(basketballMatchRepository.findAllByIsPlayoffMatchTrue()).thenReturn(Collections.emptyList());
        when(basketballTeamRepository.findAllByOrderByTeamLeaguePointsDesc()).thenReturn(teams);
        when(basketballMatchRepository.save(any(BasketballMatch.class))).thenAnswer(i -> i.getArgument(0));

        List<BasketballMatch> playoffMatches = basketballMatchService.createPlayoffMatches();

        assertEquals(4, playoffMatches.size());
        assertEquals(1L, playoffMatches.get(0).getHomeTeam().getId());
        assertEquals(8L, playoffMatches.get(0).getAwayTeam().getId());
        verify(basketballMatchRepository, times(4)).save(any(BasketballMatch.class));
    }

    @Test
    void testCreatePlayoffMatches_WhenNotEnoughTeams_ShouldThrowException() {
        when(basketballMatchRepository.findAllByIsPlayoffMatchTrue()).thenReturn(Collections.emptyList());
        when(basketballTeamRepository.findAllByOrderByTeamLeaguePointsDesc()).thenReturn(Collections.emptyList());

        assertThrows(RuntimeException.class, () -> basketballMatchService.createPlayoffMatches());
    }

    @Test
    void testCreateSemiFinalMatches_WhenQuarterFinalsAreComplete() {
        BasketballTeam team1 = new BasketballTeam(); team1.setId(1L);
        BasketballTeam team2 = new BasketballTeam(); team2.setId(2L);
        BasketballTeam team3 = new BasketballTeam(); team3.setId(3L);
        BasketballTeam team4 = new BasketballTeam(); team4.setId(4L);
        BasketballTeam team5 = new BasketballTeam(); team5.setId(5L);
        BasketballTeam team6 = new BasketballTeam(); team6.setId(6L);
        BasketballTeam team7 = new BasketballTeam(); team7.setId(7L);
        BasketballTeam team8 = new BasketballTeam(); team8.setId(8L);

        List<BasketballMatch> quarterFinals = Arrays.asList(
                new BasketballMatch(team1, team8, 100, 90, LocalDateTime.now(), true), // Winner: team1
                new BasketballMatch(team2, team7, 100, 90, LocalDateTime.now(), true), // Winner: team2
                new BasketballMatch(team3, team6, 90, 100, LocalDateTime.now(), true), // Winner: team6
                new BasketballMatch(team4, team5, 90, 100, LocalDateTime.now(), true)  // Winner: team5
        );

        when(basketballMatchRepository.findAllByIsPlayoffMatchTrue()).thenReturn(quarterFinals);
        when(basketballMatchRepository.save(any(BasketballMatch.class))).thenAnswer(i -> i.getArgument(0));

        List<BasketballMatch> semiFinals = basketballMatchService.createSemiFinalMatches();

        assertEquals(2, semiFinals.size());
        // Match 1: Winner QF1 (team1) vs Winner QF4 (team5)
        assertEquals(1L, semiFinals.get(0).getHomeTeam().getId());
        assertEquals(5L, semiFinals.get(0).getAwayTeam().getId());
        // Match 2: Winner QF2 (team2) vs Winner QF3 (team6)
        assertEquals(2L, semiFinals.get(1).getHomeTeam().getId());
        assertEquals(6L, semiFinals.get(1).getAwayTeam().getId());
    }

    @Test
    void testCreateFinalMatch_WhenSemiFinalsAreComplete() {
        BasketballTeam team1 = new BasketballTeam(); team1.setId(1L);
        BasketballTeam team2 = new BasketballTeam(); team2.setId(2L);
        BasketballTeam team5 = new BasketballTeam(); team5.setId(5L);
        BasketballTeam team6 = new BasketballTeam(); team6.setId(6L);

        List<BasketballMatch> semiFinals = new ArrayList<>();
        // Add 4 dummy QF matches
        for(int i=0; i<4; i++) semiFinals.add(new BasketballMatch());
        // Add 2 real SF matches
        semiFinals.add(new BasketballMatch(team1, team5, 100, 90, LocalDateTime.now(), true)); // Winner: team1
        semiFinals.add(new BasketballMatch(team2, team6, 90, 100, LocalDateTime.now(), true)); // Winner: team6

        when(basketballMatchRepository.findAllByIsPlayoffMatchTrue()).thenReturn(semiFinals);
        when(basketballMatchRepository.save(any(BasketballMatch.class))).thenAnswer(i -> i.getArgument(0));

        List<BasketballMatch> finals = basketballMatchService.createFinalMatch();

        assertEquals(1, finals.size());
        assertEquals(1L, finals.get(0).getHomeTeam().getId());
        assertEquals(6L, finals.get(0).getAwayTeam().getId());
    }

    @Test
    void testUpdateLiveStats() {
        // Arrange
        player.setTeam(homeTeam);
        homeTeam.setPlayers(Collections.singletonList(player));

        when(basketballPlayerRepository.getReferenceById(1L)).thenReturn(player);
        when(basketballMatchRepository.findById(1L)).thenReturn(Optional.of(match));
        // When team repo is searched, make it find the player on the home team
        when(basketballTeamRepository.findAll()).thenReturn(Arrays.asList(homeTeam, awayTeam));

        // Act
        basketballMatchService.updateLiveStats(1L, 2, 1L);

        // Assert
        assertEquals(102, match.getHomeTeamPoints()); // Initial was 100 + 2
        assertEquals(90, match.getAwayTeamPoints());
        verify(basketballMatchRepository, times(1)).save(match);
    }

    @Test
    void testGroupMatchesByDate() {
        BasketballMatch match1 = new BasketballMatch(homeTeam, awayTeam, 100, 90, LocalDateTime.of(2025, 9, 10, 20, 0), false);
        BasketballMatch match2 = new BasketballMatch(homeTeam, awayTeam, 100, 90, LocalDateTime.of(2025, 9, 11, 20, 0), false);
        BasketballMatch match3 = new BasketballMatch(homeTeam, awayTeam, 100, 90, LocalDateTime.of(2025, 9, 10, 22, 0), false);

        List<BasketballMatch> matches = Arrays.asList(match1, match2, match3);

        Map<LocalDate, List<BasketballMatch>> grouped = basketballMatchService.groupMatchesByDate(matches);

        assertEquals(2, grouped.size()); // Two distinct dates
        assertEquals(2, grouped.get(LocalDate.of(2025, 9, 10)).size());
        assertEquals(1, grouped.get(LocalDate.of(2025, 9, 11)).size());
        // Check if map is sorted by date
        assertEquals(LocalDate.of(2025, 9, 10), grouped.keySet().iterator().next());
    }

    @Test
    void testFinishMatch() {
        // Arrange
        when(basketballMatchRepository.findById(1L)).thenReturn(Optional.of(match));
        // Mock the dependent service calls to do nothing, as we are only testing the finishMatch logic
        doNothing().when(basketballTeamService).incrementMatchesPlayed(anyLong());
        doNothing().when(basketballTeamService).addWin(anyLong());
        doNothing().when(basketballTeamService).addLoss(anyLong());
        doNothing().when(basketballTeamService).addPoints(anyLong(), anyInt());
        doNothing().when(fantasyService).calculateAndAwardBasketballFantasyPoints(anyLong());

        // Act
        basketballMatchService.finishMatch(1L);

        // Assert
        assertNotNull(match.getEndTime());
        verify(basketballMatchRepository, times(1)).save(match);
        // Verify that processMatchStats was called internally
        verify(fantasyService, times(1)).calculateAndAwardBasketballFantasyPoints(1L);
    }

    @Test
    void testProcessMatchStats_HomeTeamWins() {
        match.setHomeTeamPoints(100);
        match.setAwayTeamPoints(90);

        when(basketballMatchRepository.findById(1L)).thenReturn(Optional.of(match));

        basketballMatchService.processMatchStats(1L);

        verify(basketballTeamService, times(1)).incrementMatchesPlayed(1L);
        verify(basketballTeamService, times(1)).incrementMatchesPlayed(2L);

        verify(basketballTeamService, times(1)).addWin(1L); // Home team
        verify(basketballTeamService, times(1)).addLoss(2L); // Away team
        verify(basketballTeamService, times(1)).addPoints(1L, 3);

        verify(fantasyService, times(1)).calculateAndAwardBasketballFantasyPoints(1L);
    }
}
