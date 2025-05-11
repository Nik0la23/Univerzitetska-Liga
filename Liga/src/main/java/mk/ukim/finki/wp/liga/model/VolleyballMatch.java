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
public class VolleyballMatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long volleyball_match_id;
    
    @ManyToOne
    @JoinColumn(name = "volleyball_home_id")
    private VolleyballTeam homeTeam;
    
    @ManyToOne
    @JoinColumn(name = "volleyball_away_id")
    private VolleyballTeam awayTeam;
    
    private int homeTeamPoints; // Total sets won by home team
    private int awayTeamPoints; // Total sets won by away team
    
    // Set scores for home team
    private int homeTeamSet1Points;
    private int homeTeamSet2Points;
    private int homeTeamSet3Points;
    private int homeTeamSet4Points;
    private int homeTeamSet5Points;
    
    // Set scores for away team
    private int awayTeamSet1Points;
    private int awayTeamSet2Points;
    private int awayTeamSet3Points;
    private int awayTeamSet4Points;
    private int awayTeamSet5Points;
    
    private int currentSet; // Tracks which set is currently being played (1-5)
    
    @OneToMany(mappedBy = "volleyballMatch", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VolleyballPlayerMatchStats> playerMatchStats;
    
    @ManyToMany(mappedBy = "volleyballFixtures")
    private List<VolleyballTeam> upcomingMatches;
    
    @ManyToMany(mappedBy = "volleyballResults")
    private List<VolleyballTeam> playedMatches;
    
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    
    @Column(name = "is_volleyball_playoff_match")
    private boolean isVolleyballPlayoffMatch;

    public VolleyballMatch(VolleyballTeam homeTeam, VolleyballTeam awayTeam, int homeTeamPoints,
                          int awayTeamPoints, LocalDateTime startTime, boolean isVolleyballPlayoffMatch) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeTeamPoints = homeTeamPoints;
        this.awayTeamPoints = awayTeamPoints;
        this.playerMatchStats = new ArrayList<>();
        this.startTime = startTime;
        this.endTime = startTime.plusHours(2);
        this.isVolleyballPlayoffMatch = isVolleyballPlayoffMatch;
        this.currentSet = 1;
        
        // Initialize set scores to 0
        this.homeTeamSet1Points = 0;
        this.homeTeamSet2Points = 0;
        this.homeTeamSet3Points = 0;
        this.homeTeamSet4Points = 0;
        this.homeTeamSet5Points = 0;
        
        this.awayTeamSet1Points = 0;
        this.awayTeamSet2Points = 0;
        this.awayTeamSet3Points = 0;
        this.awayTeamSet4Points = 0;
        this.awayTeamSet5Points = 0;
    }

    public VolleyballMatch() {
    }
    
    // Helper method to get current set points for a team
    public int getCurrentSetPoints(boolean isHomeTeam) {
        return switch (currentSet) {
            case 1 -> isHomeTeam ? homeTeamSet1Points : awayTeamSet1Points;
            case 2 -> isHomeTeam ? homeTeamSet2Points : awayTeamSet2Points;
            case 3 -> isHomeTeam ? homeTeamSet3Points : awayTeamSet3Points;
            case 4 -> isHomeTeam ? homeTeamSet4Points : awayTeamSet4Points;
            case 5 -> isHomeTeam ? homeTeamSet5Points : awayTeamSet5Points;
            default -> 0;
        };
    }
    
    // Helper method to set current set points for a team
    public void setCurrentSetPoints(boolean isHomeTeam, int points) {
        switch (currentSet) {
            case 1 -> {
                if (isHomeTeam) homeTeamSet1Points = points;
                else awayTeamSet1Points = points;
            }
            case 2 -> {
                if (isHomeTeam) homeTeamSet2Points = points;
                else awayTeamSet2Points = points;
            }
            case 3 -> {
                if (isHomeTeam) homeTeamSet3Points = points;
                else awayTeamSet3Points = points;
            }
            case 4 -> {
                if (isHomeTeam) homeTeamSet4Points = points;
                else awayTeamSet4Points = points;
            }
            case 5 -> {
                if (isHomeTeam) homeTeamSet5Points = points;
                else awayTeamSet5Points = points;
            }
        }
    }
    
    // Helper method to check if current set is finished
    public boolean isCurrentSetFinished() {
        int homePoints = getCurrentSetPoints(true);
        int awayPoints = getCurrentSetPoints(false);
        int pointsNeeded = currentSet == 5 ? 15 : 25;
        
        return (homePoints >= pointsNeeded && homePoints - awayPoints >= 2) ||
               (awayPoints >= pointsNeeded && awayPoints - homePoints >= 2);
    }
    
    // Helper method to check if match is finished
    public boolean isMatchFinished() {
        return homeTeamPoints == 3 || awayTeamPoints == 3;
    }
}
