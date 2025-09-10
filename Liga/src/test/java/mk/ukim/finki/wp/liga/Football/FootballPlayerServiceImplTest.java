package mk.ukim.finki.wp.liga.Football;

import mk.ukim.finki.wp.liga.model.Exceptions.InvalidFootballPlayerException;
import mk.ukim.finki.wp.liga.model.FootballPlayer;
import mk.ukim.finki.wp.liga.model.FootballPlayerScored;
import mk.ukim.finki.wp.liga.model.FootballTeam;
import mk.ukim.finki.wp.liga.model.fantasy.FantasyPlayer;
import mk.ukim.finki.wp.liga.repository.fantasy.FantasyPlayerRepository;
import mk.ukim.finki.wp.liga.repository.football.FootballPlayerRepository;
import mk.ukim.finki.wp.liga.repository.football.FootballPlayerScoredRepository;
import mk.ukim.finki.wp.liga.repository.football.FootballTeamRepository;
import mk.ukim.finki.wp.liga.service.football.impl.FootballPlayerServiceImpl;
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
class FootballPlayerServiceImplTest {

    // --- Mocks for all dependencies ---
    @Mock private FootballPlayerRepository footballPlayerRepository;
    @Mock private FootballTeamRepository footballTeamRepository;
    @Mock private FantasyPlayerRepository fantasyPlayerRepository;
    @Mock private FootballPlayerScoredRepository footballPlayerScoredRepository;

    // --- The service under test ---
    @InjectMocks
    private FootballPlayerServiceImpl footballPlayerService;

    // --- Common Test Data ---
    private FootballPlayer player;
    private FootballTeam team;
    private byte[] image;

    @BeforeEach
    void setUp() {
        image = new byte[]{1, 2, 3};
        team = new FootballTeam();
        team.setId(1L);
        team.setTeamName("Test Team");

        player = new FootballPlayer(image, "Leo", "Messi", new Date(), 123, "Rosario", "FWD", team);
        player.setFootball_player_id(1L);
    }

    @Test
    void testFindById_WhenPlayerExists() {
        when(footballPlayerRepository.findById(1L)).thenReturn(Optional.of(player));
        FootballPlayer result = footballPlayerService.findById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getFootball_player_id());
    }

    @Test
    void testFindById_WhenPlayerDoesNotExist_ShouldThrowException() {
        when(footballPlayerRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(InvalidFootballPlayerException.class, () -> footballPlayerService.findById(99L));
    }

    @Test
    void testCreate_WithTeam() {
        when(footballTeamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(footballPlayerRepository.save(any(FootballPlayer.class))).thenReturn(player);

        FootballPlayer result = footballPlayerService.create(image, "Leo", "Messi", new Date(), 123, "Rosario", "FWD", team);

        assertNotNull(result);
        assertEquals("Test Team", result.getTeam().getTeamName());
        verify(footballPlayerRepository).save(any(FootballPlayer.class));
    }

    @Test
    void testDelete_WhenPlayerHasDependencies() {
        // Arrange
        FantasyPlayer fantasyPlayer = new FantasyPlayer();
        fantasyPlayer.setFootballPlayer(player);
        FootballPlayerScored scoredRecord = new FootballPlayerScored();
        scoredRecord.setPlayer(player);

        when(footballPlayerRepository.findById(1L)).thenReturn(Optional.of(player));
        when(fantasyPlayerRepository.findAll()).thenReturn(Collections.singletonList(fantasyPlayer));
        when(footballPlayerScoredRepository.findByPlayer(player)).thenReturn(Collections.singletonList(scoredRecord));

        // Act
        footballPlayerService.delete(1L);

        // Assert: Verify dependencies are deleted before the player
        verify(fantasyPlayerRepository).deleteAll(anyList());
        verify(footballPlayerScoredRepository).deleteAll(anyList());
        verify(footballPlayerRepository).delete(player);
    }

    @Test
    void testAddStatMethods() {
        when(footballPlayerRepository.findById(1L)).thenReturn(Optional.of(player));
        when(footballPlayerRepository.save(player)).thenReturn(player);

        // Test Appearances
        player.setAppearances(10);
        footballPlayerService.addAppearances(1L);
        assertEquals(11, player.getAppearances());

        // Test Goals
        player.setGoals(20);
        footballPlayerService.addGoals(1L, 2);
        assertEquals(22, player.getGoals());

        // Test Assists
        player.setAssists(15);
        footballPlayerService.addAssists(1L, 1);
        assertEquals(16, player.getAssists());

        // Test Saves
        player.setSaves(5);
        footballPlayerService.addSaves(1L, 3);
        assertEquals(8, player.getSaves());

        // Verify save was called for each stat update
        verify(footballPlayerRepository, times(4)).save(player);
    }

    @Test
    void testGetTop5Players() {
        // Arrange: Mock players and stub getPoints() to isolate the service's sorting logic
        FootballPlayer p1 = mock(FootballPlayer.class);
        FootballPlayer p2 = mock(FootballPlayer.class);
        FootballPlayer p3 = mock(FootballPlayer.class);
        FootballPlayer p4 = mock(FootballPlayer.class);
        FootballPlayer p5 = mock(FootballPlayer.class);
        FootballPlayer p6 = mock(FootballPlayer.class);

        when(p1.getPoints()).thenReturn(100);
        when(p2.getPoints()).thenReturn(500); // Top player
        when(p3.getPoints()).thenReturn(300);
        when(p4.getPoints()).thenReturn(50);
        when(p5.getPoints()).thenReturn(450);
        when(p6.getPoints()).thenReturn(250);

        List<FootballPlayer> allPlayers = Arrays.asList(p1, p2, p3, p4, p5, p6);
        when(footballPlayerRepository.findAll()).thenReturn(allPlayers);

        // Act
        List<FootballPlayer> top5 = footballPlayerService.getTop5Players();

        // Assert
        assertEquals(5, top5.size());
        // Verify the sorting is correct based on our stubbed points
        assertEquals(500, top5.get(0).getPoints());
        assertEquals(450, top5.get(1).getPoints());
        assertEquals(300, top5.get(2).getPoints());
    }

    @Test
    void testGetTop5PlayersByTeam() {
        // Arrange: Create real players to test the specific formula (goals*2 + assists)
        FootballPlayer p1 = new FootballPlayer() {{ setGoals(10); setAssists(5); }}; // Score: 25
        FootballPlayer p2 = new FootballPlayer() {{ setGoals(20); setAssists(1); }}; // Score: 41 (Top)
        FootballPlayer p3 = new FootballPlayer() {{ setGoals(5);  setAssists(5); }}; // Score: 15

        List<FootballPlayer> teamPlayers = Arrays.asList(p1, p2, p3);
        when(footballPlayerRepository.findByTeamId(1L)).thenReturn(teamPlayers);

        // Act
        List<FootballPlayer> top5 = footballPlayerService.getTop5PlayersByTeam(1L);

        // Assert
        assertEquals(3, top5.size()); // Only 3 players, so we get 3 back
        // Verify the sorting is correct based on the formula (goals*2 + assists)
        assertEquals(41, top5.get(0).getGoals() * 2 + top5.get(0).getAssists());
        assertEquals(25, top5.get(1).getGoals() * 2 + top5.get(1).getAssists());
        assertEquals(15, top5.get(2).getGoals() * 2 + top5.get(2).getAssists());
    }
}