package mk.ukim.finki.wp.liga.service.basketball.impl;

import mk.ukim.finki.wp.liga.model.BasketballTeam;
import mk.ukim.finki.wp.liga.model.Exceptions.InvalidBasketballProductException;
import mk.ukim.finki.wp.liga.model.Exceptions.InvalidFootballProductException;
import mk.ukim.finki.wp.liga.model.shop.BasketballProduct;
import mk.ukim.finki.wp.liga.repository.basketball.BasketballProductRepository;
import mk.ukim.finki.wp.liga.repository.basketball.BasketballTeamRepository;
import mk.ukim.finki.wp.liga.service.basketball.BasketballProductService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BasketballProductServiceImpl implements BasketballProductService {

    private final BasketballProductRepository basketballProductRepository;
    private final BasketballTeamRepository basketballTeamRepository;

    public BasketballProductServiceImpl(BasketballProductRepository basketballProductRepository, BasketballTeamRepository basketballTeamRepository) {
        this.basketballProductRepository = basketballProductRepository;
        this.basketballTeamRepository = basketballTeamRepository;
    }

    @Override
    public List<BasketballProduct> findByTeam(BasketballTeam team) {
        return basketballProductRepository.findByBasketballMerch(team);
    }

    @Override
    public List<BasketballProduct> findByTeamName(String teamName) {
        return basketballProductRepository.findByBasketballMerch_TeamName(teamName);
    }

    @Override
    public List<BasketballProduct> findAll() {
        return basketballProductRepository.findAll();
    }

    @Override
    public List<BasketballProduct> findByTeamId(Long teamId) {
        return basketballProductRepository.findById(teamId).stream().toList();
    }

    @Override
    public BasketballProduct findById(Long id) {
        return basketballProductRepository.findById(id).orElseThrow(InvalidBasketballProductException::new);
    }


    @Override
    public void deleteById(Long id) {
        basketballProductRepository.deleteById(id);
    }

    @Override
    public void createNewBasketballProduct(String name, String description, double price, String imageUrl, Long teamId) {
        BasketballTeam team = basketballTeamRepository.findById(teamId).orElseThrow(InvalidFootballProductException::new);
        BasketballProduct product = new BasketballProduct(name,description,price,imageUrl,team);
        basketballProductRepository.save(product);
    }

    @Override
    public void update(Long id, String name, String description, double price, String imageUrl, Long teamId) {
        BasketballProduct product = findById(id);
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setImage(imageUrl);
        BasketballTeam team = basketballTeamRepository.findById(teamId).orElseThrow(InvalidFootballProductException::new);
        product.setBasketballMerch(team);
        basketballProductRepository.save(product);
    }


}
