package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static ru.practicum.shareit.user.dto.UserMapper.toUser;
import static ru.practicum.shareit.user.dto.UserMapper.toUserDto;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        return toUserDto(userStorage.save(toUser(userDto)));
    }

    @Override
    public UserDto getById(Long id) {
        User user = userStorage.findById(id).orElseThrow(() -> {
            throw new NotFoundException("User with id: " + id + " not found");
        });
        return toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto update(UserDto userDto, Long id) {
        User dtoUser = toUser(userDto);
        User savedUser = toUser(getById(id));
        String name = dtoUser.getName();
        String email = dtoUser.getEmail();
        if (name != null && !name.isBlank()) {
            savedUser.setName(name);
        }
        if (email != null && !email.isBlank()) {
            savedUser.setEmail(email);
        }
        return toUserDto(userStorage.save(savedUser));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        userStorage.deleteById(id);
    }

    @Override
    public List<UserDto> getAll() {
        return toUserDto(userStorage.findAll());
    }
}
