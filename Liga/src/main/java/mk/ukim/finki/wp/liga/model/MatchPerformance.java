package mk.ukim.finki.wp.liga.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
@Entity
public class MatchPerformance {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "player_id", nullable = false)
    private FantasyPlayer player;
    
    @ManyToOne
    @JoinColumn(name = "match_id", nullable = false)
    private FootballMatch match;
    
    @Column(nullable = false)
    private Integer minutesPlayed;
    
    @Column(nullable = false)
    private Integer goals;
    
    @Column(nullable = false)
    private Integer assists;
    
    @Column(nullable = false)
    private Integer saves;
    
    @Column(nullable = false)
    private Integer yellowCards;
    
    @Column(nullable = false)
    private Integer redCards;
    
    @Column(nullable = false)
    private Integer fantasyPoints;
    
    public MatchPerformance() {
        this.minutesPlayed = 0;
        this.goals = 0;
        this.assists = 0;
        this.saves = 0;
        this.yellowCards = 0;
        this.redCards = 0;
        this.fantasyPoints = 0;
    }
} 