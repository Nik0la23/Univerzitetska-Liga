package mk.ukim.finki.wp.liga.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
@Data
@Getter
@Setter
@Entity
public class PlayoffMatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "stage_id")
    private PlayoffStage stage;

    @ManyToOne
    @JoinColumn(name = "home_team_id")
    private FootballTeam homeTeam;

    @ManyToOne
    @JoinColumn(name = "away_team_id")
    private FootballTeam awayTeam;

    private int homeTeamPoints;
    private int awayTeamPoints;
    private boolean isCompleted;

    public PlayoffMatch(FootballTeam homeTeam, FootballTeam awayTeam, int homeTeamPoints, int awayTeamPoints, boolean isCompleted) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeTeamPoints = homeTeamPoints;
        this.awayTeamPoints = awayTeamPoints;
        this.isCompleted = isCompleted;
    }
    public PlayoffMatch(PlayoffStage stage, FootballTeam homeTeam, FootballTeam awayTeam, int homeTeamPoints, int awayTeamPoints, boolean isCompleted) {
        this.stage = stage;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeTeamPoints = homeTeamPoints;
        this.awayTeamPoints = awayTeamPoints;
        this.isCompleted = isCompleted;
    }
    public PlayoffMatch(){}
}
