package mk.ukim.finki.wp.liga.web.news;

import lombok.AllArgsConstructor;
import mk.ukim.finki.wp.liga.model.BasketballPlayer;
import mk.ukim.finki.wp.liga.model.FootballPlayer;
import mk.ukim.finki.wp.liga.model.News;
import mk.ukim.finki.wp.liga.model.VolleyballPlayer;
import mk.ukim.finki.wp.liga.service.basketball.BasketballPlayerService;
import mk.ukim.finki.wp.liga.service.football.FootballPlayerService;
import mk.ukim.finki.wp.liga.service.news.NewsService;
import mk.ukim.finki.wp.liga.service.volleyball.VolleyballPlayerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/news")
public class NewsController {

    private final NewsService newsService;
    private final FootballPlayerService footballPlayerService;
    private final BasketballPlayerService basketballPlayerService;
    private final VolleyballPlayerService volleyballPlayerService;

    @GetMapping
    public String showAllNews(Model model) {
        List<News> allNews = newsService.listAllNews();
        List<FootballPlayer> topFootballPlayers = footballPlayerService.getTop5Players();
        List<BasketballPlayer> topBasketballPlayers = basketballPlayerService.getTop5Players();
        List<VolleyballPlayer> topVolleyballPlayers = volleyballPlayerService.getTop5Players();
        model.addAttribute("allNews", allNews);
        model.addAttribute("topFootballPlayers", topFootballPlayers);
        model.addAttribute("topBasketballPlayers", topBasketballPlayers);
        model.addAttribute("topVolleyballPlayers", topVolleyballPlayers);
        model.addAttribute("bodyContent", "news");
        return "master_template";
    }

    @GetMapping("/details/{id}")
    public String showNewsDetails(@PathVariable Long id, Model model) {
        News news = newsService.findById(id);
        model.addAttribute("news", news);
        
        // Use sport-specific template based on the news sport
        String templateName;
        if (news.getSport().equals("FOOTBALL")) {
            templateName = "football_news_details";
        } else if (news.getSport().equals("BASKETBALL")) {
            templateName = "basketball/basketball_news_details";
        } else if (news.getSport().equals("VOLLEYBALL")) {
            templateName = "volleyball/volleyball_news_details";
        } else {
            templateName = "football_news_details"; // fallback
        }
        
        model.addAttribute("bodyContent", templateName);
        return "master_template";
    }
}


