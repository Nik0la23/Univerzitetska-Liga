package mk.ukim.finki.wp.liga.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Data
@Entity
@Setter
@Getter
public class BasketballMatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long basketball_match_id;
    @ManyToOne
    @JoinColumn(name = "basketball_home_id")
    private BasketballTeam homeTeam;
    @ManyToOne
    @JoinColumn(name = "basketball_away_id")
    private BasketballTeam awayTeam;
    private int homeTeamPoints;
    private int awayTeamPoints;
    private int homeTeamQ1Points;
    private int homeTeamQ2Points;
    private int homeTeamQ3Points;
    private int homeTeamQ4Points;
    private int awayTeamQ1Points;
    private int awayTeamQ2Points;
    private int awayTeamQ3Points;
    private int awayTeamQ4Points;
    @OneToMany(mappedBy = "basketballMatch", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BasketballPlayerMatchStats> playerMatchStats;
    @ManyToMany(mappedBy = "basketballFixtures")
    private List<BasketballTeam> upcomingMatches;
    @ManyToMany(mappedBy = "basketballResults")
    private List<BasketballTeam> playedMatches;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    @Column(name = "is_basketball_playoff_match")
    private boolean isPlayoffMatch;

    public BasketballMatch(BasketballTeam homeTeam, BasketballTeam awayTeam, int homeTeamPoints,
                           int awayTeamPoints, LocalDateTime startTime, boolean isPlayoffMatch) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeTeamPoints = homeTeamPoints;
        this.awayTeamPoints = awayTeamPoints;
        this.homeTeamQ1Points = 0;
        this.homeTeamQ2Points = 0;
        this.homeTeamQ3Points = 0;
        this.homeTeamQ4Points = 0;
        this.awayTeamQ1Points = 0;
        this.awayTeamQ2Points = 0;
        this.awayTeamQ3Points = 0;
        this.awayTeamQ4Points = 0;
        this.playerMatchStats = new ArrayList<>();
        this.startTime = startTime;
        this.endTime = startTime.plusHours(2);
        this.isPlayoffMatch = isPlayoffMatch;
    }

    public BasketballMatch() {

    }
}
