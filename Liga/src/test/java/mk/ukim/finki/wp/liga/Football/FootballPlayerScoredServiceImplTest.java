package mk.ukim.finki.wp.liga.Football;

import mk.ukim.finki.wp.liga.model.Exceptions.InvalidFootballPlayerWhoScoredException;
import mk.ukim.finki.wp.liga.model.FootballMatch;
import mk.ukim.finki.wp.liga.model.FootballPlayer;
import mk.ukim.finki.wp.liga.model.FootballPlayerScored;
import mk.ukim.finki.wp.liga.repository.football.FootballPlayerScoredRepository;
import mk.ukim.finki.wp.liga.service.football.impl.FootballPlayerScoredServiceImpl;
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
class FootballPlayerScoredServiceImplTest {

    // --- Mock the repository dependency ---
    @Mock
    private FootballPlayerScoredRepository footballPlayerScoredRepository;

    // --- The service under test ---
    @InjectMocks
    private FootballPlayerScoredServiceImpl footballPlayerScoredService;

    // --- Common test data ---
    private FootballPlayer player;
    private FootballMatch match;
    private FootballPlayerScored playerScored;

    @BeforeEach
    void setUp() {
        player = new FootballPlayer();
        player.setFootball_player_id(1L);
        player.setName("Cristiano");

        match = new FootballMatch();
        match.setFootball_match_id(1L);

        playerScored = new FootballPlayerScored(player, match);
        playerScored.setId(1L);
        playerScored.setGoalsScored(2);
    }

    @Test
    void testListAllPlayersWhoScored() {
        // Arrange
        when(footballPlayerScoredRepository.findAll()).thenReturn(Collections.singletonList(playerScored));

        // Act
        List<FootballPlayerScored> result = footballPlayerScoredService.listAllPlayersWhoScored();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(2, result.get(0).getGoalsScored());
    }

    @Test
    void testFindById_WhenRecordExists() {
        // Arrange
        when(footballPlayerScoredRepository.findById(1L)).thenReturn(Optional.of(playerScored));

        // Act
        FootballPlayerScored result = footballPlayerScoredService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testFindById_WhenRecordDoesNotExist_ShouldThrowException() {
        // Arrange
        when(footballPlayerScoredRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(InvalidFootballPlayerWhoScoredException.class, () -> footballPlayerScoredService.findById(99L));
    }

    @Test
    void testCreate() {
        // Arrange
        when(footballPlayerScoredRepository.save(any(FootballPlayerScored.class))).thenReturn(playerScored);

        // Act
        FootballPlayerScored result = footballPlayerScoredService.create(player, match);

        // Assert
        assertNotNull(result);
        assertEquals("Cristiano", result.getPlayer().getName());
        verify(footballPlayerScoredRepository, times(1)).save(any(FootballPlayerScored.class));
    }

    @Test
    void testUpdate() {
        // Arrange
        when(footballPlayerScoredRepository.findById(1L)).thenReturn(Optional.of(playerScored));
        when(footballPlayerScoredRepository.save(any(FootballPlayerScored.class))).thenAnswer(i -> i.getArgument(0));

        FootballPlayer newPlayer = new FootballPlayer();
        newPlayer.setFootball_player_id(2L);

        // Act
        FootballPlayerScored result = footballPlayerScoredService.update(1L, newPlayer, match);

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.getPlayer().getFootball_player_id());
        verify(footballPlayerScoredRepository).save(playerScored);
    }

    @Test
    void testDelete() {
        // Arrange
        when(footballPlayerScoredRepository.findById(1L)).thenReturn(Optional.of(playerScored));

        // Act
        footballPlayerScoredService.delete(1L);

        // Assert
        verify(footballPlayerScoredRepository, times(1)).delete(playerScored);
    }

    @Test
    void testSave() {
        // Arrange
        when(footballPlayerScoredRepository.save(playerScored)).thenReturn(playerScored);

        // Act
        FootballPlayerScored result = footballPlayerScoredService.save(playerScored);

        // Assert
        assertNotNull(result);
        verify(footballPlayerScoredRepository).save(playerScored);
    }

    @Test
    void testFindByPlayerAndMatch() {
        // Arrange
        when(footballPlayerScoredRepository.findFootballPlayerScoredByPlayerAndFootballMatch(player, match)).thenReturn(playerScored);

        // Act
        FootballPlayerScored result = footballPlayerScoredService.findByPlayerAndMatch(player, match);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testFindByMatch() {
        // Arrange
        when(footballPlayerScoredRepository.findByFootballMatch(match)).thenReturn(Collections.singletonList(playerScored));

        // Act
        List<FootballPlayerScored> result = footballPlayerScoredService.findByMatch(match);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
