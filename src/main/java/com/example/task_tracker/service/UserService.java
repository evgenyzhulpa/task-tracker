package com.example.task_tracker.service;

import com.example.task_tracker.entity.User;
import com.example.task_tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Flux<User> findAll() {
        return userRepository.findAll();
    }

    public Mono<User> findById(String id) {
        return userRepository.findById(id);
    }

    public Mono<User> findByName(String name) {
        return userRepository.findByName(name);
    }

    public Mono<User> save(User user) {
        user.setId(UUID.randomUUID().toString());
        return userRepository.save(user);
    }

    public Mono<User> update(String id, User user) {
        return userRepository.findById(id)
                .flatMap(userForUpdate -> {
                    String name = user.getName();
                    if (!name.isBlank()) {
                        userForUpdate.setName(name);
                    }
                    String email = user.getEmail();
                    if (!email.isBlank()) {
                        userForUpdate.setEmail(email);
                    }
                    return userRepository.save(userForUpdate);
                });
    }

    public Mono<Void> deleteById(String id) {
        return userRepository.deleteById(id);
    }
}
