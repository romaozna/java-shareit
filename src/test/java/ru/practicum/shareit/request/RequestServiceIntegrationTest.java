package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RequestServiceIntegrationTest {

    @Autowired
    private RequestService requestService;
    @Autowired
    private UserService userService;

    private UserDto userDto;
    private ItemRequestDto requestDto;

    @BeforeEach
    public void initVarsForTests() {
        userDto = new UserDto(
                null,
                "Roman",
                "roman@mail.com");

        requestDto = new ItemRequestDto(
                1L,
                "request",
                null,
                null);
    }

    @Test
    void createRequestTest() {
        UserDto createdUser = userService.create(userDto);
        requestService.create(createdUser.getId(), requestDto);

        List<ItemRequestDto> requestDtoList = requestService.getAllUserRequests(createdUser.getId());

        Assertions.assertEquals(1, requestDtoList.size());
        Assertions.assertEquals(1L, requestDtoList.get(0).getId());
        Assertions.assertEquals(requestDto.getDescription(), requestDtoList.get(0).getDescription());
        Assertions.assertEquals(0, requestDtoList.get(0).getItems().size());
    }

    @Test
    void getRequestByWrongRequestIdTest() {
        Long id = 2L;

        Assertions
                .assertThrows(NotFoundException.class, () -> requestService.getRequestById(1L, id));
    }
}