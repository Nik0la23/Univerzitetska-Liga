package mk.ukim.finki.wp.liga.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
@Data
@Setter
@Getter
@Entity
@Table(name = "football_player")
@DiscriminatorColumn(name = "dtype")
@DiscriminatorValue("FootballPlayer")
public class FootballPlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long football_player_id;
    

    @Lob
    @Column(name="profile_image")
    private  byte [] image;
    private String name;
    private String surname;
    private Date birthdate;
    private int index;
    private String city;
    private String position;
    @ManyToOne
    @JsonIgnore
    private FootballTeam team;
    private int appearances;
    private int goals;
    private int assists;
    private int saves;
    private Double price;
    
    @Column(name = "fantasy_points")
    private Integer fantasyPoints;

    public FootballPlayer(byte [] image, String name, String surname, Date birthdate, int index,
                          String city, String position, FootballTeam team) {
        this.image = image;
        this.name = name;
        this.surname = surname;
        this.birthdate = birthdate;
        this.index = index;
        this.city = city;
        this.position = position;
        this.team = team;
        this.appearances = 0;
        this.goals = 0;
        this.assists = 0;
        this.saves = 0;
        this.price = 5.0;
        this.fantasyPoints = 0;
    }

    public FootballPlayer() {

    }
    public int getPoints(){
        return goals*2 + assists;
    }
    
    public int getFantasyPoints() {
        if (fantasyPoints == null) {
            fantasyPoints = 0;
        }
        return fantasyPoints;
    }
    
    public void setFantasyPoints(Integer fantasyPoints) {
        this.fantasyPoints = fantasyPoints;
    }
    
    /**
     * Calculate dynamic price based on player statistics
     * Base price: 5.0
     * Goals: +2.0 per goal
     * Assists: +1.0 per assist  
     * Saves: +1.5 per save (for goalkeepers)
     * Appearances: +0.1 per appearance
     * Minimum price: 1.0, Maximum price: 50.0
     */
    public void calculateDynamicPrice() {
        double basePrice = 5.0;
        double calculatedPrice = basePrice;
        
        // Goals are highly valued
        calculatedPrice += goals * 2.0;
        
        // Assists are valuable
        calculatedPrice += assists * 1.0;
        
        // Saves are valuable for goalkeepers
        calculatedPrice += saves * 1.5;
        
        // Appearances show consistency
        calculatedPrice += appearances * 0.1;
        
        // Position-based adjustments
        if ("GK".equals(position)) {
            // Goalkeepers get bonus for saves
            calculatedPrice += saves * 0.5;
        } else if ("FWD".equals(position)) {
            // Forwards get bonus for goals
            calculatedPrice += goals * 0.5;
        } else if ("MID".equals(position)) {
            // Midfielders get bonus for assists
            calculatedPrice += assists * 0.5;
        }
        
        // Apply bounds
        calculatedPrice = Math.max(1.0, Math.min(50.0, calculatedPrice));
        
        this.price = calculatedPrice;
    }
    
    @Override
    public String toString() {
        return "Football Player{" +
                "id=" + football_player_id +
                ", playerName='" + name + '\'' +
                '}';
    }

}