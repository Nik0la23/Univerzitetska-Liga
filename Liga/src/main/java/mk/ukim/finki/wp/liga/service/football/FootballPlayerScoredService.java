package mk.ukim.finki.wp.liga.service.football;

import mk.ukim.finki.wp.liga.model.FootballMatch;
import mk.ukim.finki.wp.liga.model.FootballPlayer;
import mk.ukim.finki.wp.liga.model.FootballPlayerScored;
import mk.ukim.finki.wp.liga.model.FootballTeam;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface FootballPlayerScoredService {

    List<FootballPlayerScored> listAllPlayersWhoScored();


    FootballPlayerScored findById(Long id);


    FootballPlayerScored create(FootballPlayer player, FootballMatch match);


    FootballPlayerScored update(Long id, FootballPlayer player, FootballMatch match);
    FootballPlayerScored findByPlayerAndMatch(FootballPlayer player, FootballMatch match);
    FootballPlayerScored delete(Long id);
    FootballPlayerScored save(FootballPlayerScored player);
    List<FootballPlayerScored> findByMatch(FootballMatch match);


}
