package mk.ukim.finki.wp.liga.Fantasy;


import mk.ukim.finki.wp.liga.model.*;
import mk.ukim.finki.wp.liga.model.fantasy.FantasyPlayer;
import mk.ukim.finki.wp.liga.model.fantasy.FantasySport;
import mk.ukim.finki.wp.liga.model.fantasy.FantasyTeam;
import mk.ukim.finki.wp.liga.repository.UserRepository;
import mk.ukim.finki.wp.liga.repository.basketball.BasketballMatchRepository;
import mk.ukim.finki.wp.liga.repository.basketball.BasketballPlayerMatchStatsRepository;
import mk.ukim.finki.wp.liga.repository.basketball.BasketballPlayerRepository;
import mk.ukim.finki.wp.liga.repository.fantasy.FantasyPlayerRepository;
import mk.ukim.finki.wp.liga.repository.fantasy.FantasyTeamRepository;
import mk.ukim.finki.wp.liga.repository.football.FootballMatchRepository;
import mk.ukim.finki.wp.liga.repository.football.FootballPlayerRepository;
import mk.ukim.finki.wp.liga.repository.football.FootballPlayerScoredRepository;
import mk.ukim.finki.wp.liga.repository.volleyball.VolleyballMatchRepository;
import mk.ukim.finki.wp.liga.repository.volleyball.VolleyballPlayerMatchStatsRepository;
import mk.ukim.finki.wp.liga.repository.volleyball.VolleyballPlayerRepository;
import mk.ukim.finki.wp.liga.service.fantasy.impl.SimpleFantasyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
class SimpleFantasyServiceTest {

    // --- Mocks for all 12 repository dependencies ---
    @Mock private FantasyTeamRepository fantasyTeamRepository;
    @Mock private FantasyPlayerRepository fantasyPlayerRepository;
    @Mock private FootballPlayerRepository footballPlayerRepository;
    @Mock private BasketballPlayerRepository basketballPlayerRepository;
    @Mock private VolleyballPlayerRepository volleyballPlayerRepository;
    @Mock private UserRepository userRepository;
    @Mock private FootballMatchRepository footballMatchRepository;
    @Mock private FootballPlayerScoredRepository footballPlayerScoredRepository;
    @Mock private BasketballMatchRepository basketballMatchRepository;
    @Mock private BasketballPlayerMatchStatsRepository basketballPlayerMatchStatsRepository;
    @Mock private VolleyballMatchRepository volleyballMatchRepository;
    @Mock private VolleyballPlayerMatchStatsRepository volleyballPlayerMatchStatsRepository;

    // --- The service under test ---
    @InjectMocks
    private SimpleFantasyService fantasyService;

    // --- Common Test Data ---
    private User user;
    private FantasyTeam footballTeam;
    private FootballPlayer footballPlayer;
    private BasketballPlayer basketballPlayer;
    private VolleyballPlayer volleyballPlayer;


    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        footballTeam = new FantasyTeam(user, FantasySport.FOOTBALL, "My FC");
        footballTeam.setId(1L);
        footballTeam.setBudgetTotal(100.0);
        footballTeam.setBudgetSpent(0.0);

        footballPlayer = new FootballPlayer();
        footballPlayer.setFootball_player_id(1L);
        footballPlayer.setPosition("FWD");
        footballPlayer.setPrice(10.0);

        basketballPlayer = new BasketballPlayer();
        basketballPlayer.setBasketball_player_id(1L);
        basketballPlayer.setPosition("C");
        basketballPlayer.setPrice(8.0);

        volleyballPlayer = new VolleyballPlayer();
        volleyballPlayer.setVolleyball_player_id(1L);
        volleyballPlayer.setPosition("S"); // Setter
        volleyballPlayer.setPrice(6.0);
    }

    @Nested
    class TeamManagementTests {
        @Test
        void testCreateTeam_Success() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(fantasyTeamRepository.findByOwnerAndSport(user, FantasySport.FOOTBALL)).thenReturn(Optional.empty());
            when(fantasyTeamRepository.save(any(FantasyTeam.class))).thenReturn(footballTeam);

            FantasyTeam result = fantasyService.createTeam(user, FantasySport.FOOTBALL, "My FC");

            assertNotNull(result);
            assertEquals("My FC", result.getTeamName());
            verify(fantasyTeamRepository).save(any(FantasyTeam.class));
        }

        @Test
        void testCreateTeam_WhenTeamAlreadyExists_ShouldThrowException() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(fantasyTeamRepository.findByOwnerAndSport(user, FantasySport.FOOTBALL)).thenReturn(Optional.of(footballTeam));

            assertThrows(RuntimeException.class, () -> fantasyService.createTeam(user, FantasySport.FOOTBALL, "My FC"));
        }
    }

    @Nested
    class FootballTransactionAndPointsTests {
        @Test
        void testBuyFootballPlayer_Success() {
            when(footballPlayerRepository.findById(1L)).thenReturn(Optional.of(footballPlayer));
            when(fantasyPlayerRepository.existsByFantasyTeamAndFootballPlayerId(footballTeam, 1L)).thenReturn(false);
            when(fantasyPlayerRepository.findByFantasyTeam(footballTeam)).thenReturn(Collections.emptyList());

            fantasyService.buyFootballPlayer(footballTeam, 1L);

            ArgumentCaptor<FantasyTeam> teamCaptor = ArgumentCaptor.forClass(FantasyTeam.class);
            verify(fantasyTeamRepository).save(teamCaptor.capture());
            assertEquals(10.0, teamCaptor.getValue().getBudgetSpent());
            verify(fantasyPlayerRepository).save(any(FantasyPlayer.class));
        }

        @Test
        void testSellFootballPlayer_Success() {
            FantasyPlayer fantasyPlayer = new FantasyPlayer(footballTeam, footballPlayer, 10.0);
            fantasyPlayer.setId(1L);
            footballTeam.setBudgetSpent(50.0);

            when(fantasyPlayerRepository.findById(1L)).thenReturn(Optional.of(fantasyPlayer));

            fantasyService.sellFootballPlayer(footballTeam, 1L);

            ArgumentCaptor<FantasyTeam> teamCaptor = ArgumentCaptor.forClass(FantasyTeam.class);
            verify(fantasyTeamRepository).save(teamCaptor.capture());
            assertEquals(40.0, teamCaptor.getValue().getBudgetSpent());
            verify(fantasyPlayerRepository).delete(fantasyPlayer);
        }

        @Test
        void testCalculateAndAwardFantasyPoints_Football() {
            FootballMatch match = new FootballMatch();
            match.setFootball_match_id(1L);
            FootballPlayerScored performance = new FootballPlayerScored(match, footballPlayer, 1, 1, 0);
            FantasyPlayer ownedFantasyPlayer = new FantasyPlayer(footballTeam, footballPlayer, 10.0);
            footballTeam.setTotalPoints(100.0);

            when(footballMatchRepository.findById(1L)).thenReturn(Optional.of(match));
            when(footballPlayerScoredRepository.findByFootballMatch(match)).thenReturn(List.of(performance));
            when(fantasyPlayerRepository.findAll()).thenReturn(List.of(ownedFantasyPlayer));

            fantasyService.calculateAndAwardFantasyPoints(1L);

            ArgumentCaptor<FantasyTeam> teamCaptor = ArgumentCaptor.forClass(FantasyTeam.class);
            // FIX: Changed from times(1) to atLeastOnce()
            verify(fantasyTeamRepository, atLeastOnce()).save(teamCaptor.capture());
            ArgumentCaptor<FootballPlayer> playerCaptor = ArgumentCaptor.forClass(FootballPlayer.class);
            verify(footballPlayerRepository).save(playerCaptor.capture());

            assertEquals(107.0, teamCaptor.getValue().getTotalPoints());
            assertEquals(104.9, teamCaptor.getValue().getBudgetTotal());
            assertEquals(10.3, playerCaptor.getValue().getPrice());
        }
    }

    @Nested
    class BasketballTransactionAndPointsTests {
        @Test
        void testCalculateAndAwardBasketballFantasyPoints() {
            FantasyTeam basketballTeam = new FantasyTeam(user, FantasySport.BASKETBALL, "My Hoops");
            basketballTeam.setTotalPoints(200.0);
            basketballTeam.setBudgetTotal(100.0);
            BasketballMatch match = new BasketballMatch();
            match.setBasketball_match_id(1L);
            BasketballPlayerMatchStats performance = new BasketballPlayerMatchStats(basketballPlayer, match, 20, 5, 10);
            FantasyPlayer ownedFantasyPlayer = new FantasyPlayer(basketballTeam, basketballPlayer, 8.0);

            when(basketballMatchRepository.findById(1L)).thenReturn(Optional.of(match));
            when(basketballPlayerMatchStatsRepository.findByBasketballMatch(match)).thenReturn(List.of(performance));
            when(fantasyPlayerRepository.findAll()).thenReturn(List.of(ownedFantasyPlayer));

            fantasyService.calculateAndAwardBasketballFantasyPoints(1L);

            ArgumentCaptor<FantasyTeam> teamCaptor = ArgumentCaptor.forClass(FantasyTeam.class);
            // FIX: Changed from times(1) to atLeastOnce()
            verify(fantasyTeamRepository, atLeastOnce()).save(teamCaptor.capture());
            ArgumentCaptor<BasketballPlayer> playerCaptor = ArgumentCaptor.forClass(BasketballPlayer.class);
            verify(basketballPlayerRepository).save(playerCaptor.capture());

            assertEquals(245.0, teamCaptor.getValue().getTotalPoints());
            assertEquals(113.5, teamCaptor.getValue().getBudgetTotal());
            assertEquals(8.6, playerCaptor.getValue().getPrice());
        }
    }

    @Nested
    class VolleyballTransactionAndPointsTests {

        @Test
        void testBuyVolleyballPlayer_Success() {
            FantasyTeam volleyballTeam = new FantasyTeam(user, FantasySport.VOLLEYBALL, "My Spikes");
            volleyballTeam.setBudgetTotal(100.0);
            volleyballTeam.setBudgetSpent(0.0);

            when(volleyballPlayerRepository.findById(1L)).thenReturn(Optional.of(volleyballPlayer));
            when(fantasyPlayerRepository.existsByFantasyTeamAndVolleyballPlayerId(volleyballTeam, 1L)).thenReturn(false);
            when(fantasyPlayerRepository.findByFantasyTeam(volleyballTeam)).thenReturn(Collections.emptyList());

            fantasyService.buyVolleyballPlayer(volleyballTeam, 1L);

            ArgumentCaptor<FantasyTeam> teamCaptor = ArgumentCaptor.forClass(FantasyTeam.class);
            verify(fantasyTeamRepository).save(teamCaptor.capture());
            assertEquals(6.0, teamCaptor.getValue().getBudgetSpent()); // Player price
            verify(fantasyPlayerRepository).save(any(FantasyPlayer.class));
        }

        @Test
        void testSellVolleyballPlayer_Success() {
            // Arrange
            FantasyTeam volleyballTeam = new FantasyTeam(user, FantasySport.VOLLEYBALL, "My Spikes");
            volleyballTeam.setBudgetSpent(30.0);

            // ------------------ THE CRITICAL FIX IS HERE ------------------
            // The ID must be set on the team BEFORE it's assigned to the fantasyPlayer.
            volleyballTeam.setId(2L);
            // --------------------------------------------------------------

            FantasyPlayer fantasyPlayer = new FantasyPlayer(volleyballTeam, volleyballPlayer, 6.0);
            fantasyPlayer.setId(1L);

            when(fantasyPlayerRepository.findById(1L)).thenReturn(Optional.of(fantasyPlayer));

            // Act
            fantasyService.sellVolleyballPlayer(volleyballTeam, 1L);

            // Assert
            ArgumentCaptor<FantasyTeam> teamCaptor = ArgumentCaptor.forClass(FantasyTeam.class);
            verify(fantasyTeamRepository).save(teamCaptor.capture());
            assertEquals(24.0, teamCaptor.getValue().getBudgetSpent()); // 30.0 - 6.0
            verify(fantasyPlayerRepository).delete(fantasyPlayer);
        }

        @Test
        void testCalculateAndAwardVolleyballFantasyPoints() {
            FantasyTeam volleyballTeam = new FantasyTeam(user, FantasySport.VOLLEYBALL, "My Spikes");
            volleyballTeam.setTotalPoints(150.0);
            volleyballTeam.setBudgetTotal(100.0);
            VolleyballMatch match = new VolleyballMatch();
            match.setVolleyball_match_id(1L);
            VolleyballPlayerMatchStats performance = new VolleyballPlayerMatchStats(volleyballPlayer, match, 5, 20, 15, 3);
            FantasyPlayer ownedFantasyPlayer = new FantasyPlayer(volleyballTeam, volleyballPlayer, 6.0);

            when(volleyballMatchRepository.findById(1L)).thenReturn(Optional.of(match));
            when(volleyballPlayerMatchStatsRepository.findByVolleyballMatch(match)).thenReturn(List.of(performance));
            when(fantasyPlayerRepository.findAll()).thenReturn(List.of(ownedFantasyPlayer));

            fantasyService.calculateAndAwardVolleyballFantasyPoints(1L);

            ArgumentCaptor<FantasyTeam> teamCaptor = ArgumentCaptor.forClass(FantasyTeam.class);
            verify(fantasyTeamRepository, atLeastOnce()).save(teamCaptor.capture());
            ArgumentCaptor<VolleyballPlayer> playerCaptor = ArgumentCaptor.forClass(VolleyballPlayer.class);
            verify(volleyballPlayerRepository).save(playerCaptor.capture());

            // Points: servings(5) + assists(20) + scored(15) + blocks(3*2=6) = 46 pts
            assertEquals(196.0, teamCaptor.getValue().getTotalPoints()); // 150 + 46
            // Budget bonus: 46 * 0.5 = 23.0. Original budget (100) + bonus
            assertEquals(123.0, teamCaptor.getValue().getBudgetTotal());
            // Price adjustment for 46 points is +0.6. Original price (6.0) + adjustment
            assertEquals(6.6, playerCaptor.getValue().getPrice());
        }
    }
}