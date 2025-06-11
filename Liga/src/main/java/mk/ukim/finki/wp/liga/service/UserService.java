package mk.ukim.finki.wp.liga.service;

import mk.ukim.finki.wp.liga.model.User;
import java.util.Optional;

public interface UserService {
    Optional<User> findByUsername(String username);
}