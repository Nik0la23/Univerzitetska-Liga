package mk.ukim.finki.wp.liga.service;

import java.util.Set;

public interface AuthService {

    String register(String username, String password, Set<String> roles);

    String login(String username, String password);
}