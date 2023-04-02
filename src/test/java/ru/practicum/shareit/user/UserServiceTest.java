package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserStorage userStorage;

    private final UserDto userDto = new UserDto(
            null,
            "Roman",
            "roman@mail.com");

    private final User user = new User(
            1L,
            "Roman",
            "roman@mail.com");

    @Test
    void createUserTest() {
        when(userStorage.save(any(User.class))).thenReturn(user);

        UserDto createdUser = userService.create(userDto);

        Assertions.assertNotNull(createdUser);
        Assertions.assertEquals(1, createdUser.getId());
        Assertions.assertEquals(userDto.getName(), createdUser.getName());
        Assertions.assertEquals(userDto.getEmail(), createdUser.getEmail());

        verify(userStorage, times(1)).save(any(User.class));
        verifyNoMoreInteractions(userStorage);
    }

    @Test
    void getAllUsersTest() {
        when(userStorage.findAll()).thenReturn(List.of(user));

        List<UserDto> users = userService.getAll();

        Assertions.assertNotNull(users);
        Assertions.assertEquals(1, users.size());
        Assertions.assertEquals(1, users.get(0).getId());
        Assertions.assertEquals(userDto.getName(), users.get(0).getName());
        Assertions.assertEquals(userDto.getEmail(), users.get(0).getEmail());

        verify(userStorage, times(1)).findAll();
        verifyNoMoreInteractions(userStorage);
    }

    @Test
    void getUserByIdTest() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));

        UserDto user = userService.getById(1L);

        Assertions.assertNotNull(user);
        Assertions.assertEquals(1, user.getId());
        Assertions.assertEquals(userDto.getName(), user.getName());
        Assertions.assertEquals(userDto.getEmail(), user.getEmail());

        verify(userStorage, times(1)).findById(anyLong());
        verifyNoMoreInteractions(userStorage);
    }

    @Test
    void updateUserByIdTest() {
        UserDto newUpdatedUserDto = new UserDto(null, "Max", "max@yandex.ru");
        User newUpdatedUser = new User(1L, "Max", "max@yandex.ru");

        when(userStorage.save(any(User.class))).thenReturn(newUpdatedUser);
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));

        UserDto updatedUser = userService.update(newUpdatedUserDto, 1L);

        Assertions.assertNotNull(updatedUser);
        Assertions.assertEquals(1, updatedUser.getId());
        Assertions.assertEquals(newUpdatedUserDto.getName(), updatedUser.getName());
        Assertions.assertEquals(newUpdatedUserDto.getEmail(), updatedUser.getEmail());

        verify(userStorage, times(1)).save(any(User.class));
        verify(userStorage, times(1)).findById(anyLong());
        verifyNoMoreInteractions(userStorage);
    }

    @Test
    void updateUserWithInvalidNameTest() {
        UserDto newUpdatedUserDto = new UserDto(null, "", "max@yandex.ru");
        User newUpdatedUser = new User(1L, "Max", "max@yandex.ru");

        when(userStorage.save(any(User.class))).thenReturn(newUpdatedUser);
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));

        UserDto updatedUser = userService.update(newUpdatedUserDto, 1L);

        Assertions.assertNotNull(updatedUser);
        Assertions.assertEquals(1, updatedUser.getId());
        Assertions.assertNotEquals(newUpdatedUserDto.getName(), updatedUser.getName());
        Assertions.assertEquals(newUpdatedUserDto.getEmail(), updatedUser.getEmail());

        verify(userStorage, times(1)).save(any(User.class));
        verify(userStorage, times(1)).findById(anyLong());
        verifyNoMoreInteractions(userStorage);
    }

    @Test
    void updateUserWithInvalidEmailTest() {
        UserDto newUpdatedUserDto = new UserDto(null, "Max", "");
        User newUpdatedUser = new User(1L, "Max", "max@yandex.ru");

        when(userStorage.save(any(User.class))).thenReturn(newUpdatedUser);
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));

        UserDto updatedUser = userService.update(newUpdatedUserDto, 1L);

        Assertions.assertNotNull(updatedUser);
        Assertions.assertEquals(1, updatedUser.getId());
        Assertions.assertEquals(newUpdatedUserDto.getName(), updatedUser.getName());
        Assertions.assertNotEquals(newUpdatedUserDto.getEmail(), updatedUser.getEmail());

        verify(userStorage, times(1)).save(any(User.class));
        verify(userStorage, times(1)).findById(anyLong());
        verifyNoMoreInteractions(userStorage);
    }

    @Test
    void updateUserWithNullNameTest() {
        UserDto newUpdatedUserDto = new UserDto(null, null, "max@yandex.ru");
        User newUpdatedUser = new User(1L, "Max", "max@yandex.ru");

        when(userStorage.save(any(User.class))).thenReturn(newUpdatedUser);
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));

        UserDto updatedUser = userService.update(newUpdatedUserDto, 1L);

        Assertions.assertNotNull(updatedUser);
        Assertions.assertEquals(1, updatedUser.getId());
        Assertions.assertNotEquals(newUpdatedUserDto.getName(), updatedUser.getName());
        Assertions.assertEquals(newUpdatedUserDto.getEmail(), updatedUser.getEmail());

        verify(userStorage, times(1)).save(any(User.class));
        verify(userStorage, times(1)).findById(anyLong());
        verifyNoMoreInteractions(userStorage);
    }

    @Test
    void updateUserWithNullEmailTest() {
        UserDto newUpdatedUserDto = new UserDto(null, "Max", null);
        User newUpdatedUser = new User(1L, "Max", "max@yandex.ru");

        when(userStorage.save(any(User.class))).thenReturn(newUpdatedUser);
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));

        UserDto updatedUser = userService.update(newUpdatedUserDto, 1L);

        Assertions.assertNotNull(updatedUser);
        Assertions.assertEquals(1, updatedUser.getId());
        Assertions.assertEquals(newUpdatedUserDto.getName(), updatedUser.getName());
        Assertions.assertNotEquals(newUpdatedUserDto.getEmail(), updatedUser.getEmail());

        verify(userStorage, times(1)).save(any(User.class));
        verify(userStorage, times(1)).findById(anyLong());
        verifyNoMoreInteractions(userStorage);
    }

    @Test
    void deleteUserByIdTest() {
        userService.delete(1L);

        verify(userStorage, times(1)).deleteById(anyLong());
        verifyNoMoreInteractions(userStorage);
    }
}
