package mk.ukim.finki.wp.liga.web.news;

import lombok.AllArgsConstructor;
import mk.ukim.finki.wp.liga.model.FootballPlayer;
import mk.ukim.finki.wp.liga.model.News;
import mk.ukim.finki.wp.liga.service.football.FootballPlayerService;
import mk.ukim.finki.wp.liga.service.news.NewsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/football/news")
public class FootballNewsController {

    private final NewsService newsService;
    private final FootballPlayerService footballPlayerService;

    @GetMapping
    public String showFootballNews(Model model) {
        List<News> footballNews = newsService.findBySport("FOOTBALL");
        List<FootballPlayer> topPlayers = footballPlayerService.getTop5Players();
        model.addAttribute("footballNews", footballNews);
        model.addAttribute("topPlayers", topPlayers);
        model.addAttribute("bodyContent", "football_news");
        return "master_template";
    }

    @GetMapping("/details/{id}")
    public String showNewsDetails(@PathVariable Long id, Model model) {
        News news = newsService.findById(id);
        model.addAttribute("news", news);
        model.addAttribute("bodyContent", "football_news_details");
        return "master_template";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        News news = newsService.findById(id);
        if (news == null) {
            return "redirect:/football/news";
        }
        model.addAttribute("news", news);
        model.addAttribute("bodyContent","edit_football_news");
        return "master_template";
    }

    @PostMapping("/edit/{id}")
    public String editNews(@PathVariable Long id,
                           @RequestParam String title,
                           @RequestParam String content) {
        News existingNews = newsService.findById(id);
        if (existingNews == null) {
            return "redirect:/football/news";
        }

        newsService.update(id, title, "FOOTBALL", content);
        return "redirect:/football/news";
    }

    @GetMapping("/add")
    public String showAddNewsForm(Model model) {
        model.addAttribute("news", new News());
        model.addAttribute("bodyContent", "add_football_news");
        return "master_template";
    }

    @PostMapping("/add")
    public String addNews(@RequestParam String title,
                          @RequestParam String content) {
        newsService.create(title, "FOOTBALL", content);
        return "redirect:/football/news";
    }

    @GetMapping("/delete/{id}")
    public String deleteNews(@PathVariable Long id) {
        newsService.delete(id);
        return "redirect:/football/news";
    }
}

