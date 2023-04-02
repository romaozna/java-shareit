package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    private final UserDto userDto = new UserDto(
            null,
            "Roman",
            "roman@mail.dom");

    @Test
    void createNewUser() {
        UserDto createdUser = userService.create(userDto);

        Assertions.assertEquals(1L, createdUser.getId());
        Assertions.assertEquals(userDto.getName(), createdUser.getName());
        Assertions.assertEquals(userDto.getEmail(), createdUser.getEmail());
    }

    @Test
    void getUserWithInvalidId() {
        Long userId = 999L;

        Assertions.assertThrows(NotFoundException.class, () -> userService.getById(userId));
    }
}