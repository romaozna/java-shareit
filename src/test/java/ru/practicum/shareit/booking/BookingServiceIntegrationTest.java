package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BookingServiceIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    private final UserDto userDto1 = new UserDto(
            null,
            "Roman",
            "Roman@mail.com");

    private final UserDto userDto2 = new UserDto(
            null,
            "Max",
            "max@mail.com");

    private final ItemDto itemDto1 = new ItemDto(
            null,
            "brush",
            "best brush",
            true,
            null,
            null,
            null,
            null);

    private final ItemDto itemDto2 = new ItemDto(
            null,
            "hummer",
            "best hummer",
            true,
            null,
            null,
            null,
            null);

    private final BookingInDto bookingInDto1 = new BookingInDto(
            2L,
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(2));

    private final BookingInDto bookingInDto2 = new BookingInDto(
            2L,
            LocalDateTime.now().plusMinutes(30),
            LocalDateTime.now().plusHours(1));

    @Test
    void getAllBookingsByOwnerTest() {
        UserDto createdUser1 = userService.create(userDto1);
        ItemDto createdItem1 = itemService.create(createdUser1.getId(), itemDto1);
        UserDto createdUser2 = userService.create(userDto2);
        ItemDto createdItem2 = itemService.create(createdUser2.getId(), itemDto2);
        BookingOutDto bookingOutDto1 = bookingService.create(createdUser1.getId(), bookingInDto1);
        BookingOutDto bookingOutDto2 = bookingService.create(createdUser1.getId(), bookingInDto2);

        Assertions.assertEquals(1L, createdItem1.getId());
        Assertions.assertEquals(2L, createdItem2.getId());
        Assertions.assertEquals(1L, bookingOutDto1.getId());
        Assertions.assertEquals(1L, bookingOutDto1.getBooker().getId());
        Assertions.assertEquals(2L, bookingOutDto1.getItem().getId());
        Assertions.assertEquals(Status.WAITING.name(), bookingOutDto1.getStatus());

        Assertions.assertEquals(1L, createdItem1.getId());
        Assertions.assertEquals(2L, createdItem2.getId());
        Assertions.assertEquals(2L, bookingOutDto2.getId());
        Assertions.assertEquals(1L, bookingOutDto2.getBooker().getId());
        Assertions.assertEquals(2L, bookingOutDto2.getItem().getId());
        Assertions.assertEquals(Status.WAITING.name(), bookingOutDto2.getStatus());

        BookingOutDto approveOutDto1 = bookingService
                .approveOrReject(createdUser2.getId(), bookingOutDto1.getId(), true);
        BookingOutDto approveOutDto2 = bookingService
                .approveOrReject(createdUser2.getId(), bookingOutDto2.getId(), true);

        Assertions.assertEquals(1L, approveOutDto1.getId());
        Assertions.assertEquals(1L, approveOutDto1.getBooker().getId());
        Assertions.assertEquals(2L, approveOutDto1.getItem().getId());
        Assertions.assertEquals(Status.APPROVED.name(), approveOutDto1.getStatus());

        Assertions.assertEquals(2L, approveOutDto2.getId());
        Assertions.assertEquals(1L, approveOutDto2.getBooker().getId());
        Assertions.assertEquals(2L, approveOutDto2.getItem().getId());
        Assertions.assertEquals(Status.APPROVED.name(), approveOutDto2.getStatus());

        List<BookingOutDto> bookingOutputDtoList = bookingService
                .getAllByOwner(createdUser2.getId(), State.ALL, 0, 2);

        Assertions.assertEquals(2, bookingOutputDtoList.size());
        Assertions.assertEquals(1L, bookingOutputDtoList.get(0).getId());
        Assertions.assertEquals(1L, bookingOutputDtoList.get(0).getBooker().getId());
        Assertions.assertEquals(2L, bookingOutputDtoList.get(0).getItem().getId());
        Assertions.assertEquals(Status.APPROVED.name(), bookingOutputDtoList.get(0).getStatus());
    }

    @Test
    void getAllFutureBookingsByBookerTest() {
        UserDto createdUser1 = userService.create(userDto1);
        ItemDto createdItem1 = itemService.create(createdUser1.getId(), itemDto1);
        UserDto createdUser2 = userService.create(userDto2);
        ItemDto createdItem2 = itemService.create(createdUser2.getId(), itemDto2);
        BookingOutDto bookingOutDto = bookingService.create(createdUser1.getId(), bookingInDto1);

        Assertions.assertEquals(1L, createdItem1.getId());
        Assertions.assertEquals(2L, createdItem2.getId());
        Assertions.assertEquals(1L, bookingOutDto.getId());
        Assertions.assertEquals(1L, bookingOutDto.getBooker().getId());
        Assertions.assertEquals(2L, bookingOutDto.getItem().getId());
        Assertions.assertEquals(Status.WAITING.name(), bookingOutDto.getStatus());

        BookingOutDto approveOutputDto = bookingService
                .approveOrReject(createdUser2.getId(), bookingOutDto.getId(), true);

        Assertions.assertEquals(1L, approveOutputDto.getId());
        Assertions.assertEquals(1L, approveOutputDto.getBooker().getId());
        Assertions.assertEquals(2L, approveOutputDto.getItem().getId());
        Assertions.assertEquals(Status.APPROVED.name(), approveOutputDto.getStatus());

        List<BookingOutDto> bookingOutputDtoList = bookingService
                .getAllByBooker(createdUser1.getId(), State.FUTURE, 0, 1);

        Assertions.assertEquals(1, bookingOutputDtoList.size());

        Assertions.assertEquals(1L, bookingOutputDtoList.get(0).getId());
        Assertions.assertEquals(1L, bookingOutputDtoList.get(0).getBooker().getId());
        Assertions.assertEquals(2L, bookingOutputDtoList.get(0).getItem().getId());
        Assertions.assertEquals(Status.APPROVED.name(), bookingOutputDtoList.get(0).getStatus());
    }

    @Test
    void approveByOwnerWrongBookingIdTest() {
        Long id = 2L;

        Assertions
                .assertThrows(NotFoundException.class, () -> bookingService.approveOrReject(id, id, true));
    }

    @Test
    void getBookingByIdWithWrongBookingIdTest() {
        Long id = 2L;

        Assertions
                .assertThrows(NotFoundException.class, () -> bookingService.getById(id, id));
    }
}