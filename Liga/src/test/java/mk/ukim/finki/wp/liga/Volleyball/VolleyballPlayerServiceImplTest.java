package mk.ukim.finki.wp.liga.Volleyball;

import mk.ukim.finki.wp.liga.model.Exceptions.InvalidVolleyballPlayerException;
import mk.ukim.finki.wp.liga.model.VolleyballPlayer;
import mk.ukim.finki.wp.liga.model.VolleyballPlayerMatchStats;
import mk.ukim.finki.wp.liga.model.VolleyballTeam;
import mk.ukim.finki.wp.liga.model.fantasy.FantasyPlayer;
import mk.ukim.finki.wp.liga.repository.fantasy.FantasyPlayerRepository;
import mk.ukim.finki.wp.liga.repository.volleyball.VolleyballPlayerMatchStatsRepository;
import mk.ukim.finki.wp.liga.repository.volleyball.VolleyballPlayerRepository;
import mk.ukim.finki.wp.liga.repository.volleyball.VolleyballTeamRepository;
import mk.ukim.finki.wp.liga.service.volleyball.impl.VolleyballPlayerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VolleyballPlayerServiceImplTest {

    // --- Mocks for all dependencies ---
    @Mock private VolleyballPlayerRepository volleyballPlayerRepository;
    @Mock private VolleyballTeamRepository volleyballTeamRepository;
    @Mock private FantasyPlayerRepository fantasyPlayerRepository;
    @Mock private VolleyballPlayerMatchStatsRepository volleyballPlayerMatchStatsRepository;

    // --- The service under test ---
    @InjectMocks
    private VolleyballPlayerServiceImpl volleyballPlayerService;

    // --- Common Test Data ---
    private VolleyballPlayer player;
    private VolleyballTeam team;
    private byte[] image;

    @BeforeEach
    void setUp() {
        image = new byte[]{1, 2, 3};
        team = new VolleyballTeam();
        team.setVolleyball_team_id(1L);
        player = new VolleyballPlayer(image, "Gabi", "Guimarães", new Date(), 123, "Brazil", "OH", team);
        player.setVolleyball_player_id(1L);
    }

    @Test
    void testFindById_WhenPlayerExists() {
        when(volleyballPlayerRepository.findById(1L)).thenReturn(Optional.of(player));
        VolleyballPlayer result = volleyballPlayerService.findById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getVolleyball_player_id());
    }

    @Test
    void testFindById_WhenPlayerDoesNotExist_ShouldThrowException() {
        when(volleyballPlayerRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(InvalidVolleyballPlayerException.class, () -> volleyballPlayerService.findById(99L));
    }

    @Test
    void testCreate_WithTeam() {
        when(volleyballTeamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(volleyballPlayerRepository.save(any(VolleyballPlayer.class))).thenReturn(player);

        VolleyballPlayer result = volleyballPlayerService.create(image, "Gabi", "Guimarães", new Date(), 123, "Brazil", "OH", team);

        assertNotNull(result);
        assertEquals(1L, result.getTeam().getVolleyball_team_id());
        verify(volleyballPlayerRepository).save(any(VolleyballPlayer.class));
    }

    @Test
    void testDelete_WhenPlayerHasDependencies() {
        // Arrange
        FantasyPlayer fantasyPlayer = new FantasyPlayer();
        fantasyPlayer.setVolleyballPlayer(player);
        VolleyballPlayerMatchStats matchStats = new VolleyballPlayerMatchStats();
        matchStats.setPlayer(player);

        when(volleyballPlayerRepository.findById(1L)).thenReturn(Optional.of(player));
        when(fantasyPlayerRepository.findAll()).thenReturn(Collections.singletonList(fantasyPlayer));
        when(volleyballPlayerMatchStatsRepository.findByPlayer(player)).thenReturn(Collections.singletonList(matchStats));

        // Act
        volleyballPlayerService.delete(1L);

        // Assert: Verify dependencies are deleted before the player
        verify(fantasyPlayerRepository).deleteAll(anyList());
        verify(volleyballPlayerMatchStatsRepository).deleteAll(anyList());
        verify(volleyballPlayerRepository).delete(player);
    }

    @Test
    void testAddStatMethods() {
        when(volleyballPlayerRepository.findById(1L)).thenReturn(Optional.of(player));
        when(volleyballPlayerRepository.save(player)).thenReturn(player);

        // Test Points
        player.setScoredPoints(100);
        volleyballPlayerService.addPoints(1L, 10);
        assertEquals(110, player.getScoredPoints());

        // Test Blocks
        player.setBlocks(20);
        volleyballPlayerService.addBlocks(1L, 2);
        assertEquals(22, player.getBlocks());

        // Test Assists
        player.setAssists(50);
        volleyballPlayerService.addAssists(1L, 5);
        assertEquals(55, player.getAssists());

        // --- FIX: Update the test for the now-correct addServings method ---
        player.setServings(30);
        player.setAssists(5); // Reset assists to its initial value for this part of the test
        volleyballPlayerService.addServings(1L, 5);

        // Assert the new, CORRECT behavior
        assertEquals(35, player.getServings()); // Servings should now be 35
        assertEquals(5, player.getAssists());   // Assists should be unchanged

        // Verify save was called for each stat update (4 total now)
        verify(volleyballPlayerRepository, times(4)).save(player);
    }

    @Test
    void testGetTop5Players() {
        // Arrange: Mock players and stub getTotalPoints() to isolate the service's sorting logic
        VolleyballPlayer p1 = mock(VolleyballPlayer.class);
        VolleyballPlayer p2 = mock(VolleyballPlayer.class);
        VolleyballPlayer p3 = mock(VolleyballPlayer.class);

        when(p1.getTotalPoints()).thenReturn(100);
        when(p2.getTotalPoints()).thenReturn(500); // Top player
        when(p3.getTotalPoints()).thenReturn(300);

        List<VolleyballPlayer> allPlayers = Arrays.asList(p1, p2, p3);
        when(volleyballPlayerRepository.findAll()).thenReturn(allPlayers);

        // Act
        List<VolleyballPlayer> top5 = volleyballPlayerService.getTop5Players();

        // Assert
        assertEquals(3, top5.size());
        // Verify the sorting is correct based on our stubbed points
        assertEquals(500, top5.get(0).getTotalPoints());
        assertEquals(300, top5.get(1).getTotalPoints());
        assertEquals(100, top5.get(2).getTotalPoints());
    }
}
