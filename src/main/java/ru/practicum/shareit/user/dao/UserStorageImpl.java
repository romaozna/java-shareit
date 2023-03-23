package ru.practicum.shareit.user.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class UserStorageImpl implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private Long userCounter = 0L;

    @Override
    public User getById(Long id) {
        User savedUser = users.get(id);
        if (savedUser != null) {
            return savedUser;
        } else {
            log.error("Id not found {} ", id);
            throw new NotFoundException("User with id: " + id + " not found");
        }
    }

    @Override
    public User save(User user) {
        validate(user, userCounter + 1);
        user.setId(++userCounter);
        users.put(userCounter, user);
        log.info("User with id {} saved", userCounter);
        return users.get(userCounter);
    }

    @Override
    public User update(User user, Long id) {
        User savedUser = getById(id);
        validate(user, id);
        String name = user.getName();
        String email = user.getEmail();
        if (name != null && !name.isBlank()) {
            savedUser.setName(name);
        }
        if (email != null && !email.isBlank()) {
            savedUser.setEmail(email);
        }
        return savedUser;
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void delete(Long id) {
        getById(id);
        users.remove(id);
    }

    private void validate(User user, Long id) {
        List<User> notUniqueUsers = users.values().stream()
                .filter(user1 -> user1.getEmail().equals(user.getEmail()))
                .filter(user1 -> !user1.getId().equals(id))
                .collect(Collectors.toList());
        if (!notUniqueUsers.isEmpty()) {
            log.debug("Not unique email field: {}", user.getEmail());
            throw new ValidationException("E-mail address is exist");
        }
    }
}
