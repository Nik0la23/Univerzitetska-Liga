package mk.ukim.finki.wp.liga.Basketball;

import mk.ukim.finki.wp.liga.model.BasketballPlayer;
import mk.ukim.finki.wp.liga.model.BasketballPlayerMatchStats;
import mk.ukim.finki.wp.liga.model.BasketballTeam;
import mk.ukim.finki.wp.liga.model.Exceptions.InvalidBasketballPlayerException;
import mk.ukim.finki.wp.liga.model.Exceptions.InvalidBasketballTeamException;
import mk.ukim.finki.wp.liga.model.fantasy.FantasyPlayer;
import mk.ukim.finki.wp.liga.repository.basketball.BasketballPlayerMatchStatsRepository;
import mk.ukim.finki.wp.liga.repository.basketball.BasketballPlayerRepository;
import mk.ukim.finki.wp.liga.repository.basketball.BasketballTeamRepository;
import mk.ukim.finki.wp.liga.repository.fantasy.FantasyPlayerRepository;
import mk.ukim.finki.wp.liga.service.basketball.impl.BasketballPlayerServiceImpl;
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
class BasketballPlayerServiceImplTest {

    // --- Mocks for all dependencies ---
    @Mock
    private BasketballPlayerRepository basketballPlayerRepository;
    @Mock
    private BasketballTeamRepository basketballTeamRepository;
    @Mock
    private FantasyPlayerRepository fantasyPlayerRepository;
    @Mock
    private BasketballPlayerMatchStatsRepository basketballPlayerMatchStatsRepository;

    // --- The service under test ---
    @InjectMocks
    private BasketballPlayerServiceImpl basketballPlayerService;

    // --- Common test data ---
    private BasketballPlayer player;
    private BasketballTeam team;
    private byte[] image;

    @BeforeEach
    void setUp() {
        image = new byte[]{1, 2, 3};

        team = new BasketballTeam();
        team.setId(1L);
        team.setTeamName("Test Team");

        player = new BasketballPlayer(image, "John", "Doe", new Date(), 123, "Test City", "Center", team);
        player.setBasketball_player_id(1L);
    }

    @Test
    void testListAllPlayers() {
        when(basketballPlayerRepository.findAll()).thenReturn(Collections.singletonList(player));
        List<BasketballPlayer> result = basketballPlayerService.listAllPlayers();
        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getName());
    }

    @Test
    void testFindById_WhenPlayerExists() {
        when(basketballPlayerRepository.findById(1L)).thenReturn(Optional.of(player));
        BasketballPlayer result = basketballPlayerService.findById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getBasketball_player_id());
    }

    @Test
    void testFindById_WhenPlayerDoesNotExist_ShouldThrowException() {
        when(basketballPlayerRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(InvalidBasketballPlayerException.class, () -> basketballPlayerService.findById(99L));
    }

    @Test
    void testCreate_WithTeam() {
        when(basketballTeamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(basketballPlayerRepository.save(any(BasketballPlayer.class))).thenReturn(player);

        BasketballPlayer result = basketballPlayerService.create(image, "John", "Doe", new Date(), 123, "Test City", "Center", team);

        assertNotNull(result);
        assertEquals("Test Team", result.getTeam().getTeamName());
        verify(basketballPlayerRepository, times(1)).save(any(BasketballPlayer.class));
    }

    @Test
    void testCreate_WithoutTeam() {
        when(basketballPlayerRepository.save(any(BasketballPlayer.class))).thenReturn(player);
        player.setTeam(null); // Adjust test player for this scenario

        BasketballPlayer result = basketballPlayerService.create(image, "John", "Doe", new Date(), 123, "Test City", "Center", null);

        assertNotNull(result);
        assertNull(result.getTeam());
        verify(basketballTeamRepository, never()).findById(anyLong()); // Ensure team repo was never called
    }

    @Test
    void testUpdate_WhenPlayerAndTeamExist() {
        when(basketballPlayerRepository.findById(1L)).thenReturn(Optional.of(player));
        when(basketballTeamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(basketballPlayerRepository.save(any(BasketballPlayer.class))).thenAnswer(i -> i.getArgument(0));

        BasketballPlayer result = basketballPlayerService.update(1L, null, "Jane", "Doe", new Date(), 456, "New City", "Forward", team);

        assertEquals("Jane", result.getName());
        assertEquals(456, result.getIndex());
        assertArrayEquals(image, result.getImage()); // Image should not be changed if new one is null
        verify(basketballPlayerRepository, times(1)).save(player);
    }

    @Test
    void testUpdate_WhenTeamIsInvalid_ShouldThrowException() {
        when(basketballPlayerRepository.findById(1L)).thenReturn(Optional.of(player));
        when(basketballTeamRepository.findById(99L)).thenReturn(Optional.empty());

        BasketballTeam invalidTeam = new BasketballTeam();
        invalidTeam.setId(99L);

        assertThrows(InvalidBasketballTeamException.class, () -> basketballPlayerService.update(1L, null, "Jane", "Doe", new Date(), 456, "New City", "Forward", invalidTeam));
    }

    @Test
    void testDelete_WhenPlayerHasDependencies() {
        // Arrange
        FantasyPlayer fantasyPlayer = new FantasyPlayer();
        fantasyPlayer.setBasketballPlayer(player);
        BasketballPlayerMatchStats matchStats = new BasketballPlayerMatchStats();
        matchStats.setPlayer(player);

        when(basketballPlayerRepository.findById(1L)).thenReturn(Optional.of(player));
        when(fantasyPlayerRepository.findAll()).thenReturn(Collections.singletonList(fantasyPlayer));
        when(basketballPlayerMatchStatsRepository.findByPlayer(player)).thenReturn(Collections.singletonList(matchStats));

        // Act
        basketballPlayerService.delete(1L);

        // Assert: Verify that the dependent objects are deleted before the player itself.
        verify(fantasyPlayerRepository, times(1)).deleteAll(anyList());
        verify(basketballPlayerMatchStatsRepository, times(1)).deleteAll(anyList());
        verify(basketballPlayerRepository, times(1)).delete(player);
    }

    @Test
    void testAddStatMethods() {
        when(basketballPlayerRepository.findById(1L)).thenReturn(Optional.of(player));
        when(basketballPlayerRepository.save(any(BasketballPlayer.class))).thenReturn(player);

        // Test Add Appearances
        player.setAppearances(10);
        basketballPlayerService.addAppearances(1L);
        assertEquals(11, player.getAppearances());

        // Test Add Points
        player.setPoints(100);
        basketballPlayerService.addPoints(1L, 25);
        assertEquals(125, player.getPoints());

        // Test Add Assists
        player.setAssists(50);
        basketballPlayerService.addAssists(1L, 5);
        assertEquals(55, player.getAssists());

        // Test Add Rebounds
        player.setRebounds(80);
        basketballPlayerService.addRebounds(1L, 10);
        assertEquals(90, player.getRebounds());

        // Verify save was called 4 times in total
        verify(basketballPlayerRepository, times(4)).save(player);
    }

    @Test
    void testGetTop5Players() {
        // Arrange: Create MOCK players and tell Mockito exactly what their total points should be.
        BasketballPlayer player1 = mock(BasketballPlayer.class);
        BasketballPlayer player2 = mock(BasketballPlayer.class);
        BasketballPlayer player3 = mock(BasketballPlayer.class);
        BasketballPlayer player4 = mock(BasketballPlayer.class);
        BasketballPlayer player5 = mock(BasketballPlayer.class);
        BasketballPlayer player6 = mock(BasketballPlayer.class);

        // Stub the getTotalPoints() method for each mock player
        when(player1.getTotalPoints()).thenReturn(100);
        when(player2.getTotalPoints()).thenReturn(500); // This will be the top player
        when(player3.getTotalPoints()).thenReturn(300);
        when(player4.getTotalPoints()).thenReturn(50);
        when(player5.getTotalPoints()).thenReturn(450);
        when(player6.getTotalPoints()).thenReturn(250);

        List<BasketballPlayer> allPlayers = Arrays.asList(player1, player2, player3, player4, player5, player6);

        when(basketballPlayerRepository.findAll()).thenReturn(allPlayers);

        // Act
        List<BasketballPlayer> top5 = basketballPlayerService.getTop5Players();

        // Assert
        assertEquals(5, top5.size());
        // Now the assertion will pass because we guaranteed the top score is 500.
        assertEquals(500, top5.get(0).getTotalPoints());
        assertEquals(450, top5.get(1).getTotalPoints());
        assertEquals(300, top5.get(2).getTotalPoints());
        assertEquals(250, top5.get(3).getTotalPoints());
        assertEquals(100, top5.get(4).getTotalPoints());
    }

    @Test
    void testAddStats() {
        when(basketballPlayerRepository.findById(1L)).thenReturn(Optional.of(player));
        player.setPoints(100);

        basketballPlayerService.addStats(1L, 20);

        assertEquals(120, player.getPoints());
        verify(basketballPlayerRepository, times(1)).save(player);
    }

}
