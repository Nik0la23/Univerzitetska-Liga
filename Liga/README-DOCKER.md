# Liga Project - Docker Setup Guide

This guide explains how to run the Liga project using Docker Compose.

## Prerequisites

- Docker installed on your system
- Docker Compose installed (usually comes with Docker Desktop)

## Quick Start

1. **Clone the repository:**
   ```bash
   git clone <your-repo-url>
   cd Liga
   ```

2. **Start the database:**
   ```bash
   docker-compose up -d
   ```

3. **Run the Spring Boot application:**
   ```bash
   ./mvnw spring-boot:run
   ```
   
   Or on Windows:
   ```cmd
   mvnw.cmd spring-boot:run
   ```

4. **Access the application:**
   - Open your browser and go to: `http://localhost:9090`

## What's Included

### Database Setup
- **PostgreSQL 15** database running in Docker
- **Database name:** Liga
- **Username:** liga_user
- **Password:** admin
- **Port:** 5432

### Initial Data
- The `db-init/initial_data.sql` file contains complete sample data
- This includes teams, players, matches, and more
- Data is automatically loaded when you first start the database

## Configuration Details

### Docker Compose Services

- **postgres-db**: PostgreSQL 15 database
  - Container name: `my_project_db`
  - Port: 5432
  - Persistent data storage via Docker volume

### Application Configuration

The Spring Boot application connects to the PostgreSQL database with these settings:
- **URL:** `jdbc:postgresql://localhost:5432/Liga`
- **Username:** `liga_user`
- **Password:** `admin`

## Commands Reference

### Start the database
```bash
docker-compose up -d
```

### Stop the database
```bash
docker-compose down
```

### View database logs
```bash
docker-compose logs postgres-db
```

### Remove everything (including data)
```bash
docker-compose down -v
```

### Rebuild and restart
```bash
docker-compose down
docker-compose up -d
```

## Troubleshooting

### Port Already in Use
If port 5432 is already in use, you can change it in `docker-compose.yaml`:
```yaml
ports:
  - "5433:5432"  # Changed from 5432:5432
```

Then update `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5433/Liga
```

### Database Connection Issues
1. Make sure Docker is running
2. Check if the database container is running: `docker ps`
3. Wait a few seconds after starting for the database to initialize

### Fresh Database
To start with a completely fresh database:
```bash
docker-compose down -v
docker-compose up -d
```

## Security Notes

- The database credentials are for development only
- Change them in production environments
- The database is only accessible from localhost by default
