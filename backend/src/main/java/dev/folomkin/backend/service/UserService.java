package dev.folomkin.backend.service;

import dev.folomkin.backend.exception.ConflictException;
import dev.folomkin.backend.model.User;
import dev.folomkin.backend.repository.UserRepository;

import java.sql.SQLException;
import java.util.List;

public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }


    public User createUser(String name, String email) throws SQLException {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Имя обязательно");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email обязателен");
        }

        // Проверка на дубликат
        if (repository.findByEmail(email) != null) {
            throw new ConflictException("Пользователь с email '" + email + "' уже существует");
        }

        return repository.save(name, email);
    }


    public List<User> getAllUsers() throws SQLException {
        return repository.findAll();
    }

    public User getUserByEmail(String email) throws SQLException {
        return repository.findByEmail(email);
    }

}
