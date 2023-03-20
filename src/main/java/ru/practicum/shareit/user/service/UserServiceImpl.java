package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final UserMapper userMapper;

    @Override
    public UserDto create(UserDto userDto) {
        return userMapper.toUserDto(userStorage.save(userMapper.toUser(userDto)));
    }

    @Override
    public UserDto getById(Long id) {
        return userMapper.toUserDto(userStorage.getById(id));
    }

    @Override
    public UserDto update(UserDto userDto, Long id) {
        return userMapper.toUserDto(userStorage.update(userMapper.toUser(userDto), id));
    }

    @Override
    public void delete(Long id) {
        userStorage.delete(id);
    }

    @Override
    public List<UserDto> getAll() {
        return userStorage.getAll().stream().map(userMapper::toUserDto).collect(Collectors.toList());
    }
}
