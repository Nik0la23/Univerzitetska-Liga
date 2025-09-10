package mk.ukim.finki.wp.liga.Football;

import mk.ukim.finki.wp.liga.model.FootballTeam;
import mk.ukim.finki.wp.liga.model.Playoff;
import mk.ukim.finki.wp.liga.model.PlayoffMatch;
import mk.ukim.finki.wp.liga.model.PlayoffStage;
import mk.ukim.finki.wp.liga.repository.football.FootballTeamRepository;
import mk.ukim.finki.wp.liga.repository.football.PlayoffMatchRepository;
import mk.ukim.finki.wp.liga.repository.football.PlayoffRepository;
import mk.ukim.finki.wp.liga.repository.football.PlayoffStageRepository;
import mk.ukim.finki.wp.liga.service.football.impl.FootballPlayoffs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FootballPlayoffsTest {

    @Mock private PlayoffMatchRepository playoffMatchRepository;
    @Mock private PlayoffStageRepository playoffStageRepository;
    @Mock private PlayoffRepository playoffRepository;
    @Mock private FootballTeamRepository footballTeamRepository;

    @InjectMocks
    private FootballPlayoffs footballPlayoffsService;

    private List<FootballTeam> top8Teams;

    @BeforeEach
    void setUp() {
        top8Teams = LongStream.range(1, 9)
                .mapToObj(id -> {
                    FootballTeam team = new FootballTeam();
                    team.setId(id);
                    return team;
                })
                .collect(Collectors.toList());
    }

    @Test
    void testCreatePlayoff_Success() {
        // Arrange
        when(footballTeamRepository.findAllByOrderByTeamLeaguePointsDesc()).thenReturn(top8Teams);

        // Act
        footballPlayoffsService.createPlayoff();

        // Assert
        verify(playoffRepository, times(1)).save(any(Playoff.class));
        verify(playoffStageRepository, times(1)).save(any(PlayoffStage.class));
        // Verify 4 quarter-final matches are created
        verify(playoffMatchRepository, times(4)).save(any(PlayoffMatch.class));
    }

    @Test
    void testCreatePlayoff_NotEnoughTeams_ShouldThrowException() {
        // Arrange
        when(footballTeamRepository.findAllByOrderByTeamLeaguePointsDesc()).thenReturn(top8Teams.subList(0, 4));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> footballPlayoffsService.createPlayoff());
    }

    @Test
    void testCompleteMatch_AndAdvanceToNextStage() {
        // Arrange: Setup a Quarter-Final stage where 3 of 4 matches are already complete.
        Playoff playoff = new Playoff();
        PlayoffStage quarterFinals = new PlayoffStage();
        quarterFinals.setStageNumber(1);
        quarterFinals.setPlayoff(playoff);

        // This is the match we are about to complete
        PlayoffMatch matchToComplete = new PlayoffMatch(quarterFinals, top8Teams.get(0), top8Teams.get(7), 0, 0, false);

        // These are already completed matches in the same stage
        PlayoffMatch completed1 = new PlayoffMatch(quarterFinals, top8Teams.get(1), top8Teams.get(6), 2, 1, true);
        PlayoffMatch completed2 = new PlayoffMatch(quarterFinals, top8Teams.get(2), top8Teams.get(5), 2, 1, true);
        PlayoffMatch completed3 = new PlayoffMatch(quarterFinals, top8Teams.get(3), top8Teams.get(4), 2, 1, true);

        // Link all matches back to the stage
        quarterFinals.setMatches(new ArrayList<>(List.of(matchToComplete, completed1, completed2, completed3)));

        when(playoffMatchRepository.findById(anyLong())).thenReturn(Optional.of(matchToComplete));

        // Act: Complete the final match of the stage
        footballPlayoffsService.completeMatch(1L, 2, 1);

        // Assert
        // 1. Verify the current match was updated and saved
        assertTrue(matchToComplete.isCompleted());
        assertEquals(2, matchToComplete.getHomeTeamPoints());
        verify(playoffMatchRepository, times(1)).save(matchToComplete);

        // 2. Verify that a NEW stage (semi-finals) was created and saved
        verify(playoffStageRepository, times(1)).save(any(PlayoffStage.class));

        // 3. Verify that 2 NEW matches (the semi-finals) were created and saved
        verify(playoffMatchRepository, times(2)).save(argThat(match -> !match.isCompleted()));
    }
}
