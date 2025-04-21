package com.example.ecommerce_app.Repositories;

import com.example.ecommerce_app.Model.LocalUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<LocalUser, Long> {
    Optional<LocalUser> findByEmail(String email);
    Optional<LocalUser> findByUsername(String username);

    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmailAndPassword(String email, String password);
    boolean existsByUsernameAndPassword(String username, String password);
}
