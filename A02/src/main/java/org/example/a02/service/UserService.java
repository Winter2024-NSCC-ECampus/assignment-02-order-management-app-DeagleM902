package org.example.a02.service;

import org.example.a02.dto.UserRegistrationDto;
import org.example.a02.model.User;
import org.example.a02.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(UserRegistrationDto dto){
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        return userRepository.save(user);
    }

    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    public Object getAllUsers() {
        return userRepository.findAll()
                .stream()
                .toList();
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

}
