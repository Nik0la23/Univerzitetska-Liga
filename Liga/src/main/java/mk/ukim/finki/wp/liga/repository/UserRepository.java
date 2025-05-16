package mk.ukim.finki.wp.liga.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mk.ukim.finki.wp.liga.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
}