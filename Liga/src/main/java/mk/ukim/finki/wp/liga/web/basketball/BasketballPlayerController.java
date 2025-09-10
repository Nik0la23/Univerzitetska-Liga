package mk.ukim.finki.wp.liga.web.basketball;

import lombok.AllArgsConstructor;
import mk.ukim.finki.wp.liga.model.BasketballPlayer;
import mk.ukim.finki.wp.liga.model.BasketballTeam;
import mk.ukim.finki.wp.liga.service.basketball.BasketballPlayerService;
import mk.ukim.finki.wp.liga.service.basketball.BasketballTeamService;
import org.springframework.format.annotation.DateTimeFormat;
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
@RequestMapping({"/basketball/players"})
public class BasketballPlayerController {

    private final BasketballPlayerService basketballPlayerService;
    private final BasketballTeamService basketballTeamService;

    @GetMapping
    public String showAllPlayers(Model model) {
        List<BasketballPlayer> basketballPlayers = basketballPlayerService.listAllPlayers();
        model.addAttribute("basketballPlayers", basketballPlayers);
        List<BasketballPlayer> topPlayers=basketballPlayerService.getTop5Players();
        model.addAttribute("topPlayers",topPlayers);
        model.addAttribute("bodyContent","basketball/basketball_players");
        return "/basketball/master_template";
    }

    @GetMapping("/add-form")
    public String addPlayerForm(Model model){
        List<BasketballTeam> teams=this.basketballTeamService.listAllTeams();
        model.addAttribute("teams",teams);
        model.addAttribute("bodyContent","basketball/add_basketball_player");
        return "/basketball/master_template";
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
        BasketballTeam team1;
        if(team !=null)
            team1=this.basketballTeamService.findById(team);
        else
            team1=null;
        Date birthDate = Date.from(playerBirthDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        this.basketballPlayerService.create(imageBytes,playerName,playerSurname,
                birthDate,playerIndex,playerCity,playerPosition,team1);

        return "redirect:/basketball/players";
    }

    @GetMapping("/edit/{id}")
    public String editProductPage(@PathVariable Long id, Model model) {
        if (this.basketballPlayerService.findById(id)!=null) {
            BasketballPlayer player = this.basketballPlayerService.findById(id);
            List<BasketballTeam> teams = this.basketballTeamService.listAllTeams();

            model.addAttribute("teams", teams);
            model.addAttribute("player", player);
            model.addAttribute("bodyContent", "basketball/edit_basketball_player");
            return "/basketball/master_template";
        }

        return "redirect:/basketball/players?error=PlayerNotFound";
    }

    @PostMapping("/edit/{id}")
    public String editPlayer(@PathVariable("id") Long id,
                             @RequestParam(value = "playerImage", required = false) MultipartFile playerImage,
                             @RequestParam String playerName,
                             @RequestParam String playerSurname,
                             @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate playerBirthDate,
                             @RequestParam int playerIndex,
                             @RequestParam String playerCity,
                             @RequestParam String playerPosition,
                             @RequestParam Long team,
                             @RequestParam(defaultValue = "0") int appearances,
                             @RequestParam(defaultValue = "0") int points,
                             @RequestParam(defaultValue = "0") int assists,
                             @RequestParam(defaultValue = "0") int rebounds,
                             Model model
    ) {
        BasketballPlayer existingPlayer = basketballPlayerService.findById(id);
        if (existingPlayer == null) {
            return "redirect:/basketball/players";
        }
        byte[] imageBytes = existingPlayer.getImage();
        if (playerImage != null && !playerImage.isEmpty()) {
            try {
                imageBytes = playerImage.getBytes();
                System.out.println(imageBytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        BasketballTeam team1 = basketballTeamService.findById(team);
        Date birthDate = Date.from(playerBirthDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        // Update the player's details
        basketballPlayerService.update(id, imageBytes, playerName, playerSurname, birthDate, playerIndex, playerCity, playerPosition, team1, appearances, points, assists, rebounds, 0.0);
        String imageUrl = "/basketball/players/image/" + id;
        model.addAttribute("playerImageUrl", imageUrl);
        return "redirect:/basketball/players";
    }


    @GetMapping("/details/{id}")
    public String getPlayerDetails(@PathVariable Long id, Model model){
        BasketballPlayer basketballPlayer = basketballPlayerService.findById(id);
        if (basketballPlayer == null) {
            return "redirect:/basketball/players"; // Redirect to the list of players or another appropriate page
        }
        model.addAttribute("basketballPlayer", basketballPlayer);
        String imageUrl = "basketball/players/image/" + id;
        model.addAttribute("playerImageUrl", imageUrl);
        model.addAttribute("bodyContent","basketball/basketball_player_details");
        return "/basketball/master_template";
    }

    @GetMapping("/image/{id}")
@ResponseBody
public byte[] getPlayerImage(@PathVariable Long id) {
    BasketballPlayer player = basketballPlayerService.findById(id);
    return player.getImage();
}

    @PostMapping("/delete/{id}")
    public String deletePlayer(@PathVariable Long id){
        basketballPlayerService.delete(id);
        return "redirect:/basketball/players";
    }

}
