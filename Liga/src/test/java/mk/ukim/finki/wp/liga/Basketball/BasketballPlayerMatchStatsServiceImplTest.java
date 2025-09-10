package mk.ukim.finki.wp.liga.Basketball;

import mk.ukim.finki.wp.liga.model.BasketballMatch;
import mk.ukim.finki.wp.liga.model.BasketballPlayer;
import mk.ukim.finki.wp.liga.model.BasketballPlayerMatchStats;
import mk.ukim.finki.wp.liga.model.Exceptions.InvalidBasketballPlayerException;
import mk.ukim.finki.wp.liga.model.Exceptions.InvalidBasketballPlayerMatchStatsException;
import mk.ukim.finki.wp.liga.repository.basketball.BasketballMatchRepository;
import mk.ukim.finki.wp.liga.repository.basketball.BasketballPlayerMatchStatsRepository;
import mk.ukim.finki.wp.liga.repository.basketball.BasketballPlayerRepository;
import mk.ukim.finki.wp.liga.service.basketball.impl.BasketballPlayerMatchStatsServiceImpl;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BasketballPlayerMatchStatsServiceImplTest {

    // --- Mocks for the repositories the service depends on ---
    @Mock
    private BasketballPlayerMatchStatsRepository basketballPlayerMatchStatsRepository;
    @Mock
    private BasketballPlayerRepository basketballPlayerRepository;
    @Mock
    private BasketballMatchRepository basketballMatchRepository;

    // --- The service we are testing, with mocks automatically injected ---
    @InjectMocks
    private BasketballPlayerMatchStatsServiceImpl basketballPlayerMatchStatsService;

    // --- Common test data initialized before each test ---
    private BasketballPlayer player;
    private BasketballMatch match;
    private BasketballPlayerMatchStats stats;

    @BeforeEach
    void setUp() {
        // Create player instance
        player = new BasketballPlayer();
        player.setBasketball_player_id(1L);
        player.setName("LeBron James");

        // Create match instance
        match = new BasketballMatch();
        match.setBasketball_match_id(1L);

        // Create stats instance linking the player and match
        stats = new BasketballPlayerMatchStats(player, match, 30, 8, 12);
        stats.setId(1L);
    }

    // --- Tests for each public method in the service ---

    @Test
    void testListAllPlayerStatsForMatch() {
        // Arrange: When the repository is asked for stats for a specific match, return our test stats.
        when(basketballPlayerMatchStatsRepository.findByBasketballMatch(match)).thenReturn(Collections.singletonList(stats));

        // Act: Call the service method.
        List<BasketballPlayerMatchStats> result = basketballPlayerMatchStatsService.listAllPlayerStatsForMatch(match);

        // Assert: Check that the result is correct.
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(30, result.get(0).getPointsScored());
    }

    @Test
    void testFindByPlayerAndBasketballMatch() {
        // Arrange: When the repository is asked for stats for a specific player and match, return our test stats.
        when(basketballPlayerMatchStatsRepository.findByPlayerAndBasketballMatch(player, match)).thenReturn(stats);

        // Act: Call the service method.
        BasketballPlayerMatchStats result = basketballPlayerMatchStatsService.findByPlayerAndBasketballMatch(player, match);

        // Assert: Check that the result is correct.
        assertNotNull(result);
        assertEquals(player.getName(), result.getPlayer().getName());
    }

    @Test
    void testCreate_WhenPlayerExists_ShouldSaveAndReturnStats() {
        // Arrange: Ensure the player repository finds our player and the stats repository returns the saved object.
        when(basketballPlayerRepository.findById(1L)).thenReturn(Optional.of(player));
        when(basketballPlayerMatchStatsRepository.save(any(BasketballPlayerMatchStats.class))).thenReturn(stats);

        // Act: Call the create method.
        BasketballPlayerMatchStats result = basketballPlayerMatchStatsService.create(player, match, 30, 8, 12);

        // Assert: Verify the result and that the save method was called.
        assertNotNull(result);
        assertEquals(30, result.getPointsScored());
        verify(basketballPlayerMatchStatsRepository, times(1)).save(any(BasketballPlayerMatchStats.class));
    }

    @Test
    void testCreate_WhenPlayerDoesNotExist_ShouldThrowException() {
        // Arrange: Mock the player repository to return an empty optional, simulating a non-existent player.
        BasketballPlayer nonExistentPlayer = new BasketballPlayer();
        nonExistentPlayer.setBasketball_player_id(99L);
        when(basketballPlayerRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert: Check that the correct exception is thrown.
        assertThrows(InvalidBasketballPlayerException.class, () -> basketballPlayerMatchStatsService.create(nonExistentPlayer, match, 30, 8, 12));
    }

    @Test
    void testUpdate_WhenAllEntitiesExist_ShouldUpdateAndReturnStats() {
        // Arrange: Mock all repository "findById" calls to successfully find the entities.
        when(basketballPlayerMatchStatsRepository.findById(1L)).thenReturn(Optional.of(stats));
        when(basketballPlayerRepository.findById(1L)).thenReturn(Optional.of(player));
        when(basketballMatchRepository.findById(1L)).thenReturn(Optional.of(match));

        // When save is called, return the modified stats object.
        when(basketballPlayerMatchStatsRepository.save(any(BasketballPlayerMatchStats.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act: Call the update method with new values.
        BasketballPlayerMatchStats result = basketballPlayerMatchStatsService.update(1L, player, match, 35, 10, 15);

        // Assert: Verify the stats were updated.
        assertNotNull(result);
        assertEquals(35, result.getPointsScored());
        assertEquals(10, result.getAssists());
        assertEquals(15, result.getRebounds());
        verify(basketballPlayerMatchStatsRepository, times(1)).save(stats);
    }

    @Test
    void testUpdate_WhenStatsDoNotExist_ShouldThrowException() {
        // Arrange: Mock the stats repository to not find the initial stats record.
        when(basketballPlayerMatchStatsRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert: Check for the correct exception.
        assertThrows(InvalidBasketballPlayerMatchStatsException.class, () -> basketballPlayerMatchStatsService.update(99L, player, match, 35, 10, 15));
    }

    @Test
    void testDelete_WhenStatsExist() {
        // Arrange: Mock the repository to find the stats record to be deleted.
        when(basketballPlayerMatchStatsRepository.findById(1L)).thenReturn(Optional.of(stats));
        // doNothing() is used for void methods. It ensures no exception is thrown.
        doNothing().when(basketballPlayerMatchStatsRepository).delete(stats);

        // Act: Call the delete method.
        BasketballPlayerMatchStats result = basketballPlayerMatchStatsService.delete(1L);

        // Assert: Verify the result is the object that was deleted and that the delete method was called.
        assertNotNull(result);
        verify(basketballPlayerMatchStatsRepository, times(1)).delete(stats);
    }

    @Test
    void testDelete_WhenStatsDoNotExist_ShouldThrowException() {
        // Arrange: Mock the repository to not find any stats with the given ID.
        when(basketballPlayerMatchStatsRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert: Check for the correct exception.
        assertThrows(InvalidBasketballPlayerMatchStatsException.class, () -> basketballPlayerMatchStatsService.delete(99L));
    }

    @Test
    void testSave() {
        // Arrange: No special arrangement needed, just a stats object to pass.

        // Act: Call the save method.
        basketballPlayerMatchStatsService.save(stats);

        // Assert: Verify that the repository's save method was called exactly once with our stats object.
        verify(basketballPlayerMatchStatsRepository, times(1)).save(stats);
    }
}
