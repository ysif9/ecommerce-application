package com.example.ecommerce_app.Repositories;

import com.example.ecommerce_app.Model.LocalUser;
import org.springframework.data.repository.ListCrudRepository;

public interface LocalUserRepository extends ListCrudRepository<LocalUser, Long> {
}
