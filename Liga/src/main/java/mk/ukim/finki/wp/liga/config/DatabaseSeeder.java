package mk.ukim.finki.wp.liga.config;

import jakarta.annotation.PostConstruct;
import mk.ukim.finki.wp.liga.model.*;
import mk.ukim.finki.wp.liga.model.fantasy.FantasySport;
import mk.ukim.finki.wp.liga.service.UserService;
import mk.ukim.finki.wp.liga.service.basketball.BasketballMatchService;
import mk.ukim.finki.wp.liga.service.basketball.BasketballPlayerService;
import mk.ukim.finki.wp.liga.service.basketball.BasketballTeamService;
import mk.ukim.finki.wp.liga.service.fantasy.FantasyService;
import mk.ukim.finki.wp.liga.service.football.FootballMatchService;
import mk.ukim.finki.wp.liga.service.football.FootballPlayerService;
import mk.ukim.finki.wp.liga.service.football.FootballTeamService;
import mk.ukim.finki.wp.liga.service.news.NewsService;
import mk.ukim.finki.wp.liga.service.volleyball.VolleyballMatchService;
import mk.ukim.finki.wp.liga.service.volleyball.VolleyballPlayerService;
import mk.ukim.finki.wp.liga.service.volleyball.VolleyballTeamService;
import mk.ukim.finki.wp.liga.repository.football.FootballProductRepository;
import mk.ukim.finki.wp.liga.repository.basketball.BasketballProductRepository;
import mk.ukim.finki.wp.liga.repository.volleyball.VolleyballProductRepository;
import mk.ukim.finki.wp.liga.repository.football.FootballMatchRepository;
import mk.ukim.finki.wp.liga.repository.basketball.BasketballMatchRepository;
import mk.ukim.finki.wp.liga.repository.volleyball.VolleyballMatchRepository;
import mk.ukim.finki.wp.liga.repository.football.FootballPlayerRepository;
import mk.ukim.finki.wp.liga.repository.basketball.BasketballPlayerRepository;
import mk.ukim.finki.wp.liga.repository.volleyball.VolleyballPlayerRepository;
import mk.ukim.finki.wp.liga.repository.football.FootballTeamRepository;
import mk.ukim.finki.wp.liga.repository.basketball.BasketballTeamRepository;
import mk.ukim.finki.wp.liga.repository.volleyball.VolleyballTeamRepository;
import mk.ukim.finki.wp.liga.model.shop.FootballProduct;
import mk.ukim.finki.wp.liga.model.shop.BasketballProduct;
import mk.ukim.finki.wp.liga.model.shop.VolleyballProduct;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class DatabaseSeeder {

    private final FootballTeamService footballTeamService;
    private final FootballPlayerService footballPlayerService;
    private final FootballMatchService footballMatchService;

    private final VolleyballTeamService volleyballTeamService;
    private final VolleyballPlayerService volleyballPlayerService;
    private final VolleyballMatchService volleyballMatchService;

    private final BasketballTeamService basketballTeamService;
    private final BasketballPlayerService basketballPlayerService;
    private final BasketballMatchService basketballMatchService;

    private final UserService userService;
    private final FantasyService fantasyService;
    private final NewsService newsService;
    
    private final FootballProductRepository footballProductRepository;
    private final BasketballProductRepository basketballProductRepository;
    private final VolleyballProductRepository volleyballProductRepository;
    
    private final FootballMatchRepository footballMatchRepository;
    private final BasketballMatchRepository basketballMatchRepository;
    private final VolleyballMatchRepository volleyballMatchRepository;
    
    private final FootballPlayerRepository footballPlayerRepository;
    private final BasketballPlayerRepository basketballPlayerRepository;
    private final VolleyballPlayerRepository volleyballPlayerRepository;
    
    private final FootballTeamRepository footballTeamRepository;
    private final BasketballTeamRepository basketballTeamRepository;
    private final VolleyballTeamRepository volleyballTeamRepository;

    public DatabaseSeeder(FootballTeamService footballTeamService, 
                         FootballPlayerService footballPlayerService, 
                         FootballMatchService footballMatchService,
                         VolleyballTeamService volleyballTeamService, 
                         VolleyballPlayerService volleyballPlayerService, 
                         VolleyballMatchService volleyballMatchService,
                         BasketballTeamService basketballTeamService, 
                         BasketballPlayerService basketballPlayerService, 
                         BasketballMatchService basketballMatchService,
                         UserService userService,
                         FantasyService fantasyService,
                         NewsService newsService,
                         FootballProductRepository footballProductRepository,
                         BasketballProductRepository basketballProductRepository,
                         VolleyballProductRepository volleyballProductRepository,
                         FootballMatchRepository footballMatchRepository,
                         BasketballMatchRepository basketballMatchRepository,
                         VolleyballMatchRepository volleyballMatchRepository,
                         FootballPlayerRepository footballPlayerRepository,
                         BasketballPlayerRepository basketballPlayerRepository,
                         VolleyballPlayerRepository volleyballPlayerRepository,
                         FootballTeamRepository footballTeamRepository,
                         BasketballTeamRepository basketballTeamRepository,
                         VolleyballTeamRepository volleyballTeamRepository) {
        this.footballTeamService = footballTeamService;
        this.footballPlayerService = footballPlayerService;
        this.footballMatchService = footballMatchService;
        this.volleyballTeamService = volleyballTeamService;
        this.volleyballPlayerService = volleyballPlayerService;
        this.volleyballMatchService = volleyballMatchService;
        this.basketballTeamService = basketballTeamService;
        this.basketballPlayerService = basketballPlayerService;
        this.basketballMatchService = basketballMatchService;
        this.userService = userService;
        this.fantasyService = fantasyService;
        this.newsService = newsService;
        this.footballProductRepository = footballProductRepository;
        this.basketballProductRepository = basketballProductRepository;
        this.volleyballProductRepository = volleyballProductRepository;
        this.footballMatchRepository = footballMatchRepository;
        this.basketballMatchRepository = basketballMatchRepository;
        this.volleyballMatchRepository = volleyballMatchRepository;
        this.footballPlayerRepository = footballPlayerRepository;
        this.basketballPlayerRepository = basketballPlayerRepository;
        this.volleyballPlayerRepository = volleyballPlayerRepository;
        this.footballTeamRepository = footballTeamRepository;
        this.basketballTeamRepository = basketballTeamRepository;
        this.volleyballTeamRepository = volleyballTeamRepository;
    }

    @PostConstruct
    @Transactional
    public void seedDatabase() {
        System.out.println("🌱 Starting database seeding...");
        
        try {
            // Always seed the database since we're using H2 in-memory database
            // Data is lost on application restart, so we need to recreate it
            createUsers();
            createFootballData();
            createBasketballData();
            createVolleyballData();
            createShopData();
            createNews();
            createFantasyTeams();
            
            System.out.println("✅ Database seeding completed successfully!");
            System.out.println("🎯 Application is ready! Access it at: http://localhost:9090");
            System.out.println("🔍 H2 Console available at: http://localhost:9090/h2-console");
            System.out.println("   JDBC URL: jdbc:h2:mem:liga_db");
            System.out.println("   Username: sa");
            System.out.println("   Password: (leave empty)");
        } catch (Exception e) {
            System.err.println("❌ Error during database seeding: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Image utility methods
    private byte[] readImageFile(String imagePath) {
        try {
            Path path = Paths.get(imagePath);
            if (Files.exists(path)) {
                return Files.readAllBytes(path);
            } else {
                System.out.println("⚠️ Image file not found: " + imagePath);
                return new byte[0];
            }
        } catch (IOException e) {
            System.out.println("⚠️ Error reading image file " + imagePath + ": " + e.getMessage());
            return new byte[0];
        }
    }

    private String getTeamLogoPath(int teamIndex) {
        String[] teamLogos = {
            "images/team_logos/team1.webp",
            "images/team_logos/team2.png", 
            "images/team_logos/team3.webp",
            "images/team_logos/team4.png"
        };
        return teamLogos[teamIndex % teamLogos.length];
    }

    private String getPlayerImagePath(int playerIndex) {
        String[] playerImages = {
            "images/players/player1.png",
            "images/players/player2.jpg",
            "images/players/player3.jpg", 
            "images/players/player4.jpg",
            "images/players/player5.png"
        };
        return playerImages[playerIndex % playerImages.length];
    }

    private String getJerseyImagePath(int jerseyIndex) {
        String[] jerseyImages = {
            "/images/jerseys/jersey1.jpg",
            "/images/jerseys/jersey2.jpeg"
        };
        return jerseyImages[jerseyIndex % jerseyImages.length];
    }

    private String getHatImagePath(int hatIndex) {
        return "/images/hats/hat1.jpeg"; // Only one hat image available
    }

    private void createUsers() {
        System.out.println("👥 Creating users...");
        
        try {
            User admin = userService.register("admin", "admin@liga.com", "admin123");
            admin.setIsAdmin(true);
            userService.save(admin);
            System.out.println("✅ Created admin user: " + admin.getName() + " (ID: " + admin.getId() + ")");
            
            User user1 = userService.register("user1", "user1@liga.com", "password123");
            System.out.println("✅ Created user1: " + user1.getName() + " (ID: " + user1.getId() + ")");
            
            User user2 = userService.register("user2", "user2@liga.com", "password123");
            System.out.println("✅ Created user2: " + user2.getName() + " (ID: " + user2.getId() + ")");
            
            User coach1 = userService.register("coach1", "coach1@liga.com", "password123");
            System.out.println("✅ Created coach1: " + coach1.getName() + " (ID: " + coach1.getId() + ")");
            
            User player1 = userService.register("player1", "player1@liga.com", "password123");
            System.out.println("✅ Created player1: " + player1.getName() + " (ID: " + player1.getId() + ")");
            
            System.out.println("👥 All users created successfully!");
        } catch (Exception e) {
            System.out.println("❌ Error creating users: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createFootballData() {
        System.out.println("⚽ Creating football data...");
        
        // Create football teams with real logos
        FootballTeam team1 = footballTeamService.create("Градежен факултет", readImageFile(getTeamLogoPath(0)));
        FootballTeam team2 = footballTeamService.create("Медицински факултет", readImageFile(getTeamLogoPath(1)));
        FootballTeam team3 = footballTeamService.create("Технички факултет", readImageFile(getTeamLogoPath(2)));
        FootballTeam team4 = footballTeamService.create("Економски факултет", readImageFile(getTeamLogoPath(3)));
        FootballTeam team5 = footballTeamService.create("Филолошки факултет", readImageFile(getTeamLogoPath(0)));
        FootballTeam team6 = footballTeamService.create("Правен факултет", readImageFile(getTeamLogoPath(1)));
        FootballTeam team7 = footballTeamService.create("Педагошки факултет", readImageFile(getTeamLogoPath(2)));
        FootballTeam team8 = footballTeamService.create("Факултет за информатички науки", readImageFile(getTeamLogoPath(3)));

        // Create football players
        createFootballPlayers(team1, "Градежен");
        createFootballPlayers(team2, "Медицински");
        createFootballPlayers(team3, "Технички");
        createFootballPlayers(team4, "Економски");
        createFootballPlayers(team5, "Филолошки");
        createFootballPlayers(team6, "Правен");
        createFootballPlayers(team7, "Педагошки");
        createFootballPlayers(team8, "Информатички");

        // Create football matches
        createFootballMatches();
    }

    private void createFootballPlayers(FootballTeam team, String teamPrefix) {
        String[] positions = {"GK", "DEF", "DEF", "DEF", "MID", "MID", "MID", "FWD", "FWD", "FWD", "SUB"};
        String[] firstNames = {"Александар", "Марко", "Никола", "Стефан", "Димитар", "Петар", "Владимир", "Милош", "Бојан", "Филип", "Андреј"};
        String[] lastNames = {"Петровски", "Јовановски", "Стојановски", "Трајковски", "Ангеловски", "Митевски", "Георгиевски", "Ивановски", "Наумовски", "Спасовски", "Крстевски"};

        for (int i = 0; i < 11; i++) {
            try {
                // Create a random birth date between 1995 and 2005
                LocalDate localDate = LocalDate.of(1995 + (int)(Math.random() * 10), 
                    (int)(Math.random() * 12) + 1, 
                    (int)(Math.random() * 28) + 1);
                Date birthDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                
                FootballPlayer player = footballPlayerService.create(
                    readImageFile(getPlayerImagePath(i)), // real player image
                    firstNames[i % firstNames.length],
                    lastNames[i % lastNames.length],
                    birthDate,
                    1000 + i,
                    "Скопје",
                    positions[i],
                    team
                );
                
                // Assign realistic statistics based on position
                assignFootballPlayerStats(player, positions[i]);
                
            } catch (Exception e) {
                System.out.println("⚠️ Error creating football player: " + e.getMessage());
            }
        }
    }
    
    private void assignFootballPlayerStats(FootballPlayer player, String position) {
        // Assign realistic statistics based on position
        int appearances = 8 + (int)(Math.random() * 5); // 8-12 appearances
        
        // Get team's total goals from COMPLETED matches only (Past Results)
        List<FootballMatch> teamMatches = footballMatchService.listAllFootballMatches().stream()
            .filter(match -> (match.getHomeTeam().equals(player.getTeam()) || match.getAwayTeam().equals(player.getTeam()))
                    && match.getEndTime().isBefore(LocalDateTime.now())) // Only completed matches
            .collect(Collectors.toList());
        
        int teamTotalGoals = teamMatches.stream()
            .mapToInt(match -> match.getHomeTeam().equals(player.getTeam()) ? 
                match.getHomeTeamPoints() : match.getAwayTeamPoints())
            .sum();
        
        // Calculate realistic stats based on team performance (only from completed matches)
        int maxGoalsPerPlayer = Math.max(1, teamTotalGoals / 11); // Distribute goals among 11 players
        
        System.out.println("⚽ " + player.getTeam().getTeamName() + " - " + player.getName() + " " + player.getSurname() + 
                          " (Completed matches: " + teamMatches.size() + ", Team total goals: " + teamTotalGoals + 
                          ", Max goals per player: " + maxGoalsPerPlayer + ")");
        
        switch (position) {
            case "GK":
                player.setGoals(0); // Goalkeepers rarely score
                player.setAssists(1 + (int)(Math.random() * 3)); // 1-3 assists
                player.setSaves(15 + (int)(Math.random() * 20)); // 15-35 saves
                break;
            case "DEF":
                player.setGoals(Math.min(1 + (int)(Math.random() * 3), maxGoalsPerPlayer)); // 1-3 goals, but not more than team average
                player.setAssists(2 + (int)(Math.random() * 4)); // 2-5 assists
                player.setSaves(0); // Defenders don't make saves
                break;
            case "MID":
                player.setGoals(Math.min(2 + (int)(Math.random() * 5), maxGoalsPerPlayer * 2)); // 2-6 goals, but constrained
                player.setAssists(3 + (int)(Math.random() * 6)); // 3-8 assists
                player.setSaves(0); // Midfielders don't make saves
                break;
            case "FWD":
                player.setGoals(Math.min(4 + (int)(Math.random() * 8), maxGoalsPerPlayer * 3)); // 4-11 goals, but constrained
                player.setAssists(1 + (int)(Math.random() * 4)); // 1-4 assists
                player.setSaves(0); // Forwards don't make saves
                break;
            case "SUB":
                player.setGoals(Math.min(0 + (int)(Math.random() * 2), maxGoalsPerPlayer)); // 0-1 goals, but constrained
                player.setAssists(0 + (int)(Math.random() * 2)); // 0-1 assists
                player.setSaves(0); // Substitutes rarely make saves
                break;
        }
        
        player.setAppearances(appearances);
        
        // Update fantasy points
        player.setFantasyPoints(player.getPoints());
        
        // Save the updated player with statistics
        footballPlayerRepository.save(player);
    }

    private void createFootballMatches() {
        List<FootballTeam> teams = footballTeamService.listAllTeams();
        
        // Create some sample matches
        if (teams.size() >= 8) {
            try {
                // Week 1 matches
                createFootballMatchWithHalves(teams.get(0), teams.get(1), 2, 1, LocalDateTime.now().minusDays(7));
                createFootballMatchWithHalves(teams.get(2), teams.get(3), 0, 3, LocalDateTime.now().minusDays(7));
                createFootballMatchWithHalves(teams.get(4), teams.get(5), 1, 1, LocalDateTime.now().minusDays(7));
                createFootballMatchWithHalves(teams.get(6), teams.get(7), 2, 0, LocalDateTime.now().minusDays(7));
                
                // Week 2 matches
                createFootballMatchWithHalves(teams.get(1), teams.get(2), 1, 2, LocalDateTime.now().minusDays(5));
                createFootballMatchWithHalves(teams.get(3), teams.get(4), 3, 1, LocalDateTime.now().minusDays(5));
                createFootballMatchWithHalves(teams.get(5), teams.get(6), 0, 2, LocalDateTime.now().minusDays(5));
                createFootballMatchWithHalves(teams.get(7), teams.get(0), 1, 1, LocalDateTime.now().minusDays(5));
                
                // Week 3 matches
                createFootballMatchWithHalves(teams.get(0), teams.get(3), 2, 2, LocalDateTime.now().minusDays(3));
                createFootballMatchWithHalves(teams.get(1), teams.get(5), 1, 0, LocalDateTime.now().minusDays(3));
                createFootballMatchWithHalves(teams.get(2), teams.get(7), 3, 1, LocalDateTime.now().minusDays(3));
                createFootballMatchWithHalves(teams.get(4), teams.get(6), 0, 1, LocalDateTime.now().minusDays(3));
                
                // Update team statistics after creating all matches
                updateFootballTeamStatistics();
                
                System.out.println("✅ Created 12 football matches and updated team statistics");
            } catch (Exception e) {
                System.out.println("⚠️ Error creating football matches: " + e.getMessage());
            }
        }
    }
    
    private void createFootballMatchWithHalves(FootballTeam homeTeam, FootballTeam awayTeam, 
                                             int homeTotal, int awayTotal, LocalDateTime startTime) {
        // Create the match first
        FootballMatch match = footballMatchService.createAndAddToFixtures(homeTeam, awayTeam, homeTotal, awayTotal, startTime);
        
        // Set endTime to be in the past so it's counted as completed
        match.setEndTime(startTime.plusHours(2)); // Match ends 2 hours after start
        
        // Generate random half scores that add up to the total
        int homeH1 = (int)(Math.random() * (homeTotal + 1));
        int homeH2 = homeTotal - homeH1;
        int awayH1 = (int)(Math.random() * (awayTotal + 1));
        int awayH2 = awayTotal - awayH1;
        
        // Set the half scores
        match.setHomeTeamH1Points(homeH1);
        match.setHomeTeamH2Points(homeH2);
        match.setAwayTeamH1Points(awayH1);
        match.setAwayTeamH2Points(awayH2);
        
        // Save the updated match with half scores
        footballMatchRepository.save(match);
    }

    private void updateFootballTeamStatistics() {
        System.out.println("📊 Updating football team statistics...");
        
        List<FootballTeam> teams = footballTeamService.listAllTeams();
        
        for (FootballTeam team : teams) {
            try {
                // Get all completed matches for this team
                List<FootballMatch> teamMatches = footballMatchService.listAllFootballMatches().stream()
                    .filter(match -> (match.getHomeTeam().equals(team) || match.getAwayTeam().equals(team))
                            && match.getEndTime().isBefore(LocalDateTime.now())) // Only completed matches
                    .collect(Collectors.toList());
                
                int wins = 0;
                int losses = 0;
                int draws = 0;
                int leaguePoints = 0;
                int goalsFor = 0;
                int goalsAgainst = 0;
                
                for (FootballMatch match : teamMatches) {
                    boolean isHomeTeam = match.getHomeTeam().equals(team);
                    int teamGoals = isHomeTeam ? match.getHomeTeamPoints() : match.getAwayTeamPoints();
                    int opponentGoals = isHomeTeam ? match.getAwayTeamPoints() : match.getHomeTeamPoints();
                    
                    goalsFor += teamGoals;
                    goalsAgainst += opponentGoals;
                    
                    if (teamGoals > opponentGoals) {
                        wins++;
                        leaguePoints += 3; // 3 points for win
                    } else if (teamGoals < opponentGoals) {
                        losses++;
                        // 0 points for loss
                    } else {
                        draws++;
                        leaguePoints += 1; // 1 point for draw
                    }
                }
                
                // Update team statistics
                team.setTeamMatchesPlayed(wins + losses + draws);
                team.setTeamWins(wins);
                team.setTeamLoses(losses);
                team.setTeamDraws(draws);
                team.setTeamLeaguePoints(leaguePoints);
                team.setGoalsFor(goalsFor);
                team.setGoalsAgainst(goalsAgainst);
                team.setGoalDifference(goalsFor - goalsAgainst);
                
                // Save updated team using repository
                footballTeamRepository.save(team);
                
                System.out.println("⚽ " + team.getTeamName() + ": " + wins + "W-" + losses + "L-" + draws + "D, " + leaguePoints + " pts, " + goalsFor + ":" + goalsAgainst);
                
            } catch (Exception e) {
                System.out.println("⚠️ Error updating statistics for " + team.getTeamName() + ": " + e.getMessage());
            }
        }
        
        System.out.println("✅ Football team statistics updated successfully!");
    }

    private void createBasketballData() {
        System.out.println("🏀 Creating basketball data...");
        
        // Create basketball teams with real logos
        BasketballTeam team1 = basketballTeamService.create("Градежен факултет", readImageFile(getTeamLogoPath(0)));
        BasketballTeam team2 = basketballTeamService.create("Медицински факултет", readImageFile(getTeamLogoPath(1)));
        BasketballTeam team3 = basketballTeamService.create("Технички факултет", readImageFile(getTeamLogoPath(2)));
        BasketballTeam team4 = basketballTeamService.create("Економски факултет", readImageFile(getTeamLogoPath(3)));
        BasketballTeam team5 = basketballTeamService.create("Филолошки факултет", readImageFile(getTeamLogoPath(0)));
        BasketballTeam team6 = basketballTeamService.create("Правен факултет", readImageFile(getTeamLogoPath(1)));
        BasketballTeam team7 = basketballTeamService.create("Педагошки факултет", readImageFile(getTeamLogoPath(2)));
        BasketballTeam team8 = basketballTeamService.create("Факултет за информатички науки", readImageFile(getTeamLogoPath(3)));

        // Create basketball players
        createBasketballPlayers(team1, "Градежен");
        createBasketballPlayers(team2, "Медицински");
        createBasketballPlayers(team3, "Технички");
        createBasketballPlayers(team4, "Економски");
        createBasketballPlayers(team5, "Филолошки");
        createBasketballPlayers(team6, "Правен");
        createBasketballPlayers(team7, "Педагошки");
        createBasketballPlayers(team8, "Информатички");

        // Create basketball matches
        createBasketballMatches();
    }

    private void createBasketballPlayers(BasketballTeam team, String teamPrefix) {
        String[] positions = {"PG", "SG", "SF", "PF", "C", "PG", "SG", "SF", "PF", "C", "SUB"};
        String[] firstNames = {"Александар", "Марко", "Никола", "Стефан", "Димитар", "Петар", "Владимир", "Милош", "Бојан", "Филип", "Андреј"};
        String[] lastNames = {"Петровски", "Јовановски", "Стојановски", "Трајковски", "Ангеловски", "Митевски", "Георгиевски", "Ивановски", "Наумовски", "Спасовски", "Крстевски"};

        for (int i = 0; i < 11; i++) {
            try {
                // Create a random birth date between 1995 and 2005
                LocalDate localDate = LocalDate.of(1995 + (int)(Math.random() * 10), 
                    (int)(Math.random() * 12) + 1, 
                    (int)(Math.random() * 28) + 1);
                Date birthDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                
                BasketballPlayer player = basketballPlayerService.create(
                    readImageFile(getPlayerImagePath(i)), // real player image
                    firstNames[i % firstNames.length],
                    lastNames[i % lastNames.length],
                    birthDate,
                    2000 + i,
                    "Скопје",
                    positions[i],
                    team
                );
                
                // Assign realistic statistics based on position
                assignBasketballPlayerStats(player, positions[i]);
                
            } catch (Exception e) {
                System.out.println("⚠️ Error creating basketball player: " + e.getMessage());
            }
        }
    }

    private void assignBasketballPlayerStats(BasketballPlayer player, String position) {
        // Assign realistic statistics based on position
        int appearances = 8 + (int)(Math.random() * 5); // 8-12 appearances
        
        // Get team's total points from COMPLETED matches only (Past Results)
        List<BasketballMatch> teamMatches = basketballMatchService.listAllBasketballMatches().stream()
            .filter(match -> (match.getHomeTeam().equals(player.getTeam()) || match.getAwayTeam().equals(player.getTeam()))
                    && match.getEndTime().isBefore(LocalDateTime.now())) // Only completed matches
            .collect(Collectors.toList());
        
        int teamTotalPoints = teamMatches.stream()
            .mapToInt(match -> match.getHomeTeam().equals(player.getTeam()) ? 
                match.getHomeTeamPoints() : match.getAwayTeamPoints())
            .sum();
        
        // Calculate realistic stats based on team performance (only from completed matches)
        int maxPointsPerPlayer = Math.max(1, teamTotalPoints / 11); // Distribute points among 11 players
        
        System.out.println("🏀 " + player.getTeam().getTeamName() + " - " + player.getName() + " " + player.getSurname() + 
                          " (Completed matches: " + teamMatches.size() + ", Team total points: " + teamTotalPoints + 
                          ", Max points per player: " + maxPointsPerPlayer + ")");
        
        switch (position) {
            case "PG": // Point Guard
                player.setPoints(Math.min(8 + (int)(Math.random() * 12), maxPointsPerPlayer * 2)); // 8-19 points, but constrained
                player.setAssists(4 + (int)(Math.random() * 8)); // 4-11 assists
                player.setRebounds(2 + (int)(Math.random() * 6)); // 2-7 rebounds
                break;
            case "SG": // Shooting Guard
                player.setPoints(Math.min(10 + (int)(Math.random() * 15), maxPointsPerPlayer * 3)); // 10-24 points, but constrained
                player.setAssists(2 + (int)(Math.random() * 6)); // 2-7 assists
                player.setRebounds(3 + (int)(Math.random() * 7)); // 3-9 rebounds
                break;
            case "SF": // Small Forward
                player.setPoints(Math.min(12 + (int)(Math.random() * 18), maxPointsPerPlayer * 4)); // 12-29 points, but constrained
                player.setAssists(2 + (int)(Math.random() * 5)); // 2-6 assists
                player.setRebounds(4 + (int)(Math.random() * 8)); // 4-11 rebounds
                break;
            case "PF": // Power Forward
                player.setPoints(Math.min(8 + (int)(Math.random() * 12), maxPointsPerPlayer * 2)); // 8-19 points, but constrained
                player.setAssists(1 + (int)(Math.random() * 4)); // 1-4 assists
                player.setRebounds(6 + (int)(Math.random() * 10)); // 6-15 rebounds
                break;
            case "C": // Center
                player.setPoints(Math.min(6 + (int)(Math.random() * 10), maxPointsPerPlayer * 2)); // 6-15 points, but constrained
                player.setAssists(1 + (int)(Math.random() * 3)); // 1-3 assists
                player.setRebounds(8 + (int)(Math.random() * 12)); // 8-19 rebounds
                break;
            case "SUB":
                player.setPoints(Math.min(2 + (int)(Math.random() * 6), maxPointsPerPlayer)); // 2-7 points, but constrained
                player.setAssists(0 + (int)(Math.random() * 3)); // 0-2 assists
                player.setRebounds(1 + (int)(Math.random() * 4)); // 1-4 rebounds
                break;
        }
        
        player.setAppearances(appearances);
        
        // Save the updated player with statistics
        basketballPlayerRepository.save(player);
    }

    private void createBasketballMatches() {
        List<BasketballTeam> teams = basketballTeamService.listAllTeams();
        
        if (teams.size() >= 8) {
            try {
                // Week 1 matches
                createBasketballMatchWithQuarters(teams.get(0), teams.get(1), 85, 78, LocalDateTime.now().minusDays(6));
                createBasketballMatchWithQuarters(teams.get(2), teams.get(3), 92, 88, LocalDateTime.now().minusDays(6));
                createBasketballMatchWithQuarters(teams.get(4), teams.get(5), 76, 82, LocalDateTime.now().minusDays(6));
                createBasketballMatchWithQuarters(teams.get(6), teams.get(7), 95, 89, LocalDateTime.now().minusDays(6));
                
                // Week 2 matches
                createBasketballMatchWithQuarters(teams.get(1), teams.get(2), 88, 91, LocalDateTime.now().minusDays(4));
                createBasketballMatchWithQuarters(teams.get(3), teams.get(4), 84, 79, LocalDateTime.now().minusDays(4));
                createBasketballMatchWithQuarters(teams.get(5), teams.get(6), 77, 85, LocalDateTime.now().minusDays(4));
                createBasketballMatchWithQuarters(teams.get(7), teams.get(0), 93, 87, LocalDateTime.now().minusDays(4));
                
                // Update team statistics after creating all matches
                updateBasketballTeamStatistics();
                
                System.out.println("✅ Created 8 basketball matches and updated team statistics");
            } catch (Exception e) {
                System.out.println("⚠️ Error creating basketball matches: " + e.getMessage());
            }
        }
    }
    
    private void createBasketballMatchWithQuarters(BasketballTeam homeTeam, BasketballTeam awayTeam, 
                                                 int homeTotal, int awayTotal, LocalDateTime startTime) {
        // Create the match first
        BasketballMatch match = basketballMatchService.createAndAddToFixtures(homeTeam, awayTeam, homeTotal, awayTotal, startTime);
        
        // Set endTime to be in the past so it's counted as completed
        match.setEndTime(startTime.plusHours(2)); // Match ends 2 hours after start
        
        // Generate random quarter scores that add up to the total
        int homeQ1 = (int)(Math.random() * (homeTotal / 2 + 1));
        int homeQ2 = (int)(Math.random() * ((homeTotal - homeQ1) / 2 + 1));
        int homeQ3 = (int)(Math.random() * ((homeTotal - homeQ1 - homeQ2) / 2 + 1));
        int homeQ4 = homeTotal - homeQ1 - homeQ2 - homeQ3;
        
        int awayQ1 = (int)(Math.random() * (awayTotal / 2 + 1));
        int awayQ2 = (int)(Math.random() * ((awayTotal - awayQ1) / 2 + 1));
        int awayQ3 = (int)(Math.random() * ((awayTotal - awayQ1 - awayQ2) / 2 + 1));
        int awayQ4 = awayTotal - awayQ1 - awayQ2 - awayQ3;
        
        // Set the quarter scores
        match.setHomeTeamQ1Points(homeQ1);
        match.setHomeTeamQ2Points(homeQ2);
        match.setHomeTeamQ3Points(homeQ3);
        match.setHomeTeamQ4Points(homeQ4);
        match.setAwayTeamQ1Points(awayQ1);
        match.setAwayTeamQ2Points(awayQ2);
        match.setAwayTeamQ3Points(awayQ3);
        match.setAwayTeamQ4Points(awayQ4);
        
        // Save the updated match with quarter scores
        basketballMatchRepository.save(match);
    }

    private void updateBasketballTeamStatistics() {
        System.out.println("📊 Updating basketball team statistics...");
        
        List<BasketballTeam> teams = basketballTeamService.listAllTeams();
        
        for (BasketballTeam team : teams) {
            try {
                // Get all completed matches for this team
                List<BasketballMatch> teamMatches = basketballMatchService.listAllBasketballMatches().stream()
                    .filter(match -> (match.getHomeTeam().equals(team) || match.getAwayTeam().equals(team))
                            && match.getEndTime().isBefore(LocalDateTime.now())) // Only completed matches
                    .collect(Collectors.toList());
                
                int wins = 0;
                int losses = 0;
                int leaguePoints = 0;
                
                for (BasketballMatch match : teamMatches) {
                    boolean isHomeTeam = match.getHomeTeam().equals(team);
                    int teamPoints = isHomeTeam ? match.getHomeTeamPoints() : match.getAwayTeamPoints();
                    int opponentPoints = isHomeTeam ? match.getAwayTeamPoints() : match.getHomeTeamPoints();
                    
                    if (teamPoints > opponentPoints) {
                        wins++;
                        leaguePoints += 3; // 3 points for win
                    } else if (teamPoints < opponentPoints) {
                        losses++;
                        // 0 points for loss
                    }
                    // Basketball doesn't have draws
                }
                
                // Update team statistics
                team.setTeamMatchesPlayed(wins + losses);
                team.setTeamWins(wins);
                team.setTeamLoses(losses);
                team.setTeamLeaguePoints(leaguePoints);
                
                // Save updated team using repository
                basketballTeamRepository.save(team);
                
                System.out.println("🏀 " + team.getTeamName() + ": " + wins + "W-" + losses + "L, " + leaguePoints + " pts");
                
            } catch (Exception e) {
                System.out.println("⚠️ Error updating statistics for " + team.getTeamName() + ": " + e.getMessage());
            }
        }
        
        System.out.println("✅ Basketball team statistics updated successfully!");
    }

    private void createVolleyballData() {
        System.out.println("🏐 Creating volleyball data...");
        
        // Create volleyball teams with real logos
        VolleyballTeam team1 = volleyballTeamService.create("Градежен факултет", readImageFile(getTeamLogoPath(0)));
        VolleyballTeam team2 = volleyballTeamService.create("Медицински факултет", readImageFile(getTeamLogoPath(1)));
        VolleyballTeam team3 = volleyballTeamService.create("Технички факултет", readImageFile(getTeamLogoPath(2)));
        VolleyballTeam team4 = volleyballTeamService.create("Економски факултет", readImageFile(getTeamLogoPath(3)));
        VolleyballTeam team5 = volleyballTeamService.create("Филолошки факултет", readImageFile(getTeamLogoPath(0)));
        VolleyballTeam team6 = volleyballTeamService.create("Правен факултет", readImageFile(getTeamLogoPath(1)));
        VolleyballTeam team7 = volleyballTeamService.create("Педагошки факултет", readImageFile(getTeamLogoPath(2)));
        VolleyballTeam team8 = volleyballTeamService.create("Факултет за информатички науки", readImageFile(getTeamLogoPath(3)));

        // Create volleyball players
        createVolleyballPlayers(team1, "Градежен");
        createVolleyballPlayers(team2, "Медицински");
        createVolleyballPlayers(team3, "Технички");
        createVolleyballPlayers(team4, "Економски");
        createVolleyballPlayers(team5, "Филолошки");
        createVolleyballPlayers(team6, "Правен");
        createVolleyballPlayers(team7, "Педагошки");
        createVolleyballPlayers(team8, "Информатички");

        // Create volleyball matches
        createVolleyballMatches();
    }

    private void createVolleyballPlayers(VolleyballTeam team, String teamPrefix) {
        String[] positions = {"S", "OH", "OH", "MB", "MB", "OPP", "L", "DS", "S", "OH", "MB"};
        String[] firstNames = {"Александар", "Марко", "Никола", "Стефан", "Димитар", "Петар", "Владимир", "Милош", "Бојан", "Филип", "Андреј"};
        String[] lastNames = {"Петровски", "Јовановски", "Стојановски", "Трајковски", "Ангеловски", "Митевски", "Георгиевски", "Ивановски", "Наумовски", "Спасовски", "Крстевски"};

        for (int i = 0; i < 11; i++) {
            try {
                // Create a random birth date between 1995 and 2005
                LocalDate localDate = LocalDate.of(1995 + (int)(Math.random() * 10), 
                    (int)(Math.random() * 12) + 1, 
                    (int)(Math.random() * 28) + 1);
                Date birthDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                
                VolleyballPlayer player = volleyballPlayerService.create(
                    readImageFile(getPlayerImagePath(i)), // real player image
                    firstNames[i % firstNames.length],
                    lastNames[i % lastNames.length],
                    birthDate,
                    3000 + i,
                    "Скопје",
                    positions[i],
                    team
                );
                
                // Assign realistic statistics based on position
                assignVolleyballPlayerStats(player, positions[i]);
                
            } catch (Exception e) {
                System.out.println("⚠️ Error creating volleyball player: " + e.getMessage());
            }
        }
    }
    
    private void assignVolleyballPlayerStats(VolleyballPlayer player, String position) {
        // Assign realistic statistics based on position
        int appearances = 8 + (int)(Math.random() * 5); // 8-12 appearances
        
        // Get team's total points from COMPLETED matches only (Past Results)
        List<VolleyballMatch> teamMatches = volleyballMatchService.listAllVolleyballMatches().stream()
            .filter(match -> (match.getHomeTeam().equals(player.getTeam()) || match.getAwayTeam().equals(player.getTeam()))
                    && match.getEndTime().isBefore(LocalDateTime.now())) // Only completed matches
            .collect(Collectors.toList());
        
        int teamTotalPoints = teamMatches.stream()
            .mapToInt(match -> {
                if (match.getHomeTeam().equals(player.getTeam())) {
                    return match.getHomeTeamSet1Points() + match.getHomeTeamSet2Points() + 
                           match.getHomeTeamSet3Points() + match.getHomeTeamSet4Points() + 
                           match.getHomeTeamSet5Points();
                } else {
                    return match.getAwayTeamSet1Points() + match.getAwayTeamSet2Points() + 
                           match.getAwayTeamSet3Points() + match.getAwayTeamSet4Points() + 
                           match.getAwayTeamSet5Points();
                }
            })
            .sum();
        
        // Calculate realistic stats based on team performance (only from completed matches)
        int maxPointsPerPlayer = Math.max(1, teamTotalPoints / 11); // Distribute points among 11 players
        
        System.out.println("🏐 " + player.getTeam().getTeamName() + " - " + player.getName() + " " + player.getSurname() + 
                          " (Completed matches: " + teamMatches.size() + ", Team total points: " + teamTotalPoints + 
                          ", Max points per player: " + maxPointsPerPlayer + ")");
        
        switch (position) {
            case "S": // Setter
                player.setScoredPoints(Math.min(2 + (int)(Math.random() * 4), maxPointsPerPlayer)); // 2-5 points, but constrained
                player.setAssists(15 + (int)(Math.random() * 20)); // 15-34 assists
                player.setServings(8 + (int)(Math.random() * 12)); // 8-19 servings
                player.setBlocks(1 + (int)(Math.random() * 3)); // 1-3 blocks
                break;
            case "OH": // Outside Hitter
                player.setScoredPoints(Math.min(12 + (int)(Math.random() * 18), maxPointsPerPlayer * 3)); // 12-29 points, but constrained
                player.setAssists(1 + (int)(Math.random() * 3)); // 1-3 assists
                player.setServings(10 + (int)(Math.random() * 15)); // 10-24 servings
                player.setBlocks(2 + (int)(Math.random() * 5)); // 2-6 blocks
                break;
            case "MB": // Middle Blocker
                player.setScoredPoints(Math.min(8 + (int)(Math.random() * 12), maxPointsPerPlayer * 2)); // 8-19 points, but constrained
                player.setAssists(0 + (int)(Math.random() * 2)); // 0-1 assists
                player.setServings(6 + (int)(Math.random() * 10)); // 6-15 servings
                player.setBlocks(5 + (int)(Math.random() * 10)); // 5-14 blocks
                break;
            case "OPP": // Opposite
                player.setScoredPoints(Math.min(10 + (int)(Math.random() * 15), maxPointsPerPlayer * 3)); // 10-24 points, but constrained
                player.setAssists(1 + (int)(Math.random() * 3)); // 1-3 assists
                player.setServings(8 + (int)(Math.random() * 12)); // 8-19 servings
                player.setBlocks(2 + (int)(Math.random() * 4)); // 2-5 blocks
                break;
            case "L": // Libero
                player.setScoredPoints(Math.min(1 + (int)(Math.random() * 2), maxPointsPerPlayer)); // 1-2 points, but constrained
                player.setAssists(2 + (int)(Math.random() * 4)); // 2-5 assists
                player.setServings(5 + (int)(Math.random() * 8)); // 5-12 servings
                player.setBlocks(0); // Liberos rarely block
                break;
            case "DS": // Defensive Specialist
                player.setScoredPoints(Math.min(2 + (int)(Math.random() * 3), maxPointsPerPlayer)); // 2-4 points, but constrained
                player.setAssists(1 + (int)(Math.random() * 2)); // 1-2 assists
                player.setServings(6 + (int)(Math.random() * 8)); // 6-13 servings
                player.setBlocks(1 + (int)(Math.random() * 2)); // 1-2 blocks
                break;
        }
        
        player.setAppearances(appearances);
        
        // Save the updated player with statistics
        volleyballPlayerRepository.save(player);
    }

    private void createVolleyballMatches() {
        List<VolleyballTeam> teams = volleyballTeamService.listAllTeams();
        
        if (teams.size() >= 8) {
            try {
                // Week 1 matches
                createVolleyballMatchWithSets(teams.get(0), teams.get(1), 3, 1, LocalDateTime.now().minusDays(8));
                createVolleyballMatchWithSets(teams.get(2), teams.get(3), 3, 2, LocalDateTime.now().minusDays(8));
                createVolleyballMatchWithSets(teams.get(4), teams.get(5), 3, 0, LocalDateTime.now().minusDays(8));
                createVolleyballMatchWithSets(teams.get(6), teams.get(7), 2, 3, LocalDateTime.now().minusDays(8));
                
                // Week 2 matches
                createVolleyballMatchWithSets(teams.get(1), teams.get(2), 3, 1, LocalDateTime.now().minusDays(6));
                createVolleyballMatchWithSets(teams.get(3), teams.get(4), 1, 3, LocalDateTime.now().minusDays(6));
                createVolleyballMatchWithSets(teams.get(5), teams.get(6), 3, 2, LocalDateTime.now().minusDays(6));
                createVolleyballMatchWithSets(teams.get(7), teams.get(0), 0, 3, LocalDateTime.now().minusDays(6));
                
                // Update team statistics after creating all matches
                updateVolleyballTeamStatistics();
                
                System.out.println("✅ Created 8 volleyball matches and updated team statistics");
            } catch (Exception e) {
                System.out.println("⚠️ Error creating volleyball matches: " + e.getMessage());
            }
        }
    }
    
    private void createVolleyballMatchWithSets(VolleyballTeam homeTeam, VolleyballTeam awayTeam, 
                                             int homeSetsWon, int awaySetsWon, LocalDateTime startTime) {
        // Create the match first
        VolleyballMatch match = volleyballMatchService.createAndAddToFixtures(homeTeam, awayTeam, homeSetsWon, awaySetsWon, startTime);
        
        // Set endTime to be in the past so it's counted as completed
        match.setEndTime(startTime.plusHours(2)); // Match ends 2 hours after start
        
        // Determine how many sets were played (minimum 3, maximum 5)
        int totalSetsPlayed = Math.max(3, homeSetsWon + awaySetsWon);
        
        // Generate random set scores for each set played
        for (int set = 1; set <= totalSetsPlayed; set++) {
            int maxPoints = (set == 5) ? 15 : 25; // Set 5 has max 15 points, others have max 25
            
            // Generate realistic volleyball scores (usually close, with one team winning by at least 2)
            int homePoints, awayPoints;
            do {
                homePoints = (int)(Math.random() * (maxPoints - 1)) + 1; // 1 to maxPoints-1
                awayPoints = (int)(Math.random() * (maxPoints - 1)) + 1; // 1 to maxPoints-1
            } while (Math.abs(homePoints - awayPoints) < 2); // Ensure at least 2 point difference
            
            // Ensure one team reaches the winning score
            if (homePoints > awayPoints) {
                homePoints = Math.min(homePoints, maxPoints);
                awayPoints = Math.min(awayPoints, maxPoints - 2);
            } else {
                awayPoints = Math.min(awayPoints, maxPoints);
                homePoints = Math.min(homePoints, maxPoints - 2);
            }
            
            // Set the set scores
            switch (set) {
                case 1:
                    match.setHomeTeamSet1Points(homePoints);
                    match.setAwayTeamSet1Points(awayPoints);
                    break;
                case 2:
                    match.setHomeTeamSet2Points(homePoints);
                    match.setAwayTeamSet2Points(awayPoints);
                    break;
                case 3:
                    match.setHomeTeamSet3Points(homePoints);
                    match.setAwayTeamSet3Points(awayPoints);
                    break;
                case 4:
                    match.setHomeTeamSet4Points(homePoints);
                    match.setAwayTeamSet4Points(awayPoints);
                    break;
                case 5:
                    match.setHomeTeamSet5Points(homePoints);
                    match.setAwayTeamSet5Points(awayPoints);
                    break;
            }
        }
        
        // Save the updated match with set scores
        volleyballMatchRepository.save(match);
    }

    private void updateVolleyballTeamStatistics() {
        System.out.println("📊 Updating volleyball team statistics...");
        
        List<VolleyballTeam> teams = volleyballTeamService.listAllTeams();
        
        for (VolleyballTeam team : teams) {
            try {
                // Get all completed matches for this team
                List<VolleyballMatch> teamMatches = volleyballMatchService.listAllVolleyballMatches().stream()
                    .filter(match -> (match.getHomeTeam().equals(team) || match.getAwayTeam().equals(team))
                            && match.getEndTime().isBefore(LocalDateTime.now())) // Only completed matches
                    .collect(Collectors.toList());
                
                int wins = 0;
                int losses = 0;
                int leaguePoints = 0;
                
                for (VolleyballMatch match : teamMatches) {
                    boolean isHomeTeam = match.getHomeTeam().equals(team);
                    int teamSetsWon = isHomeTeam ? match.getHomeTeamPoints() : match.getAwayTeamPoints();
                    int opponentSetsWon = isHomeTeam ? match.getAwayTeamPoints() : match.getHomeTeamPoints();
                    
                    if (teamSetsWon > opponentSetsWon) {
                        wins++;
                        leaguePoints += 3; // 3 points for win
                    } else if (teamSetsWon < opponentSetsWon) {
                        losses++;
                        // 0 points for loss
                    }
                    // Volleyball doesn't have draws
                }
                
                // Update team statistics
                team.setTeamMatchesPlayed(wins + losses);
                team.setTeamWins(wins);
                team.setTeamLoses(losses);
                team.setTeamLeaguePoints(leaguePoints);
                
                // Save updated team using repository
                volleyballTeamRepository.save(team);
                
                System.out.println("🏐 " + team.getTeamName() + ": " + wins + "W-" + losses + "L, " + leaguePoints + " pts");
                
            } catch (Exception e) {
                System.out.println("⚠️ Error updating statistics for " + team.getTeamName() + ": " + e.getMessage());
            }
        }
        
        System.out.println("✅ Volleyball team statistics updated successfully!");
    }

    private void createNews() {
        System.out.println("📰 Creating news articles...");
        
        try {
            newsService.create("Универзитетската лига започнува со 8 тимови!", 
                "FOOTBALL", 
                "Голема радост на УКИМ како започна новата сезона на Универзитетската лига. Оваа сезона се натпреваруваат 8 факултети во фудбал, кошарка и одбојка: Градежен, Медицински, Технички, Економски, Филолошки, Правен, Педагошки и Факултет за информатички науки.");
            
            newsService.create("Градежен факултет победник во фудбал", 
                "FOOTBALL", 
                "Во драматичен натпревар, Градежен факултет го победи Медицински факултет со резултат 2:1. Головите ги постигнаа Александар Петровски и Марко Јовановски.");
            
            newsService.create("Педагошки факултет дебитира со победа", 
                "FOOTBALL", 
                "Педагошки факултет го направи своето деби во лигата со убедлива победа над Факултет за информатички науки со резултат 2:0. Ова е првата сезона кога овие два факултети се натпреваруваат во лигата.");
            
            newsService.create("Кошаркарски натпревар со висок резултат", 
                "BASKETBALL", 
                "Технички факултет го победи Економски факултет во кошарка со резултат 92:88. Натпреварот беше полн со акција и несигурен до последниот момент.");
            
            newsService.create("Педагошки факултет доминира во кошарка", 
                "BASKETBALL", 
                "Педагошки факултет покажа одлична форма во кошарка, победувајќи го Факултет за информатички науки со резултат 95:89. Играчите покажаа одлична тимска работа.");
            
            newsService.create("Одбојкарски натпревар во пет сета", 
                "VOLLEYBALL", 
                "Во најдолгиот натпревар до сега, Градежен факултет го победи Медицински факултет во одбојка со резултат 3:2. Натпреварот траеше преку 2 часа.");
            
            newsService.create("Новите тимови се прилагодуваат", 
                "VOLLEYBALL", 
                "Педагошки факултет и Факултет за информатички науки се прилагодуваат на новите услови во одбојкарската лига. Иако се нови, покажуваат голем потенцијал за иднината.");
        } catch (Exception e) {
            System.out.println("⚠️ Error creating news: " + e.getMessage());
        }
    }

    private void createFantasyTeams() {
        System.out.println("🎮 Creating fantasy teams...");
        
        try {
            // Get some users
            var user1 = userService.findByName("user1");
            var user2 = userService.findByName("user2");
            
            if (user1.isPresent()) {
                fantasyService.createTeam(user1.get(), FantasySport.FOOTBALL, "Мојот фудбалски тим");
                fantasyService.createTeam(user1.get(), FantasySport.BASKETBALL, "Кошаркарски шампиони");
                fantasyService.createTeam(user1.get(), FantasySport.VOLLEYBALL, "Одбојкарски мајстори");
            }
            
            if (user2.isPresent()) {
                fantasyService.createTeam(user2.get(), FantasySport.FOOTBALL, "Фудбалски витези");
                fantasyService.createTeam(user2.get(), FantasySport.BASKETBALL, "Кошаркарски титани");
            }
        } catch (Exception e) {
            System.out.println("⚠️ Error creating fantasy teams: " + e.getMessage());
        }
    }

    private void createShopData() {
        System.out.println("🛒 Creating shop data...");
        
        try {
            createFootballProducts();
            createBasketballProducts();
            createVolleyballProducts();
            
            System.out.println("✅ Shop data created successfully!");
        } catch (Exception e) {
            System.out.println("⚠️ Error creating shop data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createFootballProducts() {
        System.out.println("⚽ Creating football merchandise...");
        
        List<FootballTeam> teams = footballTeamService.listAllTeams();
        
        for (FootballTeam team : teams) {
            try {
                // Create different types of merchandise for each team
                FootballProduct jersey = new FootballProduct(
                    team.getTeamName() + " - Дрес",
                    "Официјален дрес на " + team.getTeamName() + " за сезона 2024/25",
                    25.99,
                    getJerseyImagePath(0),
                    team
                );
                footballProductRepository.save(jersey);
                
                FootballProduct scarf = new FootballProduct(
                    team.getTeamName() + " - Шал",
                    "Официјален шал на " + team.getTeamName() + " со лого",
                    12.50,
                    getJerseyImagePath(1),
                    team
                );
                footballProductRepository.save(scarf);
                
                FootballProduct cap = new FootballProduct(
                    team.getTeamName() + " - Капа",
                    "Спортска капа на " + team.getTeamName(),
                    8.99,
                    getHatImagePath(0),
                    team
                );
                footballProductRepository.save(cap);
                
                FootballProduct mug = new FootballProduct(
                    team.getTeamName() + " - Шолја",
                    "Керамичка шолја со лого на " + team.getTeamName(),
                    6.99,
                    getJerseyImagePath(0),
                    team
                );
                footballProductRepository.save(mug);
                
            } catch (Exception e) {
                System.out.println("⚠️ Error creating football products for " + team.getTeamName() + ": " + e.getMessage());
            }
        }
        
        System.out.println("✅ Created football merchandise for " + teams.size() + " teams");
    }

    private void createBasketballProducts() {
        System.out.println("🏀 Creating basketball merchandise...");
        
        List<BasketballTeam> teams = basketballTeamService.listAllTeams();
        
        for (BasketballTeam team : teams) {
            try {
                // Create different types of merchandise for each team
                BasketballProduct jersey = new BasketballProduct(
                    team.getTeamName() + " - Дрес",
                    "Официјален кошаркарски дрес на " + team.getTeamName() + " за сезона 2024/25",
                    28.99,
                    getJerseyImagePath(0),
                    team
                );
                basketballProductRepository.save(jersey);
                
                BasketballProduct shorts = new BasketballProduct(
                    team.getTeamName() + " - Шорцеви",
                    "Официјални кошаркарски шорцеви на " + team.getTeamName(),
                    22.50,
                    getJerseyImagePath(1),
                    team
                );
                basketballProductRepository.save(shorts);
                
                BasketballProduct ball = new BasketballProduct(
                    team.getTeamName() + " - Топка",
                    "Официјална кошаркарска топка на " + team.getTeamName(),
                    35.99,
                    getJerseyImagePath(0),
                    team
                );
                basketballProductRepository.save(ball);
                
                BasketballProduct wristband = new BasketballProduct(
                    team.getTeamName() + " - Нараквица",
                    "Спортска нараквица на " + team.getTeamName(),
                    4.99,
                    getHatImagePath(0),
                    team
                );
                basketballProductRepository.save(wristband);
                
            } catch (Exception e) {
                System.out.println("⚠️ Error creating basketball products for " + team.getTeamName() + ": " + e.getMessage());
            }
        }
        
        System.out.println("✅ Created basketball merchandise for " + teams.size() + " teams");
    }

    private void createVolleyballProducts() {
        System.out.println("🏐 Creating volleyball merchandise...");
        
        List<VolleyballTeam> teams = volleyballTeamService.listAllTeams();
        
        for (VolleyballTeam team : teams) {
            try {
                // Create different types of merchandise for each team
                VolleyballProduct jersey = new VolleyballProduct(
                    team.getTeamName() + " - Дрес",
                    "Официјален одбојкарски дрес на " + team.getTeamName() + " за сезона 2024/25",
                    26.99,
                    getJerseyImagePath(0),
                    team
                );
                volleyballProductRepository.save(jersey);
                
                VolleyballProduct shorts = new VolleyballProduct(
                    team.getTeamName() + " - Шорцеви",
                    "Официјални одбојкарски шорцеви на " + team.getTeamName(),
                    18.50,
                    getJerseyImagePath(1),
                    team
                );
                volleyballProductRepository.save(shorts);
                
                VolleyballProduct ball = new VolleyballProduct(
                    team.getTeamName() + " - Топка",
                    "Официјална одбојкарска топка на " + team.getTeamName(),
                    32.99,
                    getJerseyImagePath(0),
                    team
                );
                volleyballProductRepository.save(ball);
                
                VolleyballProduct kneePads = new VolleyballProduct(
                    team.getTeamName() + " - Коленки",
                    "Заштитни коленки за одбојка на " + team.getTeamName(),
                    15.99,
                    getHatImagePath(0),
                    team
                );
                volleyballProductRepository.save(kneePads);
                
            } catch (Exception e) {
                System.out.println("⚠️ Error creating volleyball products for " + team.getTeamName() + ": " + e.getMessage());
            }
        }
        
        System.out.println("✅ Created volleyball merchandise for " + teams.size() + " teams");
    }
}
