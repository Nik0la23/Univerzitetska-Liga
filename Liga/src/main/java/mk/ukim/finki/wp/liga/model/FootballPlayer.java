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
    }

    public FootballPlayer() {

    }
    public int getPoints(){
        return goals*2 + assists;
    }
    @Override
    public String toString() {
        return "Football Player{" +
                "id=" + football_player_id +
                ", playerName='" + name + '\'' +
                '}';
    }

}