package mk.ukim.finki.wp.liga.service;

import mk.ukim.finki.wp.liga.model.User;

import java.util.Optional;

public interface UserService {
    User register(String name, String email, String rawPassword);
    Optional<User> login(String name, String rawPassword);
    Optional<User> findByName(String name);
}


