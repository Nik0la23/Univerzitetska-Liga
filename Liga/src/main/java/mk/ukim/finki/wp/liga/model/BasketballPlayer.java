package mk.ukim.finki.wp.liga.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Data
@Entity
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
}