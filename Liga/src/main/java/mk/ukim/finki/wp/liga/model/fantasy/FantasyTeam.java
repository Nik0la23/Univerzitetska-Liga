package mk.ukim.finki.wp.liga.model.fantasy;

import jakarta.persistence.*;
import lombok.Data;
import mk.ukim.finki.wp.liga.model.User;

@Entity
@Data
public class FantasyTeam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User owner;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FantasySport sport;

    @Column(nullable = false)
    private String teamName;

    @Column(nullable = false)
    private Double budgetTotal = 100.0;

    @Column(nullable = false)
    private Double budgetSpent = 0.0;

    @Column(nullable = false)
    private String formation = "4-4-2"; // Default formation

    @Column(nullable = false)
    private Double totalPoints = 0.0; // Fantasy points scored

    @Column(name = "created_date")
    private java.time.LocalDateTime createdDate;

    @Column(name = "last_updated")
    private java.time.LocalDateTime lastUpdated;

    public FantasyTeam() {}

    public FantasyTeam(User owner, FantasySport sport, String teamName) {
        this.owner = owner;
        this.userId = owner.getId();
        this.sport = sport;
        this.teamName = teamName;
        this.budgetTotal = 100.0;
        this.budgetSpent = 0.0;
        this.formation = getDefaultFormation(sport);
        this.totalPoints = 0.0;
        this.createdDate = java.time.LocalDateTime.now();
        this.lastUpdated = java.time.LocalDateTime.now();
    }

    private String getDefaultFormation(FantasySport sport) {
        return switch (sport) {
            case FOOTBALL -> "4-4-2";
            case BASKETBALL -> "1-2-2";
            case VOLLEYBALL -> "6-2";
        };
    }
}



