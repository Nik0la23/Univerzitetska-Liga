package mk.ukim.finki.wp.liga.Volleyball;

import mk.ukim.finki.wp.liga.model.Exceptions.InvalidVolleyballPlayerException;
import mk.ukim.finki.wp.liga.model.VolleyballMatch;
import mk.ukim.finki.wp.liga.model.VolleyballPlayer;
import mk.ukim.finki.wp.liga.model.VolleyballPlayerMatchStats;
import mk.ukim.finki.wp.liga.repository.volleyball.VolleyballPlayerMatchStatsRepository;
import mk.ukim.finki.wp.liga.service.volleyball.impl.VolleyballPlayerMatchStatsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VolleyballPlayerMatchStatsServiceImplTest {

    // --- Mock the repository dependency ---
    @Mock
    private VolleyballPlayerMatchStatsRepository volleyballPlayerMatchStatsRepository;

    // --- The service under test ---
    @InjectMocks
    private VolleyballPlayerMatchStatsServiceImpl volleyballPlayerMatchStatsService;

    // --- Common test data ---
    private VolleyballPlayer player;
    private VolleyballMatch match;
    private VolleyballPlayerMatchStats stats;

    @BeforeEach
    void setUp() {
        player = new VolleyballPlayer();
        player.setVolleyball_player_id(1L);

        match = new VolleyballMatch();
        match.setVolleyball_match_id(1L);

        stats = new VolleyballPlayerMatchStats(player, match, 15, 5, 10, 3);
        stats.setId(1L);
    }

    @Test
    void testListAllPlayerStatsForMatch() {
        // Arrange
        when(volleyballPlayerMatchStatsRepository.findByVolleyballMatch(match)).thenReturn(Collections.singletonList(stats));

        // Act
        List<VolleyballPlayerMatchStats> result = volleyballPlayerMatchStatsService.listAllPlayerStatsForMatch(match);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(15, result.get(0).getScoredPoints());
    }

    @Test
    void testCreate() {
        // Arrange
        when(volleyballPlayerMatchStatsRepository.save(any(VolleyballPlayerMatchStats.class))).thenReturn(stats);

        // Act
        VolleyballPlayerMatchStats result = volleyballPlayerMatchStatsService.create(player, match, 15, 5, 10, 3);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getPlayer().getVolleyball_player_id());
        assertEquals(3, result.getBlocks());
        verify(volleyballPlayerMatchStatsRepository).save(any(VolleyballPlayerMatchStats.class));
    }

    @Test
    void testUpdate() {
        // Arrange
        when(volleyballPlayerMatchStatsRepository.findById(1L)).thenReturn(Optional.of(stats));
        when(volleyballPlayerMatchStatsRepository.save(any(VolleyballPlayerMatchStats.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        VolleyballPlayerMatchStats result = volleyballPlayerMatchStatsService.update(1L, player, match, 20, 8, 12, 5);

        // Assert
        assertNotNull(result);
        assertEquals(20, result.getScoredPoints());
        assertEquals(8, result.getAssists());
        assertEquals(5, result.getBlocks());
        verify(volleyballPlayerMatchStatsRepository).save(stats);
    }

    @Test
    void testUpdate_WhenStatsNotFound_ShouldThrowException() {
        // Arrange
        when(volleyballPlayerMatchStatsRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(InvalidVolleyballPlayerException.class, () -> volleyballPlayerMatchStatsService.update(99L, player, match, 20, 8, 12, 5));
    }

    @Test
    void testDelete() {
        // Arrange
        when(volleyballPlayerMatchStatsRepository.findById(1L)).thenReturn(Optional.of(stats));

        // Act
        volleyballPlayerMatchStatsService.delete(1L);

        // Assert
        verify(volleyballPlayerMatchStatsRepository, times(1)).delete(stats);
    }

    @Test
    void testDelete_WhenStatsNotFound_ShouldThrowException() {
        // Arrange
        when(volleyballPlayerMatchStatsRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(InvalidVolleyballPlayerException.class, () -> volleyballPlayerMatchStatsService.delete(99L));
    }

    @Test
    void testSave() {
        // Arrange - no setup needed, just an object to pass

        // Act
        volleyballPlayerMatchStatsService.save(stats);

        // Assert
        verify(volleyballPlayerMatchStatsRepository, times(1)).save(stats);
    }

    @Test
    void testFindByPlayerAndVolleyballMatch() {
        // Arrange
        when(volleyballPlayerMatchStatsRepository.findByPlayerAndVolleyballMatch(player, match)).thenReturn(stats);

        // Act
        VolleyballPlayerMatchStats result = volleyballPlayerMatchStatsService.findByPlayerAndVolleyballMatch(player, match);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }
}
