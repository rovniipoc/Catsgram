package ru.yandex.practicum.catsgram.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    private final Map<Long, User> users = new HashMap<>();

    public Collection<User> findAll() {
        return users.values();
    }

    public Optional<User> getUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    public User create(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ConditionsNotMetException("Email должен быть указан");
        }
        if (users.values()
                .stream()
                .map(User::getEmail)
                .anyMatch(email -> email.equals(user.getEmail()))) {
            throw new DuplicatedDataException("Этот email уже используется");
        }
        user.setId(getNextId());
        user.setRegistrationDate(Instant.now());
        users.put(user.getId(), user);
        return user;
    }

    public User update(User user) {
        if (user.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (users.containsKey(user.getId())) {
            User oldUser = users.get(user.getId());
            if (users.values()
                    .stream()
                    .map(User::getEmail)
                    .anyMatch(email -> email.equals(user.getEmail()))) {
                throw new DuplicatedDataException("Этот email уже используется");
            }
            if (user.getEmail() != null) {
                oldUser.setEmail(user.getEmail());
            }
            if (user.getUsername() != null) {
                oldUser.setUsername(user.getUsername());
            }
            if (user.getPassword() != null) {
                oldUser.setPassword(user.getPassword());
            }
            return oldUser;
        }
        throw new NotFoundException("Пользователь с id = " + user.getId() + " не найден");
    }

    public Optional<User> findUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
