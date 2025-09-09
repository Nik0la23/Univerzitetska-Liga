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
        System.out.println("🔐 Attempting login for user: " + name);
        Optional<User> userOpt = userRepository.findByName(name);
        
        if (userOpt.isEmpty()) {
            System.out.println("❌ User not found: " + name);
            return Optional.empty();
        }
        
        User user = userOpt.get();
        boolean passwordMatches = encoder.matches(rawPassword, user.getPasswordHash());
        System.out.println("🔑 Password match for " + name + ": " + passwordMatches);
        
        if (passwordMatches) {
            System.out.println("✅ Login successful for user: " + name);
            return Optional.of(user);
        } else {
            System.out.println("❌ Invalid password for user: " + name);
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByName(String name) {
        return userRepository.findByName(name);
    }
}


