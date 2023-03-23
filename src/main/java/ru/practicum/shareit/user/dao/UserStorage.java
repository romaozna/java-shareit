package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {
    User getById(Long id);

    User save(User user);

    User update(User user, Long id);

    List<User> getAll();

    void delete(Long id);
}
