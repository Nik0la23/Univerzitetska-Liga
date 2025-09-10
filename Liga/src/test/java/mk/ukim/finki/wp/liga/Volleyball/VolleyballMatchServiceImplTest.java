package mk.ukim.finki.wp.liga.Volleyball;


import mk.ukim.finki.wp.liga.model.Exceptions.InvalidVolleyballMatchException;
import mk.ukim.finki.wp.liga.model.VolleyballMatch;
import mk.ukim.finki.wp.liga.model.VolleyballPlayer;
import mk.ukim.finki.wp.liga.model.VolleyballTeam;
import mk.ukim.finki.wp.liga.repository.volleyball.VolleyballMatchRepository;
import mk.ukim.finki.wp.liga.repository.volleyball.VolleyballPlayerRepository;
import mk.ukim.finki.wp.liga.repository.volleyball.VolleyballTeamRepository;
import mk.ukim.finki.wp.liga.service.fantasy.FantasyService;
import mk.ukim.finki.wp.liga.service.volleyball.VolleyballPlayerService;
import mk.ukim.finki.wp.liga.service.volleyball.VolleyballTeamService;
import mk.ukim.finki.wp.liga.service.volleyball.impl.VolleyballMatchServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VolleyballMatchServiceImplTest {

    @Mock private VolleyballMatchRepository matchRepository;
    @Mock private VolleyballTeamRepository teamRepository;
    @Mock private VolleyballTeamService teamService;
    @Mock private FantasyService fantasyService;
    @Mock private VolleyballPlayerRepository playerRepository;
    @Mock private VolleyballPlayerService playerService;

    private VolleyballMatchServiceImpl volleyballMatchService;

    private VolleyballTeam homeTeam;
    private VolleyballMatch match;

    @BeforeEach
    void setUp() {
        volleyballMatchService = new VolleyballMatchServiceImpl(
                matchRepository, teamRepository, playerRepository,
                teamService, playerService, fantasyService
        );

        homeTeam = new VolleyballTeam();
        homeTeam.setVolleyball_team_id(1L);
        homeTeam.setPlayers(new ArrayList<>());

        VolleyballTeam awayTeam = new VolleyballTeam();
        awayTeam.setVolleyball_team_id(2L);
        match = new VolleyballMatch(homeTeam, awayTeam, 0, 0, LocalDateTime.now(), false);
        match.setVolleyball_match_id(1L);
    }

    @Test
    void testFindById_WhenMatchExists() {
        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
        VolleyballMatch result = volleyballMatchService.findById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getVolleyball_match_id());
    }

    @Test
    void testFindById_WhenMatchDoesNotExist_ShouldThrowException() {
        when(matchRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(InvalidVolleyballMatchException.class, () -> volleyballMatchService.findById(99L));
    }

    @Test
    void testProcessMatchStats_HomeTeamWins() {
        match.setHomeTeamPoints(3);
        match.setAwayTeamPoints(1);
        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));

        volleyballMatchService.processMatchStats(1L);

        verify(teamService).incrementMatchesPlayed(1L);
        verify(teamService).incrementMatchesPlayed(2L);
        verify(teamService).addWin(1L);
        verify(teamService).addLoss(2L);
        verify(teamService).addPoints(1L, 3);
        verify(fantasyService).calculateAndAwardVolleyballFantasyPoints(1L);
    }

//    @Test
//    void testUpdateLiveStats_WinFirstSet() {
//        // Arrange: Explicitly set all other set scores to 0
//        match.setHomeTeamSet1Points(24);
//        match.setAwayTeamSet1Points(23);
//        match.setHomeTeamSet2Points(0);
//        match.setAwayTeamSet2Points(0);
//        // ...and so on for other sets if they are not default 0
//
//        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
//
//        VolleyballPlayer scoringPlayer = new VolleyballPlayer();
//        homeTeam.getPlayers().add(scoringPlayer);
//        when(playerRepository.getReferenceById(anyLong())).thenReturn(scoringPlayer);
//        when(teamRepository.findAll()).thenReturn(List.of(homeTeam));
//
//        // Act
//        volleyballMatchService.updateLiveStats(1L, 1, 1L, 1);
//
//        // Assert
//        ArgumentCaptor<VolleyballMatch> matchCaptor = ArgumentCaptor.forClass(VolleyballMatch.class);
//        verify(matchRepository).save(matchCaptor.capture());
//        VolleyballMatch savedMatch = matchCaptor.getValue();
//
//        assertEquals(25, savedMatch.getHomeTeamSet1Points());
//        assertEquals(1, savedMatch.getHomeTeamPoints()); // Home team has 1 set
//        assertEquals(0, savedMatch.getAwayTeamPoints()); // Away team has 0 sets
//        assertNull(savedMatch.getEndTime()); // Match is not over
//    }

    @Test
    void testUpdateLiveStats_WinFinalSetAndMatch() {
        // Arrange: Provide the full history. Home team has already won set 1 and 2.
        // Set 1: Home wins 25-20
        match.setHomeTeamSet1Points(25);
        match.setAwayTeamSet1Points(20);
        // Set 2: Home wins 25-18
        match.setHomeTeamSet2Points(25);
        match.setAwayTeamSet2Points(18);
        // Set 3: Away wins 23-25
        match.setHomeTeamSet3Points(23);
        match.setAwayTeamSet3Points(25);
        // Current Set 4 score: 24-20
        match.setHomeTeamSet4Points(24);
        match.setAwayTeamSet4Points(20);

        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));

        VolleyballPlayer scoringPlayer = new VolleyballPlayer();
        homeTeam.getPlayers().add(scoringPlayer);
        when(playerRepository.getReferenceById(anyLong())).thenReturn(scoringPlayer);
        when(teamRepository.findAll()).thenReturn(List.of(homeTeam));

        volleyballMatchService.updateLiveStats(1L, 1, 1L, 4);

        // Assert
        ArgumentCaptor<VolleyballMatch> matchCaptor = ArgumentCaptor.forClass(VolleyballMatch.class);
        verify(matchRepository).save(matchCaptor.capture());
        VolleyballMatch savedMatch = matchCaptor.getValue();

        assertEquals(3, savedMatch.getHomeTeamPoints());
        // Match should now be finished
        assertNotNull(savedMatch.getEndTime());
    }
}