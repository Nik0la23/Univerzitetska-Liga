# Liga Project - H2 Database Setup

This guide explains how to run the Liga project using H2 in-memory database with automatic data seeding.

## Quick Start

1. **Run the application:**
   ```bash
   ./mvnw spring-boot:run
   ```
   
   Or on Windows:
   ```cmd
   mvnw.cmd spring-boot:run
   ```

2. **Access the application:**
   - Open your browser and go to: `http://localhost:9090`

3. **Access H2 Console (for debugging):**
   - Go to: `http://localhost:9090/h2-console`
   - JDBC URL: `jdbc:h2:mem:liga_db`
   - Username: `sa`
   - Password: (leave empty)

## What's Included

### Database Setup
- **H2 In-Memory Database** - No external database required
- **Automatic Schema Creation** - Tables are created automatically
- **Data Seeding** - Sample data is loaded on every startup

### Sample Data
The DatabaseSeeder automatically creates:

#### Users
- `admin` (admin@liga.com) - password: `admin123`
- `user1` (user1@liga.com) - password: `password123`
- `user2` (user2@liga.com) - password: `password123`
- `coach1` (coach1@liga.com) - password: `password123`
- `player1` (player1@liga.com) - password: `password123`

#### Teams (6 teams per sport)
- **Football:** Пумбарски факултет, Медицински факултет, Технички факултет, Економски факултет, Филолошки факултет, Правен факултет
- **Basketball:** Same 6 teams
- **Volleyball:** Same 6 teams

#### Players
- **11 players per team** with realistic Macedonian names
- **Proper positions** for each sport
- **Random birth dates** between 1995-2005

#### Matches
- **Sample matches** with realistic scores
- **Historical dates** (matches from past days)

#### News Articles
- **4 sample news articles** covering all three sports
- **Macedonian content** about university league activities

#### Fantasy Teams
- **Fantasy teams** created for users across all sports
- **Different formations** for each sport

## Configuration Details

### Application Properties
- **Database:** H2 in-memory (`jdbc:h2:mem:liga_db`)
- **Port:** 9090
- **Schema:** Auto-created on startup (`create-drop`)
- **H2 Console:** Enabled for debugging

### Key Features
- **No External Dependencies** - No Docker or PostgreSQL required
- **Automatic Data Seeding** - Fresh data on every startup
- **Development Friendly** - H2 console for database inspection
- **Fast Startup** - In-memory database is very fast

## Console Output

When you run the application, you'll see:
```
🌱 Starting database seeding...
👥 Creating users...
⚽ Creating football data...
🏀 Creating basketball data...
🏐 Creating volleyball data...
📰 Creating news articles...
🎮 Creating fantasy teams...
✅ Database seeding completed successfully!
🎯 Application is ready! Access it at: http://localhost:9090
🔍 H2 Console available at: http://localhost:9090/h2-console
   JDBC URL: jdbc:h2:mem:liga_db
   Username: sa
   Password: (leave empty)
```

## Important Notes

- **Data Persistence:** Data is lost when the application stops (in-memory database)
- **Fresh Data:** Every application restart creates fresh sample data
- **Development Only:** This setup is ideal for development and testing
- **No Docker Required:** You can run the application without Docker

## Switching Back to PostgreSQL

If you want to switch back to PostgreSQL with Docker:

1. Uncomment the PostgreSQL configuration in `application.properties`
2. Comment out the H2 configuration
3. Start the Docker database: `docker-compose up -d`
4. Restart the application

The DatabaseSeeder will automatically detect existing data and skip seeding when using PostgreSQL.
