package mk.ukim.finki.wp.liga.service.volleyball.impl;

import mk.ukim.finki.wp.liga.model.Exceptions.InvalidFootballProductException;
import mk.ukim.finki.wp.liga.model.Exceptions.InvalidVolleyballProductException;
import mk.ukim.finki.wp.liga.model.VolleyballTeam;
import mk.ukim.finki.wp.liga.model.shop.VolleyballProduct;
import mk.ukim.finki.wp.liga.repository.volleyball.VolleyballProductRepository;
import mk.ukim.finki.wp.liga.repository.volleyball.VolleyballTeamRepository;
import mk.ukim.finki.wp.liga.service.volleyball.VolleyballProductService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VolleyballProductServiceImpl implements VolleyballProductService {

    private final VolleyballProductRepository volleyballProductRepository;
    private final VolleyballTeamRepository volleyballTeamRepository;

    public VolleyballProductServiceImpl(VolleyballProductRepository volleyballProductRepository, VolleyballTeamRepository volleyballTeamRepository) {
        this.volleyballProductRepository = volleyballProductRepository;
        this.volleyballTeamRepository = volleyballTeamRepository;
    }

    @Override
    public List<VolleyballProduct> findByTeam(VolleyballTeam team) {
        return volleyballProductRepository.findByVolleyballMerch(team);
    }

    @Override
    public List<VolleyballProduct> findByTeamName(String teamName) {
        return volleyballProductRepository.findByVolleyballMerch_TeamName(teamName);
    }

    @Override
    public List<VolleyballProduct> findAll() {
        return volleyballProductRepository.findAll();
    }

    @Override
    public List<VolleyballProduct> findByTeamId(Long teamId) {
        return volleyballProductRepository.findById(teamId).stream().toList();
    }

    @Override
    public VolleyballProduct findById(Long id) {
        return volleyballProductRepository.findById(id).orElseThrow(InvalidVolleyballProductException::new);
    }

    @Override
    public void deleteById(Long id) {
        volleyballProductRepository.deleteById(id);
    }

    @Override
    public void createNewFootballProduct(String name, String description, double price, String imageUrl, Long teamId) {
        VolleyballTeam team = volleyballTeamRepository.findById(teamId).orElseThrow(InvalidFootballProductException::new);
        VolleyballProduct product = new VolleyballProduct(name,description,price,imageUrl,team);
        volleyballProductRepository.save(product);
    }

    @Override
    public void update(Long id, String name, String description, double price, String imageUrl, Long teamId) {
        VolleyballProduct product = findById(id);
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setImage(imageUrl);
        VolleyballTeam team = volleyballTeamRepository.findById(teamId).orElseThrow(InvalidFootballProductException::new);
        product.setVolleyballMerch(team);
        volleyballProductRepository.save(product);
    }
}
