package mk.ukim.finki.wp.liga.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import mk.ukim.finki.wp.liga.config.JwtUtil;
import mk.ukim.finki.wp.liga.model.Role;
import mk.ukim.finki.wp.liga.model.User;
import mk.ukim.finki.wp.liga.repository.UserRepository;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public String register(String username, String password, Set<Role> roles) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(roles);
        userRepository.save(user);

        return jwtUtil.generateToken(username, roles.stream().map(Role::name).collect(Collectors.toSet()));
    }

    public String login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return jwtUtil.generateToken(username, user.getRoles().stream().map(Role::name).collect(Collectors.toSet()));
    }
}
