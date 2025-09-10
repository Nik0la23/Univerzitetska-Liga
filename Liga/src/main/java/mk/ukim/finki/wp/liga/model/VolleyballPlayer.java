package mk.ukim.finki.wp.liga.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
@Data
@Entity
@Table(name = "volleyball_player")
@Setter
@Getter
public class VolleyballPlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long volleyball_player_id;
    @Lob
    private byte[] image;
    private String name;
    private String surname;
    private Date birthdate;
    private int index;
    private String city;
    private String position;
    @ManyToOne
    @JsonIgnore
    private VolleyballTeam team;
    private int appearances;
    private int servings;
    private int assists;
    private int scoredPoints;
    private int blocks;
    private Double price;

    public VolleyballPlayer(byte[] image, String name, String surname, Date birthdate, int index,
                            String city, String position, VolleyballTeam team) {
        this.image = image;
        this.name = name;
        this.surname = surname;
        this.birthdate = birthdate;
        this.index = index;
        this.city = city;
        this.position = position;
        this.team = team;
        this.appearances = 0;
        this.servings = 0;
        this.assists = 0;
        this.scoredPoints = 0;
        this.blocks = 0;
        this.price = 5.0;
    }

    public VolleyballPlayer() {

    }

    public int getTotalPoints(){
        return scoredPoints + assists + blocks +servings;
    }
    
    /**
     * Calculate dynamic price based on player statistics
     * Base price: 5.0
     * Scored Points: +1.5 per point
     * Assists: +1.0 per assist
     * Blocks: +1.2 per block
     * Servings: +0.8 per serving
     * Appearances: +0.1 per appearance
     * Minimum price: 1.0, Maximum price: 50.0
     */
    public void calculateDynamicPrice() {
        double basePrice = 5.0;
        double calculatedPrice = basePrice;
        
        // Scored points are highly valuable
        calculatedPrice += scoredPoints * 1.5;
        
        // Assists show playmaking ability
        calculatedPrice += assists * 1.0;
        
        // Blocks show defensive prowess
        calculatedPrice += blocks * 1.2;
        
        // Servings show serving ability
        calculatedPrice += servings * 0.8;
        
        // Appearances show consistency
        calculatedPrice += appearances * 0.1;
        
        // Position-based adjustments
        if ("S".equals(position)) {
            // Setters get bonus for assists
            calculatedPrice += assists * 0.5;
        } else if ("OH".equals(position) || "OPP".equals(position)) {
            // Outside hitters and opposites get bonus for scored points
            calculatedPrice += scoredPoints * 0.3;
        } else if ("MB".equals(position)) {
            // Middle blockers get bonus for blocks
            calculatedPrice += blocks * 0.5;
        } else if ("L".equals(position) || "DS".equals(position)) {
            // Liberos and defensive specialists get bonus for appearances
            calculatedPrice += appearances * 0.2;
        }
        
        // Apply bounds
        calculatedPrice = Math.max(1.0, Math.min(50.0, calculatedPrice));
        
        this.price = calculatedPrice;
    }
    
    @Override
    public String toString() {
        return "Volleyball Player{" +
                "id=" + volleyball_player_id +
                ", playerName='" + name + '\'' +
                '}';
    }

}