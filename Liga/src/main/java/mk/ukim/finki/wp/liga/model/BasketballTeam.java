package mk.ukim.finki.wp.liga.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class BasketballTeam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String teamName;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BasketballPlayer> players;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JoinTable(
            name = "basketball_team_fixtures",
            joinColumns = @JoinColumn(name = "basketball_team_id"),
            inverseJoinColumns = @JoinColumn(name = "basketball_match_id")
    )
    private List<BasketballMatch> basketballFixtures;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JoinTable(
            name = "basketball_team_results",
            joinColumns = @JoinColumn(name = "basketball_team_id"),
            inverseJoinColumns = @JoinColumn(name = "basketball_match_id")
    )
    private List<BasketballMatch> basketballResults;

    private int teamMatchesPlayed;
    private int teamLeaguePoints;
    private int teamWins;
    private int teamLoses;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "basketball_team_logo")
    private byte[] logo;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "basketball_team_last_five_matches", joinColumns = @JoinColumn(name = "basketball_team_id"))
    @Column(name = "basketball_match_result")
    private List<String> lastFiveMatches = new ArrayList<>();

    public BasketballTeam(String teamName, byte[] logo) {
        this.teamName = teamName;
        this.players = new ArrayList<>();
        this.basketballFixtures = new ArrayList<>();
        this.basketballResults = new ArrayList<>();
        this.teamMatchesPlayed = 0;
        this.teamLeaguePoints = 0;
        this.teamWins = 0;
        this.teamLoses = 0;
        this.logo = logo;
    }

    public BasketballTeam() {
    }

    public void addMatchResult(String result) {
        if (lastFiveMatches.size() == 5) {
            lastFiveMatches.remove(0);
        }
        lastFiveMatches.add(result);
    }
}