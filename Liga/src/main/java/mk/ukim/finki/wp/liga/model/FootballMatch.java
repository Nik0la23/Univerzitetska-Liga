package mk.ukim.finki.wp.liga.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Data
@Setter
@Getter
@Entity
public class FootballMatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long football_match_id;
    @ManyToOne
    @JoinColumn(name = "football_home_id")
    private FootballTeam homeTeam;
    @ManyToOne
    @JoinColumn(name = "football_away_id")
    private FootballTeam awayTeam;
    private int homeTeamPoints;
    private int awayTeamPoints;
    
    // Half scores for home team
    private int homeTeamH1Points;
    private int homeTeamH2Points;
    
    // Half scores for away team
    private int awayTeamH1Points;
    private int awayTeamH2Points;
    
    @OneToMany(mappedBy = "footballMatch", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FootballPlayerScored> playersWhoScored;
    @ManyToMany(mappedBy = "footballFixtures")
    private List<FootballTeam> upcomingMatches;
    @ManyToMany(mappedBy = "footballResults")
    private List<FootballTeam> playedMatches;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    @Column(name = "is_playoff_match")
    private boolean isPlayoffMatch;

    public FootballMatch(FootballTeam homeTeam, FootballTeam awayTeam, int homeTeamPoints,
                         int awayTeamPoints, LocalDateTime startTime, boolean isPlayoffMatch) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeTeamPoints = homeTeamPoints;
        this.awayTeamPoints = awayTeamPoints;
        this.homeTeamH1Points = 0;
        this.homeTeamH2Points = 0;
        this.awayTeamH1Points = 0;
        this.awayTeamH2Points = 0;
        this.playersWhoScored = new ArrayList<>();
        this.startTime = startTime;
        this.endTime = startTime.plusHours(2);
        this.isPlayoffMatch = isPlayoffMatch;
    }

    public FootballMatch() {

    }

}
