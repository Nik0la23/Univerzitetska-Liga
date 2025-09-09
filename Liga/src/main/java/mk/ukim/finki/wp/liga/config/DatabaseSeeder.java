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
                         VolleyballProductRepository volleyballProductRepository) {
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

    private void createUsers() {
        System.out.println("👥 Creating users...");
        
        try {
            User admin = userService.register("admin", "admin@liga.com", "admin123");
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
        
        // Create football teams
        FootballTeam team1 = footballTeamService.create("Пумбарски факултет", null);
        FootballTeam team2 = footballTeamService.create("Медицински факултет", null);
        FootballTeam team3 = footballTeamService.create("Технички факултет", null);
        FootballTeam team4 = footballTeamService.create("Економски факултет", null);
        FootballTeam team5 = footballTeamService.create("Филолошки факултет", null);
        FootballTeam team6 = footballTeamService.create("Правен факултет", null);
        FootballTeam team7 = footballTeamService.create("Педагошки факултет", null);
        FootballTeam team8 = footballTeamService.create("Факултет за информатички науки", null);

        // Create football players
        createFootballPlayers(team1, "Пумбарски");
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
                
                footballPlayerService.create(
                    null, // image
                    firstNames[i % firstNames.length],
                    lastNames[i % lastNames.length],
                    birthDate,
                    1000 + i,
                    "Скопје",
                    positions[i],
                    team
                );
            } catch (Exception e) {
                System.out.println("⚠️ Error creating football player: " + e.getMessage());
            }
        }
    }

    private void createFootballMatches() {
        List<FootballTeam> teams = footballTeamService.listAllTeams();
        
        // Create some sample matches
        if (teams.size() >= 8) {
            try {
                // Week 1 matches
                footballMatchService.createAndAddToFixtures(
                    teams.get(0), teams.get(1), 
                    2, 1, 
                    LocalDateTime.now().minusDays(7)
                );
                
                footballMatchService.createAndAddToFixtures(
                    teams.get(2), teams.get(3), 
                    0, 3, 
                    LocalDateTime.now().minusDays(7)
                );
                
                footballMatchService.createAndAddToFixtures(
                    teams.get(4), teams.get(5), 
                    1, 1, 
                    LocalDateTime.now().minusDays(7)
                );
                
                footballMatchService.createAndAddToFixtures(
                    teams.get(6), teams.get(7), 
                    2, 0, 
                    LocalDateTime.now().minusDays(7)
                );
                
                // Week 2 matches
                footballMatchService.createAndAddToFixtures(
                    teams.get(1), teams.get(2), 
                    1, 2, 
                    LocalDateTime.now().minusDays(5)
                );
                
                footballMatchService.createAndAddToFixtures(
                    teams.get(3), teams.get(4), 
                    3, 1, 
                    LocalDateTime.now().minusDays(5)
                );
                
                footballMatchService.createAndAddToFixtures(
                    teams.get(5), teams.get(6), 
                    0, 2, 
                    LocalDateTime.now().minusDays(5)
                );
                
                footballMatchService.createAndAddToFixtures(
                    teams.get(7), teams.get(0), 
                    1, 1, 
                    LocalDateTime.now().minusDays(5)
                );
                
                // Week 3 matches
                footballMatchService.createAndAddToFixtures(
                    teams.get(0), teams.get(3), 
                    2, 2, 
                    LocalDateTime.now().minusDays(3)
                );
                
                footballMatchService.createAndAddToFixtures(
                    teams.get(1), teams.get(5), 
                    1, 0, 
                    LocalDateTime.now().minusDays(3)
                );
                
                footballMatchService.createAndAddToFixtures(
                    teams.get(2), teams.get(7), 
                    3, 1, 
                    LocalDateTime.now().minusDays(3)
                );
                
                footballMatchService.createAndAddToFixtures(
                    teams.get(4), teams.get(6), 
                    0, 1, 
                    LocalDateTime.now().minusDays(3)
                );
                
                System.out.println("✅ Created 12 football matches");
            } catch (Exception e) {
                System.out.println("⚠️ Error creating football matches: " + e.getMessage());
            }
        }
    }

    private void createBasketballData() {
        System.out.println("🏀 Creating basketball data...");
        
        // Create basketball teams
        BasketballTeam team1 = basketballTeamService.create("Пумбарски факултет", null);
        BasketballTeam team2 = basketballTeamService.create("Медицински факултет", null);
        BasketballTeam team3 = basketballTeamService.create("Технички факултет", null);
        BasketballTeam team4 = basketballTeamService.create("Економски факултет", null);
        BasketballTeam team5 = basketballTeamService.create("Филолошки факултет", null);
        BasketballTeam team6 = basketballTeamService.create("Правен факултет", null);
        BasketballTeam team7 = basketballTeamService.create("Педагошки факултет", null);
        BasketballTeam team8 = basketballTeamService.create("Факултет за информатички науки", null);

        // Create basketball players
        createBasketballPlayers(team1, "Пумбарски");
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
                
                basketballPlayerService.create(
                    null, // image
                    firstNames[i % firstNames.length],
                    lastNames[i % lastNames.length],
                    birthDate,
                    2000 + i,
                    "Скопје",
                    positions[i],
                    team
                );
            } catch (Exception e) {
                System.out.println("⚠️ Error creating basketball player: " + e.getMessage());
            }
        }
    }

    private void createBasketballMatches() {
        List<BasketballTeam> teams = basketballTeamService.listAllTeams();
        
        if (teams.size() >= 8) {
            try {
                // Week 1 matches
                basketballMatchService.createAndAddToFixtures(
                    teams.get(0), teams.get(1), 
                    85, 78, 
                    LocalDateTime.now().minusDays(6)
                );
                
                basketballMatchService.createAndAddToFixtures(
                    teams.get(2), teams.get(3), 
                    92, 88, 
                    LocalDateTime.now().minusDays(6)
                );
                
                basketballMatchService.createAndAddToFixtures(
                    teams.get(4), teams.get(5), 
                    76, 82, 
                    LocalDateTime.now().minusDays(6)
                );
                
                basketballMatchService.createAndAddToFixtures(
                    teams.get(6), teams.get(7), 
                    95, 89, 
                    LocalDateTime.now().minusDays(6)
                );
                
                // Week 2 matches
                basketballMatchService.createAndAddToFixtures(
                    teams.get(1), teams.get(2), 
                    88, 91, 
                    LocalDateTime.now().minusDays(4)
                );
                
                basketballMatchService.createAndAddToFixtures(
                    teams.get(3), teams.get(4), 
                    84, 79, 
                    LocalDateTime.now().minusDays(4)
                );
                
                basketballMatchService.createAndAddToFixtures(
                    teams.get(5), teams.get(6), 
                    77, 85, 
                    LocalDateTime.now().minusDays(4)
                );
                
                basketballMatchService.createAndAddToFixtures(
                    teams.get(7), teams.get(0), 
                    93, 87, 
                    LocalDateTime.now().minusDays(4)
                );
                
                System.out.println("✅ Created 8 basketball matches");
            } catch (Exception e) {
                System.out.println("⚠️ Error creating basketball matches: " + e.getMessage());
            }
        }
    }

    private void createVolleyballData() {
        System.out.println("🏐 Creating volleyball data...");
        
        // Create volleyball teams
        VolleyballTeam team1 = volleyballTeamService.create("Пумбарски факултет", null);
        VolleyballTeam team2 = volleyballTeamService.create("Медицински факултет", null);
        VolleyballTeam team3 = volleyballTeamService.create("Технички факултет", null);
        VolleyballTeam team4 = volleyballTeamService.create("Економски факултет", null);
        VolleyballTeam team5 = volleyballTeamService.create("Филолошки факултет", null);
        VolleyballTeam team6 = volleyballTeamService.create("Правен факултет", null);
        VolleyballTeam team7 = volleyballTeamService.create("Педагошки факултет", null);
        VolleyballTeam team8 = volleyballTeamService.create("Факултет за информатички науки", null);

        // Create volleyball players
        createVolleyballPlayers(team1, "Пумбарски");
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
        String[] positions = {"Setter", "OH", "OH", "MB", "MB", "Opposite", "Libero", "Setter", "OH", "MB", "Opposite"};
        String[] firstNames = {"Александар", "Марко", "Никола", "Стефан", "Димитар", "Петар", "Владимир", "Милош", "Бојан", "Филип", "Андреј"};
        String[] lastNames = {"Петровски", "Јовановски", "Стојановски", "Трајковски", "Ангеловски", "Митевски", "Георгиевски", "Ивановски", "Наумовски", "Спасовски", "Крстевски"};

        for (int i = 0; i < 11; i++) {
            try {
                // Create a random birth date between 1995 and 2005
                LocalDate localDate = LocalDate.of(1995 + (int)(Math.random() * 10), 
                    (int)(Math.random() * 12) + 1, 
                    (int)(Math.random() * 28) + 1);
                Date birthDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                
                volleyballPlayerService.create(
                    null, // image
                    firstNames[i % firstNames.length],
                    lastNames[i % lastNames.length],
                    birthDate,
                    3000 + i,
                    "Скопје",
                    positions[i],
                    team
                );
            } catch (Exception e) {
                System.out.println("⚠️ Error creating volleyball player: " + e.getMessage());
            }
        }
    }

    private void createVolleyballMatches() {
        List<VolleyballTeam> teams = volleyballTeamService.listAllTeams();
        
        if (teams.size() >= 8) {
            try {
                // Week 1 matches
                volleyballMatchService.createAndAddToFixtures(
                    teams.get(0), teams.get(1), 
                    3, 1, 
                    LocalDateTime.now().minusDays(8)
                );
                
                volleyballMatchService.createAndAddToFixtures(
                    teams.get(2), teams.get(3), 
                    3, 2, 
                    LocalDateTime.now().minusDays(8)
                );
                
                volleyballMatchService.createAndAddToFixtures(
                    teams.get(4), teams.get(5), 
                    3, 0, 
                    LocalDateTime.now().minusDays(8)
                );
                
                volleyballMatchService.createAndAddToFixtures(
                    teams.get(6), teams.get(7), 
                    2, 3, 
                    LocalDateTime.now().minusDays(8)
                );
                
                // Week 2 matches
                volleyballMatchService.createAndAddToFixtures(
                    teams.get(1), teams.get(2), 
                    3, 1, 
                    LocalDateTime.now().minusDays(6)
                );
                
                volleyballMatchService.createAndAddToFixtures(
                    teams.get(3), teams.get(4), 
                    1, 3, 
                    LocalDateTime.now().minusDays(6)
                );
                
                volleyballMatchService.createAndAddToFixtures(
                    teams.get(5), teams.get(6), 
                    3, 2, 
                    LocalDateTime.now().minusDays(6)
                );
                
                volleyballMatchService.createAndAddToFixtures(
                    teams.get(7), teams.get(0), 
                    0, 3, 
                    LocalDateTime.now().minusDays(6)
                );
                
                System.out.println("✅ Created 8 volleyball matches");
            } catch (Exception e) {
                System.out.println("⚠️ Error creating volleyball matches: " + e.getMessage());
            }
        }
    }

    private void createNews() {
        System.out.println("📰 Creating news articles...");
        
        try {
            newsService.create("Универзитетската лига започнува со 8 тимови!", 
                "FOOTBALL", 
                "Голема радост на УКИМ како започна новата сезона на Универзитетската лига. Оваа сезона се натпреваруваат 8 факултети во фудбал, кошарка и одбојка: Пумбарски, Медицински, Технички, Економски, Филолошки, Правен, Педагошки и Факултет за информатички науки.");
            
            newsService.create("Пумбарски факултет победник во фудбал", 
                "FOOTBALL", 
                "Во драматичен натпревар, Пумбарски факултет го победи Медицински факултет со резултат 2:1. Головите ги постигнаа Александар Петровски и Марко Јовановски.");
            
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
                "Во најдолгиот натпревар до сега, Пумбарски факултет го победи Медицински факултет во одбојка со резултат 3:2. Натпреварот траеше преку 2 часа.");
            
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
                    "/images/football/jersey.jpg",
                    team
                );
                footballProductRepository.save(jersey);
                
                FootballProduct scarf = new FootballProduct(
                    team.getTeamName() + " - Шал",
                    "Официјален шал на " + team.getTeamName() + " со лого",
                    12.50,
                    "/images/football/scarf.jpg",
                    team
                );
                footballProductRepository.save(scarf);
                
                FootballProduct cap = new FootballProduct(
                    team.getTeamName() + " - Капа",
                    "Спортска капа на " + team.getTeamName(),
                    8.99,
                    "/images/football/cap.jpg",
                    team
                );
                footballProductRepository.save(cap);
                
                FootballProduct mug = new FootballProduct(
                    team.getTeamName() + " - Шолја",
                    "Керамичка шолја со лого на " + team.getTeamName(),
                    6.99,
                    "/images/football/mug.jpg",
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
                    "/images/basketball/jersey.jpg",
                    team
                );
                basketballProductRepository.save(jersey);
                
                BasketballProduct shorts = new BasketballProduct(
                    team.getTeamName() + " - Шорцеви",
                    "Официјални кошаркарски шорцеви на " + team.getTeamName(),
                    22.50,
                    "/images/basketball/shorts.jpg",
                    team
                );
                basketballProductRepository.save(shorts);
                
                BasketballProduct ball = new BasketballProduct(
                    team.getTeamName() + " - Топка",
                    "Официјална кошаркарска топка на " + team.getTeamName(),
                    35.99,
                    "/images/basketball/ball.jpg",
                    team
                );
                basketballProductRepository.save(ball);
                
                BasketballProduct wristband = new BasketballProduct(
                    team.getTeamName() + " - Нараквица",
                    "Спортска нараквица на " + team.getTeamName(),
                    4.99,
                    "/images/basketball/wristband.jpg",
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
                    "/images/volleyball/jersey.jpg",
                    team
                );
                volleyballProductRepository.save(jersey);
                
                VolleyballProduct shorts = new VolleyballProduct(
                    team.getTeamName() + " - Шорцеви",
                    "Официјални одбојкарски шорцеви на " + team.getTeamName(),
                    18.50,
                    "/images/volleyball/shorts.jpg",
                    team
                );
                volleyballProductRepository.save(shorts);
                
                VolleyballProduct ball = new VolleyballProduct(
                    team.getTeamName() + " - Топка",
                    "Официјална одбојкарска топка на " + team.getTeamName(),
                    32.99,
                    "/images/volleyball/ball.jpg",
                    team
                );
                volleyballProductRepository.save(ball);
                
                VolleyballProduct kneePads = new VolleyballProduct(
                    team.getTeamName() + " - Коленки",
                    "Заштитни коленки за одбојка на " + team.getTeamName(),
                    15.99,
                    "/images/volleyball/kneepads.jpg",
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
