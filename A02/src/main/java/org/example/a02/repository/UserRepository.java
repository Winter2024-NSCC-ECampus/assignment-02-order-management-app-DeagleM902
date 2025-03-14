package org.example.a02.repository;

import org.example.a02.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    //Fill as needed
}
