package mk.ukim.finki.wp.liga.model.fantasy;

import jakarta.persistence.*;
import lombok.Data;
import mk.ukim.finki.wp.liga.model.BasketballPlayer;
import mk.ukim.finki.wp.liga.model.FootballPlayer;
import mk.ukim.finki.wp.liga.model.VolleyballPlayer;

@Entity
@Data
public class FantasyPlayer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private FantasyTeam fantasyTeam;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FantasySport sport;

    // References to actual players
    @ManyToOne
    private FootballPlayer footballPlayer;

    @ManyToOne
    private BasketballPlayer basketballPlayer;

    @ManyToOne
    private VolleyballPlayer volleyballPlayer;

    @Column(nullable = false)
    private Double pricePaid;

    @Column
    private String assignedPosition; // Position on field/court (e.g., "GK", "PG", etc.)

    public FantasyPlayer() {}

    public FantasyPlayer(FantasyTeam fantasyTeam, FootballPlayer footballPlayer, Double pricePaid) {
        this.fantasyTeam = fantasyTeam;
        this.footballPlayer = footballPlayer;
        this.sport = FantasySport.FOOTBALL;
        this.pricePaid = pricePaid;
    }

    public FantasyPlayer(FantasyTeam fantasyTeam, BasketballPlayer basketballPlayer, Double pricePaid) {
        this.fantasyTeam = fantasyTeam;
        this.basketballPlayer = basketballPlayer;
        this.sport = FantasySport.BASKETBALL;
        this.pricePaid = pricePaid;
    }

    public FantasyPlayer(FantasyTeam fantasyTeam, VolleyballPlayer volleyballPlayer, Double pricePaid) {
        this.fantasyTeam = fantasyTeam;
        this.volleyballPlayer = volleyballPlayer;
        this.sport = FantasySport.VOLLEYBALL;
        this.pricePaid = pricePaid;
    }

    // Getters and setters
    public Long getId() { return id; }

    public FantasyTeam getFantasyTeam() { return fantasyTeam; }
    public void setFantasyTeam(FantasyTeam fantasyTeam) { this.fantasyTeam = fantasyTeam; }

    public FantasySport getSport() { return sport; }
    public void setSport(FantasySport sport) { this.sport = sport; }

    public FootballPlayer getFootballPlayer() { return footballPlayer; }
    public void setFootballPlayer(FootballPlayer footballPlayer) { this.footballPlayer = footballPlayer; }

    public BasketballPlayer getBasketballPlayer() { return basketballPlayer; }
    public void setBasketballPlayer(BasketballPlayer basketballPlayer) { this.basketballPlayer = basketballPlayer; }

    public VolleyballPlayer getVolleyballPlayer() { return volleyballPlayer; }
    public void setVolleyballPlayer(VolleyballPlayer volleyballPlayer) { this.volleyballPlayer = volleyballPlayer; }

    public Double getPricePaid() { return pricePaid; }
    public void setPricePaid(Double pricePaid) { this.pricePaid = pricePaid; }

    public String getAssignedPosition() { return assignedPosition; }
    public void setAssignedPosition(String assignedPosition) { this.assignedPosition = assignedPosition; }

    // Helper methods to get player info regardless of sport
    public String getPlayerName() {
        if (footballPlayer != null) return footballPlayer.getName() + " " + footballPlayer.getSurname();
        if (basketballPlayer != null) return basketballPlayer.getName() + " " + basketballPlayer.getSurname();
        if (volleyballPlayer != null) return volleyballPlayer.getName() + " " + volleyballPlayer.getSurname();
        return "";
    }

    public String getPlayerPosition() {
        if (footballPlayer != null) return footballPlayer.getPosition();
        if (basketballPlayer != null) return basketballPlayer.getPosition();
        if (volleyballPlayer != null) return volleyballPlayer.getPosition();
        return "";
    }

    public String getTeamName() {
        if (footballPlayer != null && footballPlayer.getTeam() != null) return footballPlayer.getTeam().getTeamName();
        if (basketballPlayer != null && basketballPlayer.getTeam() != null) return basketballPlayer.getTeam().getTeamName();
        if (volleyballPlayer != null && volleyballPlayer.getTeam() != null) return volleyballPlayer.getTeam().getTeamName();
        return "";
    }

    public byte[] getPlayerImage() {
        if (footballPlayer != null) return footballPlayer.getImage();
        if (basketballPlayer != null) return basketballPlayer.getImage();
        if (volleyballPlayer != null) return volleyballPlayer.getImage();
        return null;
    }
}
