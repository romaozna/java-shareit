package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dao.UserRepository;
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
    private UserRepository userRepository;

    private UserDto userDto;
    private User user;
    private UserDto newUpdatedUserDto;
    private User newUpdatedUser;

    @BeforeEach
    public void initVarsForTests() {
        userDto = new UserDto(
                1L,
                "Roman",
                "roman@mail.com");

        user = new User(
                1L,
                "Roman",
                "roman@mail.com");

        newUpdatedUserDto = new UserDto(
                null,
                "Max",
                "max@yandex.ru");

        newUpdatedUser = new User(
                1L,
                "Max",
                "max@yandex.ru");
    }

    @Test
    void createUserTest() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto createdUser = userService.create(userDto);

        Assertions.assertNotNull(createdUser);
        Assertions.assertEquals(1, createdUser.getId());
        Assertions.assertEquals(userDto.getName(), createdUser.getName());
        Assertions.assertEquals(userDto.getEmail(), createdUser.getEmail());

        verify(userRepository, times(1)).save(any(User.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getAllUsersTest() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserDto> users = userService.getAll();

        Assertions.assertNotNull(users);
        Assertions.assertEquals(1, users.size());
        Assertions.assertEquals(1, users.get(0).getId());
        Assertions.assertEquals(userDto.getName(), users.get(0).getName());
        Assertions.assertEquals(userDto.getEmail(), users.get(0).getEmail());

        verify(userRepository, times(1)).findAll();
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getUserByIdTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        UserDto user = userService.getById(1L);

        Assertions.assertNotNull(user);
        Assertions.assertEquals(1, user.getId());
        Assertions.assertEquals(userDto.getName(), user.getName());
        Assertions.assertEquals(userDto.getEmail(), user.getEmail());

        verify(userRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateUserByIdTest() {
        when(userRepository.save(any(User.class))).thenReturn(newUpdatedUser);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        UserDto updatedUser = userService.update(newUpdatedUserDto, 1L);

        Assertions.assertNotNull(updatedUser);
        Assertions.assertEquals(1, updatedUser.getId());
        Assertions.assertEquals(newUpdatedUserDto.getName(), updatedUser.getName());
        Assertions.assertEquals(newUpdatedUserDto.getEmail(), updatedUser.getEmail());

        verify(userRepository, times(1)).save(any(User.class));
        verify(userRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateUserWithInvalidNameTest() {
        newUpdatedUserDto.setName("");

        when(userRepository.save(any(User.class))).thenReturn(newUpdatedUser);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        UserDto updatedUser = userService.update(newUpdatedUserDto, 1L);

        Assertions.assertNotNull(updatedUser);
        Assertions.assertEquals(1, updatedUser.getId());
        Assertions.assertNotEquals(newUpdatedUserDto.getName(), updatedUser.getName());
        Assertions.assertEquals(newUpdatedUserDto.getEmail(), updatedUser.getEmail());

        verify(userRepository, times(1)).save(any(User.class));
        verify(userRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateUserWithInvalidEmailTest() {
        newUpdatedUserDto.setEmail("");

        when(userRepository.save(any(User.class))).thenReturn(newUpdatedUser);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        UserDto updatedUser = userService.update(newUpdatedUserDto, 1L);

        Assertions.assertNotNull(updatedUser);
        Assertions.assertEquals(1, updatedUser.getId());
        Assertions.assertEquals(newUpdatedUserDto.getName(), updatedUser.getName());
        Assertions.assertNotEquals(newUpdatedUserDto.getEmail(), updatedUser.getEmail());

        verify(userRepository, times(1)).save(any(User.class));
        verify(userRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateUserWithNullNameTest() {
        newUpdatedUserDto.setName(null);

        when(userRepository.save(any(User.class))).thenReturn(newUpdatedUser);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        UserDto updatedUser = userService.update(newUpdatedUserDto, 1L);

        Assertions.assertNotNull(updatedUser);
        Assertions.assertEquals(1, updatedUser.getId());
        Assertions.assertNotEquals(newUpdatedUserDto.getName(), updatedUser.getName());
        Assertions.assertEquals(newUpdatedUserDto.getEmail(), updatedUser.getEmail());

        verify(userRepository, times(1)).save(any(User.class));
        verify(userRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateUserWithNullEmailTest() {
        newUpdatedUserDto.setEmail(null);

        when(userRepository.save(any(User.class))).thenReturn(newUpdatedUser);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        UserDto updatedUser = userService.update(newUpdatedUserDto, 1L);

        Assertions.assertNotNull(updatedUser);
        Assertions.assertEquals(1, updatedUser.getId());
        Assertions.assertEquals(newUpdatedUserDto.getName(), updatedUser.getName());
        Assertions.assertNotEquals(newUpdatedUserDto.getEmail(), updatedUser.getEmail());

        verify(userRepository, times(1)).save(any(User.class));
        verify(userRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void deleteUserByIdTest() {
        userService.delete(1L);

        verify(userRepository, times(1)).deleteById(anyLong());
        verifyNoMoreInteractions(userRepository);
    }
}
