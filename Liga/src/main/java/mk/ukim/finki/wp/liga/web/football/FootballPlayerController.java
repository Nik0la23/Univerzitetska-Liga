package mk.ukim.finki.wp.liga.web.football;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import mk.ukim.finki.wp.liga.model.FootballPlayer;
import mk.ukim.finki.wp.liga.model.FootballTeam;
import mk.ukim.finki.wp.liga.service.football.FootballPlayerService;
import mk.ukim.finki.wp.liga.service.football.FootballTeamService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping({"/players"})
public class FootballPlayerController {

    private final FootballPlayerService footballPlayerService;
    private final FootballTeamService footballTeamService;


    @GetMapping
    public String showAllPlayers(Model model) {
        List<FootballPlayer> footballPlayers = footballPlayerService.listAllPlayers();
       model.addAttribute("footballPlayers", footballPlayers);
       List<FootballPlayer> topPlayers=footballPlayerService.getTop5Players();
       model.addAttribute("topPlayers",topPlayers);
       model.addAttribute("bodyContent", "football_players");
       return "master_template";
    }

    @GetMapping("/add-form")
    public String addPlayerForm(@RequestParam(value = "returnUrl", required = false, defaultValue = "/players") String returnUrl, Model model){
        List<FootballTeam> teams=this.footballTeamService.listAllTeams();
        model.addAttribute("teams",teams);
        model.addAttribute("bodyContent","add-football-player");
        model.addAttribute("returnUrl", returnUrl);
        model.addAttribute("bodyContent","add_football_player");
        return "master_template";

    }



    @PostMapping("/add")
    public String addPlayer(@RequestParam(value = "playerImage", required = false) MultipartFile playerImage,
                            @RequestParam String playerName,
                            @RequestParam String playerSurname,
                            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate playerBirthDate,
                            @RequestParam Integer playerIndex,
                            @RequestParam String playerCity,
                            @RequestParam String playerPosition,
                            @RequestParam(required = false) Long team,
                            Model model
    ) throws IOException {
        if (playerName.isEmpty() || playerSurname.isEmpty() || playerBirthDate == null || playerIndex == null || playerCity.isEmpty() || playerPosition.isEmpty()) {
            model.addAttribute("errorMessage", "Please fill out all required fields.");
            List<FootballTeam> teams = this.footballTeamService.listAllTeams();
            model.addAttribute("teams", teams);
            model.addAttribute("bodyContent","add_football_player");
            return "master_template"; // Return the view with the error message
        }
        byte [] imageBytes=null;
        if (playerImage != null && !playerImage.isEmpty()) {
            try {
                imageBytes = playerImage.getBytes();
            } catch (IOException e) {
                // Log the error and handle the exception appropriately
                e.printStackTrace();
                throw new RuntimeException("Failed to read the image file", e);
            }
        }
        FootballTeam team1;
        if(team !=null)
             team1=this.footballTeamService.findById(team);
        else
            team1=null;
        Date birthDate = Date.from(playerBirthDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        this.footballPlayerService.create(imageBytes,playerName,playerSurname,
                birthDate,playerIndex,playerCity,playerPosition,team1);

        return "redirect:/players" ;
    }
    @GetMapping("/edit/{id}")
    public String editFootballPlayer(@PathVariable Long id, Model model) {
        if (this.footballPlayerService.findById(id)!=null) {
            FootballPlayer player = this.footballPlayerService.findById(id);
            List<FootballTeam> teams = this.footballTeamService.listAllTeams();

            model.addAttribute("teams", teams);
            model.addAttribute("player", player);
            model.addAttribute("bodyContent", "edit_football_player");
            return "master_template";
        }

        return "redirect:/players?error=PlayerNotFound";
    }
    @PostMapping("/edit/{id}")
    public String editFootballPlayer(@PathVariable("id") Long id,
                             @RequestParam(value = "playerImage", required = false) MultipartFile playerImage,
                             @RequestParam String playerName,
                             @RequestParam String playerSurname,
                             @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate playerBirthDate,
                             @RequestParam int playerIndex,
                             @RequestParam String playerCity,
                             @RequestParam String playerPosition,
                             @RequestParam Long team,
                             Model model
    ) {
        if (playerName.isEmpty() || playerSurname.isEmpty() || playerBirthDate == null || playerIndex == 0 || playerCity.isEmpty() || playerPosition.isEmpty()) {
            model.addAttribute("errorMessage", "Please fill out all required fields.");
            FootballPlayer player = this.footballPlayerService.findById(id);
            List<FootballTeam> teams = this.footballTeamService.listAllTeams();
            model.addAttribute("player", player);
            model.addAttribute("teams", teams);
            model.addAttribute("bodyContent","edit_football_player");
            return "master_template"; // Return the view with the error message
        }

        byte[] imageBytes = null;
        if (playerImage != null && !playerImage.isEmpty()) {
            try {
                imageBytes = playerImage.getBytes();
                System.out.println(imageBytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FootballTeam team1 = footballTeamService.findById(team);
        Date birthDate = Date.from(playerBirthDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        footballPlayerService.update(id, imageBytes, playerName, playerSurname, birthDate, playerIndex, playerCity, playerPosition, team1);
        String imageUrl = "/players/image/" + id;
        model.addAttribute("playerImageUrl", imageUrl);
        return "redirect:/players";
    }
    @GetMapping("/details/{id}")
    public String getPlayerDetails(@PathVariable Long id, HttpServletRequest request, Model model){
        FootballPlayer footballPlayer = footballPlayerService.findById(id);
        if (footballPlayer == null) {
            return "redirect:/players"; // Redirect to the list of players or another appropriate page
        }
        model.addAttribute("footballPlayer", footballPlayer);
        model.addAttribute("referer", request.getHeader("Referer"));
        String imageUrl = "/players/image/" + id;
        model.addAttribute("playerImageUrl", imageUrl);
        model.addAttribute("bodyContent","football_player_details");
        return "master_template";
    }
    @GetMapping("/image/{id}")
    public ResponseEntity<byte[]> getPlayerImage(@PathVariable Long id) {
        FootballPlayer player = footballPlayerService.findById(id);

        if (player != null && player.getImage() != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG); // Set appropriate media type
            return new ResponseEntity<>(player.getImage(), headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @PostMapping("/delete/{id}")
    public String deletePlayer(@PathVariable Long id){
        footballPlayerService.delete(id);
         return "redirect:/players";
    }



}
