package mk.ukim.finki.wp.liga.service.volleyball;

import mk.ukim.finki.wp.liga.model.VolleyballPlayer;
import mk.ukim.finki.wp.liga.model.VolleyballTeam;

import java.util.Date;
import java.util.List;

public interface VolleyballPlayerService {
    List<VolleyballPlayer> listAllPlayers();


    VolleyballPlayer findById(Long id);


    VolleyballPlayer create(byte [] image, String name, String surname, Date birthdate, int index, String city, String position, VolleyballTeam team);


    VolleyballPlayer update(Long id, byte [] image, String name, String surname, Date birthdate, int index, String city, String position, VolleyballTeam team);
    VolleyballPlayer update(Long id, byte [] image, String name, String surname, Date birthdate, int index, String city, String position, VolleyballTeam team, int appearances, int servings, int assists, int scoredPoints, int blocks, Double price);


    VolleyballPlayer delete(Long id);

    VolleyballPlayer addAppearances(Long Id);
    VolleyballPlayer addPoints(Long Id, int pointsToAdd);
    VolleyballPlayer addAssists(Long Id, int assistsToAdd);
    VolleyballPlayer addServings(Long Id, int servingsToAdd);
    VolleyballPlayer addBlocks(Long Id, int blocksToAdd);

    List<VolleyballPlayer> getPlayersByIds(List<Long> ids);
    List<VolleyballPlayer> getTop5Players();

    List<VolleyballPlayer> getTop5PlayersByTeam(Long id);
    void recalculateAllPlayerPrices();
}
