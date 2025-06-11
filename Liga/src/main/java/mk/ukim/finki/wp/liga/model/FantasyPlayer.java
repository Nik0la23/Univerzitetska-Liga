package mk.ukim.finki.wp.liga.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
@Entity
public class FantasyPlayer extends FootballPlayer {
    
    @Column(nullable = false)
    private Double price;
    
    @Column(nullable = false)
    private Integer fantasyPoints;
    
    @ManyToOne
    @JoinColumn(name = "fantasy_team_id")
    private FantasyTeam fantasyTeam;
    
    public FantasyPlayer() {
        super();
        this.fantasyPoints = 0;
    }
    
    @Override
    public int getPoints() {
        // Fantasy points calculation based on real performance
        return this.fantasyPoints + super.getPoints();
    }
    
    public void updateFantasyPoints() {
        // Calculate fantasy points based on real performance
        this.fantasyPoints = getGoals() * 5 + getAssists() * 3 + getAppearances() * 2 + getSaves() * 2;
    }
} 