package mk.ukim.finki.wp.liga.service.impl;

import lombok.RequiredArgsConstructor;
import mk.ukim.finki.wp.liga.model.User;
import mk.ukim.finki.wp.liga.repository.UserRepository;
import mk.ukim.finki.wp.liga.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SimpleUserService implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public User register(String name, String email, String rawPassword) {
        if (userRepository.existsByName(name)) {
            throw new RuntimeException("Name already taken");
        }
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already in use");
        }
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPasswordHash(encoder.encode(rawPassword));
        return userRepository.save(user);
    }

    @Override
    public Optional<User> login(String name, String rawPassword) {
        return userRepository.findByName(name)
                .filter(u -> encoder.matches(rawPassword, u.getPasswordHash()));
    }

    @Override
    public Optional<User> findByName(String name) {
        return userRepository.findByName(name);
    }
}


