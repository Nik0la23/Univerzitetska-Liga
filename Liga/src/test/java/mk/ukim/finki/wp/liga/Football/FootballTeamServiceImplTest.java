package mk.ukim.finki.wp.liga.Football;

import mk.ukim.finki.wp.liga.model.Exceptions.InvalidFootballTeamException;
import mk.ukim.finki.wp.liga.model.FootballMatch;
import mk.ukim.finki.wp.liga.model.FootballTeam;
import mk.ukim.finki.wp.liga.repository.football.FootballTeamRepository;
import mk.ukim.finki.wp.liga.service.football.impl.FootballTeamServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FootballTeamServiceImplTest {

    @Mock private FootballTeamRepository footballTeamRepository;

    @InjectMocks
    private FootballTeamServiceImpl footballTeamService;

    private FootballTeam team;

    @BeforeEach
    void setUp() {
        team = new FootballTeam("Test Team", new byte[]{});
        team.setId(1L);
    }

    @Test
    void testFindById_WhenTeamExists() {
        when(footballTeamRepository.findById(1L)).thenReturn(Optional.of(team));
        FootballTeam result = footballTeamService.findById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testFindById_WhenTeamDoesNotExist_ShouldThrowException() {
        when(footballTeamRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(InvalidFootballTeamException.class, () -> footballTeamService.findById(99L));
    }

    @Test
    void testCreate() {
        footballTeamService.create("New Team", new byte[]{});
        verify(footballTeamRepository, times(1)).save(any(FootballTeam.class));
    }

    @Test
    void testUpdateStats() {
        // Arrange: Create a list of completed matches for the team
        FootballTeam opponent = new FootballTeam() {{ setId(2L); }};
        List<FootballMatch> results = new ArrayList<>();
        // A win (2-1)
        results.add(new FootballMatch(team, opponent, 2, 1, LocalDateTime.now().minusDays(10), false) {{ setEndTime(LocalDateTime.now()); }});
        // A draw (1-1)
        results.add(new FootballMatch(opponent, team, 1, 1, LocalDateTime.now().minusDays(5), false) {{ setEndTime(LocalDateTime.now()); }});
        // A loss (0-3)
        results.add(new FootballMatch(team, opponent, 0, 3, LocalDateTime.now().minusDays(2), false) {{ setEndTime(LocalDateTime.now()); }});

        team.setFootballResults(results);
        when(footballTeamRepository.findById(1L)).thenReturn(Optional.of(team));

        // Act
        footballTeamService.updateStats(1L);

        // Assert: Capture the team object passed to save() and verify the calculated stats
        ArgumentCaptor<FootballTeam> teamCaptor = ArgumentCaptor.forClass(FootballTeam.class);
        verify(footballTeamRepository).save(teamCaptor.capture());

        FootballTeam updatedTeam = teamCaptor.getValue();
        assertEquals(3, updatedTeam.getTeamMatchesPlayed());
        assertEquals(1, updatedTeam.getTeamWins());
        assertEquals(1, updatedTeam.getTeamDraws());
        assertEquals(1, updatedTeam.getTeamLoses());
        assertEquals(4, updatedTeam.getTeamLeaguePoints()); // 3 for a win + 1 for a draw
    }

    @Test
    void testAddWin() {
        when(footballTeamRepository.findById(1L)).thenReturn(Optional.of(team));
        team.setTeamWins(5);

        footballTeamService.addWin(1L);

        assertEquals(6, team.getTeamWins());
        verify(footballTeamRepository).save(team);
    }
}