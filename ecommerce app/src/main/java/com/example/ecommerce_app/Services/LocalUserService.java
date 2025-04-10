package com.example.ecommerce_app.Services;

import com.example.ecommerce_app.Repositories.LocalUserRepository;
import com.example.ecommerce_app.Model.LocalUser;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class LocalUserService {

    private final LocalUserRepository localUserRepository;

    public LocalUserService(LocalUserRepository localUserRepository) {
        this.localUserRepository = localUserRepository;
    }

    public LocalUser getUserById(Long userId) {
        return localUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    }
}
