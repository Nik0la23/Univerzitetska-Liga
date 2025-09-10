package mk.ukim.finki.wp.liga.service.football;

import mk.ukim.finki.wp.liga.model.FootballPlayer;
import mk.ukim.finki.wp.liga.model.FootballTeam;

import java.util.Date;
import java.util.List;

public interface FootballPlayerService {

    List<FootballPlayer> listAllPlayers();


    FootballPlayer findById(Long id);


    FootballPlayer create(byte [] image, String name, String surname, Date birthdate, int index, String city, String position, FootballTeam team);


    FootballPlayer update(Long id, byte [] image, String name, String surname, Date birthdate, int index, String city, String position, FootballTeam team);
    FootballPlayer update(Long id, byte [] image, String name, String surname, Date birthdate, int index, String city, String position, FootballTeam team, int appearances, int goals, int assists, int saves, Double price);


    FootballPlayer delete(Long id);

    FootballPlayer addAppearances(Long Id);
    FootballPlayer addGoals(Long Id, int goalsToAdd);
    FootballPlayer addAssists(Long Id, int assistsToAdd);
    FootballPlayer addSaves(Long Id, int savesToAdd);

    List<FootballPlayer> getPlayersByIds(List<Long> ids);
    List<FootballPlayer> getTop5Players();
    List<FootballPlayer> getTop5PlayersByTeam(Long teamId);
    void recalculateAllPlayerPrices();
   // Long getTeamId();
}
