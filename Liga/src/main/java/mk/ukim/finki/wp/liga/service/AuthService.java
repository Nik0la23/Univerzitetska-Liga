package mk.ukim.finki.wp.liga.service;

import java.util.Set;

import mk.ukim.finki.wp.liga.model.Role;

public interface AuthService {
    String register(String username, String password, Set<Role> roles);
    String login(String username, String password);
}
