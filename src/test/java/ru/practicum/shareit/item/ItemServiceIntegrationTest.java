package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ItemServiceIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private RequestService requestService;

    @Autowired
    private BookingService bookingService;


    private final UserDto userDto = new UserDto(
            null,
            "Roman",
            "roman@mail.com");

    private final ItemDto itemDto = new ItemDto(
            null,
            "Brush",
            "Brush for wash",
            true,
            null,
            null,
            new ArrayList<>(),
            null);

    private final ItemDto itemDto2 = new ItemDto(
            null,
            "Brush",
            "Brush for trash",
            true,
            null,
            null,
            new ArrayList<>(),
            null);

    private final UserDto userDto2 = new UserDto(
            null,
            "Max",
            "max@mail.com");

    private final ItemDto itemDtoToRequest = new ItemDto(
            null,
            "Brush",
            "Brush for stash",
            true,
            null,
            null,
            new ArrayList<>(),
            1L);

    private final ItemRequestDto requestDto = new ItemRequestDto(
            1L,
            "request",
            null,
            null);

    private final BookingInDto bookingInDto = new BookingInDto(
            1L,
            LocalDateTime.now().plusMinutes(3),
            LocalDateTime.now().plusMinutes(5));

    private final CommentDto commentDto = new CommentDto(
            null,
            "Akuna-matata",
            null,
            null);

    @Test
    void createItem() {
        UserDto createdUser = userService.create(userDto);
        ItemDto createdItem = itemService.create(createdUser.getId(), itemDto);

        Assertions.assertEquals(1L, createdItem.getId());
        Assertions.assertEquals(itemDto.getName(), createdItem.getName());
        Assertions.assertEquals(itemDto.getDescription(), createdItem.getDescription());
    }

    @Test
    void addItemToRequest() {
        UserDto createdUser = userService.create(userDto);
        requestService.create(createdUser.getId(), requestDto);

        ItemDto createdItemToRequest = itemService.create(createdUser.getId(), itemDtoToRequest);

        Assertions.assertEquals(1L, createdItemToRequest.getRequestId());
        Assertions.assertEquals(itemDtoToRequest.getName(), createdItemToRequest.getName());
        Assertions.assertEquals(itemDtoToRequest.getDescription(), createdItemToRequest.getDescription());
    }

    @Test
    void addComment() {

        UserDto createdUser1 = userService.create(userDto);
        UserDto createdUser2 = userService.create(userDto2);
        ItemDto createdItem = itemService.create(createdUser2.getId(), itemDto2);
        BookingOutDto bookingOutDto1 = bookingService.create(createdUser1.getId(), bookingInDto);

        bookingService
                .approveOrReject(createdUser2.getId(), bookingOutDto1.getId(), true);

        CommentDto createdComment = itemService.createComment(commentDto, createdUser1.getId(),
                createdItem.getId(), LocalDateTime.now().plusMinutes(10));

        Assertions.assertEquals(1L, createdComment.getId());
        Assertions.assertEquals(commentDto.getText(), createdComment.getText());
    }

    @Test
    void getItemByWrongItemId() {
        Long id = 2L;

        Assertions
                .assertThrows(NotFoundException.class, () -> itemService.getById(id, id));
    }
}