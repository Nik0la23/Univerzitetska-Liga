package mk.ukim.finki.wp.liga.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Data
@Entity
@Table(name = "basketball_player")
public class BasketballPlayer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long basketball_player_id;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "basketball_profile_image")
    private byte[] image;

    private String name;
    private String surname;
    private Date birthdate;
    private int index;
    private String city;
    private String position;

    @ManyToOne
    @JsonIgnore
    private BasketballTeam team;

    private int appearances;
    private int points;
    private int assists;
    private int rebounds;
    private Double price;

    public BasketballPlayer(byte[] image, String name, String surname, Date birthdate, int index,
                            String city, String position, BasketballTeam team) {
        this.image = image;
        this.name = name;
        this.surname = surname;
        this.birthdate = birthdate;
        this.index = index;
        this.city = city;
        this.position = position;
        this.team = team;
        this.appearances = 0;
        this.points = 0;
        this.assists = 0;
        this.rebounds = 0;
        this.price = 5.0;
    }

    public BasketballPlayer() {
    }

    @Override
    public String toString() {
        return "BasketballPlayer{" +
                "id=" + basketball_player_id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                '}';
    }

    public int getTotalPoints() {
        return points * 2 + assists + rebounds;
    }
    
    /**
     * Calculate dynamic price based on player statistics
     * Base price: 5.0
     * Points: +0.5 per point
     * Assists: +1.0 per assist
     * Rebounds: +0.8 per rebound
     * Appearances: +0.1 per appearance
     * Minimum price: 1.0, Maximum price: 50.0
     */
    public void calculateDynamicPrice() {
        double basePrice = 5.0;
        double calculatedPrice = basePrice;
        
        // Points are valuable
        calculatedPrice += points * 0.5;
        
        // Assists show playmaking ability
        calculatedPrice += assists * 1.0;
        
        // Rebounds show defensive/offensive presence
        calculatedPrice += rebounds * 0.8;
        
        // Appearances show consistency
        calculatedPrice += appearances * 0.1;
        
        // Position-based adjustments
        if ("PG".equals(position)) {
            // Point guards get bonus for assists
            calculatedPrice += assists * 0.5;
        } else if ("SG".equals(position) || "SF".equals(position)) {
            // Guards and forwards get bonus for points
            calculatedPrice += points * 0.3;
        } else if ("PF".equals(position) || "C".equals(position)) {
            // Big men get bonus for rebounds
            calculatedPrice += rebounds * 0.5;
        }
        
        // Apply bounds
        calculatedPrice = Math.max(1.0, Math.min(50.0, calculatedPrice));
        
        this.price = calculatedPrice;
    }
}