package mk.ukim.finki.wp.liga.service.basketball;

import mk.ukim.finki.wp.liga.model.BasketballPlayer;
import mk.ukim.finki.wp.liga.model.BasketballTeam;

import java.util.Date;
import java.util.List;

public interface BasketballPlayerService {
    List<BasketballPlayer> listAllPlayers();
    BasketballPlayer findById(Long id);
    BasketballPlayer create(byte [] image, String name, String surname, Date birthdate, int index, String city, String position, BasketballTeam team);
    BasketballPlayer update(Long id, byte [] image, String name, String surname, Date birthdate, int index, String city, String position, BasketballTeam team);
    BasketballPlayer update(Long id, byte [] image, String name, String surname, Date birthdate, int index, String city, String position, BasketballTeam team, int appearances, int points, int assists, int rebounds, Double price);

    BasketballPlayer delete(Long id);

    BasketballPlayer addAppearances(Long Id);
    BasketballPlayer addPoints(Long Id, int pointsToAdd);
    BasketballPlayer addAssists(Long Id, int assistsToAdd);
    BasketballPlayer addRebounds(Long Id, int reboundsToAdd);

    List<BasketballPlayer> getPlayersByIds(List<Long> ids);
    List<BasketballPlayer> getTop5Players();
    List<BasketballPlayer> getTop5PlayersByTeam(Long teamId);
    void addStats(Long playerId, int basketsToAdd);
    void recalculateAllPlayerPrices();
}
