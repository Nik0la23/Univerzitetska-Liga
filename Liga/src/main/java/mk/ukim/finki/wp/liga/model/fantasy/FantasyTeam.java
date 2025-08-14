package mk.ukim.finki.wp.liga.model.fantasy;

import jakarta.persistence.*;
import mk.ukim.finki.wp.liga.model.User;

@Entity
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
        switch (sport) {
            case FOOTBALL:
                return "4-4-2";
            case BASKETBALL:
                return "1-2-2";
            case VOLLEYBALL:
                return "6-2";
            default:
                return "4-4-2";
        }
    }

    public Long getId() { return id; }
    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public FantasySport getSport() { return sport; }
    public void setSport(FantasySport sport) { this.sport = sport; }
    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }
    public Double getBudgetTotal() { return budgetTotal; }
    public Double getBudgetSpent() { return budgetSpent; }
    public void setBudgetSpent(Double budgetSpent) { this.budgetSpent = budgetSpent; }
    public String getFormation() { return formation; }
    public void setFormation(String formation) { this.formation = formation; }
    public Double getTotalPoints() { return totalPoints; }
    public void setTotalPoints(Double totalPoints) { this.totalPoints = totalPoints; }
    public java.time.LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(java.time.LocalDateTime createdDate) { this.createdDate = createdDate; }
    public java.time.LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(java.time.LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}



