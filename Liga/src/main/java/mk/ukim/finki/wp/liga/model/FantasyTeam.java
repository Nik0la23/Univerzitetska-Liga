package mk.ukim.finki.wp.liga.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Data
@Setter
@Getter
@Entity
public class FantasyTeam {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String teamName;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;
    
    @OneToMany(mappedBy = "fantasyTeam", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FantasyPlayer> players = new ArrayList<>();
    
    @Column(nullable = false)
    private String formation;
    
    @Column(nullable = false)
    private Double budgetTotal;
    
    @Column(nullable = false)
    private Double budgetSpent;
    
    @Column(nullable = false)
    private Integer totalPoints;
    
    public FantasyTeam() {
        this.budgetTotal = 100.0; // Default budget of 100
        this.budgetSpent = 0.0;
        this.totalPoints = 0;
    }
    
    public void addPlayer(FantasyPlayer player) {
        if (this.budgetSpent + player.getPrice() <= this.budgetTotal) {
            this.players.add(player);
            player.setFantasyTeam(this);
            this.budgetSpent += player.getPrice();
            updateTotalPoints();
        }
    }
    
    public void removePlayer(FantasyPlayer player) {
        if (this.players.remove(player)) {
            player.setFantasyTeam(null);
            this.budgetSpent -= player.getPrice();
            updateTotalPoints();
        }
    }
    
    private void updateTotalPoints() {
        this.totalPoints = this.players.stream()
                .mapToInt(FantasyPlayer::getPoints)
                .sum();
    }
} 