package mk.ukim.finki.wp.liga.Volleyball;

import mk.ukim.finki.wp.liga.model.Exceptions.InvalidVolleyballTeamException;
import mk.ukim.finki.wp.liga.model.VolleyballMatch;
import mk.ukim.finki.wp.liga.model.VolleyballTeam;
import mk.ukim.finki.wp.liga.model.dtos.VolleyBallStandings;
import mk.ukim.finki.wp.liga.repository.volleyball.VolleyballTeamRepository;
import mk.ukim.finki.wp.liga.service.volleyball.impl.VolleyballTeamServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VolleyballTeamServiceImplTest {

    // --- Mocks ---
    @Mock
    private VolleyballTeamRepository volleyballTeamRepository;

    // --- Service Under Test ---
    @InjectMocks
    private VolleyballTeamServiceImpl volleyballTeamService;

    // --- Common Test Data ---
    private VolleyballTeam team;

    @BeforeEach
    void setUp() {
        team = new VolleyballTeam("Test Team", new byte[]{});
        team.setVolleyball_team_id(1L);
    }

    @Test
    void testFindById_WhenTeamExists() {
        when(volleyballTeamRepository.findById(1L)).thenReturn(Optional.of(team));
        VolleyballTeam result = volleyballTeamService.findById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getVolleyball_team_id());
    }

    @Test
    void testFindById_WhenTeamDoesNotExist_ShouldThrowException() {
        when(volleyballTeamRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(InvalidVolleyballTeamException.class, () -> volleyballTeamService.findById(99L));
    }

    @Test
    void testCreate() {
        volleyballTeamService.create("New Team", new byte[]{});
        verify(volleyballTeamRepository, times(1)).save(any(VolleyballTeam.class));
    }

    @Test
    void testDelete() {
        when(volleyballTeamRepository.findById(1L)).thenReturn(Optional.of(team));
        volleyballTeamService.delete(1L);
        verify(volleyballTeamRepository, times(1)).delete(team);
    }

    @Test
    void testUpdateStats() {
        // Arrange: Create match results: 1 win, 1 loss, 1 future (unplayed) match
        VolleyballTeam opponent = new VolleyballTeam() {{ setVolleyball_team_id(2L); }};
        List<VolleyballMatch> results = new ArrayList<>();
        // A win (3-1 sets)
        results.add(new VolleyballMatch(team, opponent, 3, 1, LocalDateTime.now().minusDays(10), false) {{ setEndTime(LocalDateTime.now()); }});
        // A loss (2-3 sets)
        results.add(new VolleyballMatch(opponent, team, 3, 2, LocalDateTime.now().minusDays(5), false) {{ setEndTime(LocalDateTime.now()); }});
        // A future match that should be ignored by the stats calculation
        results.add(new VolleyballMatch(team, opponent, 0, 0, LocalDateTime.now().plusDays(2), false) {{ setEndTime(LocalDateTime.now().plusDays(2)); }});

        team.setVolleyballResults(results);
        when(volleyballTeamRepository.findById(1L)).thenReturn(Optional.of(team));

        // Act
        volleyballTeamService.updateStats(1L);

        // Assert: Capture the team object passed to save() and verify the calculated stats
        ArgumentCaptor<VolleyballTeam> teamCaptor = ArgumentCaptor.forClass(VolleyballTeam.class);
        verify(volleyballTeamRepository).save(teamCaptor.capture());

        VolleyballTeam updatedTeam = teamCaptor.getValue();
        assertEquals(2, updatedTeam.getTeamMatchesPlayed()); // Ignores the future match
        assertEquals(1, updatedTeam.getTeamWins());
        assertEquals(1, updatedTeam.getTeamLoses());
        assertEquals(2, updatedTeam.getTeamLeaguePoints()); // 2 points for a win
    }

    @Test
    void testGetStandings() {
        // Arrange: Create teams with different win counts to test sorting
        VolleyballTeam team1 = new VolleyballTeam() {{ setTeamWins(10); }};
        VolleyballTeam team2 = new VolleyballTeam() {{ setTeamWins(15); }}; // Should be first
        VolleyballTeam team3 = new VolleyballTeam() {{ setTeamWins(5); }};

        List<VolleyballTeam> unsortedTeams = Arrays.asList(team1, team2, team3);
        when(volleyballTeamRepository.findAll()).thenReturn(unsortedTeams);

        // Act
        List<VolleyBallStandings> standings = volleyballTeamService.getStandings();

        // Assert
        assertEquals(3, standings.size());
        // Verify the list is sorted by wins in descending order
        assertEquals(15, standings.get(0).getWins());
        assertEquals(10, standings.get(1).getWins());
        assertEquals(5, standings.get(2).getWins());
    }

    @Test
    void testStatIncrementMethods() {
        when(volleyballTeamRepository.findById(1L)).thenReturn(Optional.of(team));

        // Test addWin
        team.setTeamWins(5);
        volleyballTeamService.addWin(1L);
        assertEquals(6, team.getTeamWins());

        // Test addLoss
        team.setTeamLoses(3);
        volleyballTeamService.addLoss(1L);
        assertEquals(4, team.getTeamLoses());

        // Test addPoints
        team.setTeamLeaguePoints(10);
        volleyballTeamService.addPoints(1L, 2);
        assertEquals(12, team.getTeamLeaguePoints());

        // Verify that save was called for each update
        verify(volleyballTeamRepository, times(3)).save(team);
    }
}