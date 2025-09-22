package com.rkchat.demo.repositories;

import com.rkchat.demo.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
   Optional <User> findByUsername(String username);
   List<User> findByUsernameContainingIgnoreCase(String username);
   Optional<User> findByGoogleId(String googleId);
}
