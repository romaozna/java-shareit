package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.dao.BookingStorage;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
public class BookingServiceImplTest {

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private BookingStorage bookingStorage;

    @Mock
    private UserStorage userStorage;

    @Mock
    private ItemStorage itemStorage;

    private final UserDto userDto = new UserDto(
            1L,
            "Roman",
            "roman@mail.com");

    private final UserDto anotherUserDto = new UserDto(
            2L,
            "Max",
            "max@mail.com");

    private final ItemDto itemDtoId1 = new ItemDto(
            1L,
            "brush",
            "best brush",
            true,
            null,
            null,
            new ArrayList<>(),
            2L);

    private final LocalDateTime start = LocalDateTime.now().plusMinutes(1);
    private final LocalDateTime end = LocalDateTime.now().plusDays(1);

    private final BookingOutDto bookingOutDto = new BookingOutDto(
            1L,
            start,
            end,
            Status.APPROVED.name(),
            userDto,
            itemDtoId1);

    private final BookingOutDto bookingApprovedDto = new BookingOutDto(
            1L,
            start,
            end,
            Status.APPROVED.name(),
            anotherUserDto,
            itemDtoId1);

    private final BookingInDto bookingInDto = new BookingInDto(
            1L,
            start,
            end);
    private final BookingInDto bookingInDtoWithInvalidEnd = new BookingInDto(
            1L,
            start,
            end.minusDays(2));

    private final BookingInDto bookingInDateDtoWithInvalidStart = new BookingInDto(
            1L,
            start.minusDays(2),
            end);

    private final BookingInDto bookingInDtoWithSameStartDate = new BookingInDto(
            1L,
            start,
            start);

    private final User user = new User(
            1L,
            "Roman",
            "roman@mail.com");

    private final User anotherUser = new User(
            2L,
            "Max",
            "max@mail.com");

    private final Item item = new Item(
            1L,
            "brush",
            "best brush",
            true,
            anotherUser,
            null);

    private final Item itemUnavailable = new Item(
            1L,
            "brush",
            "best brush",
            false,
            anotherUser,
            null);

    private final Booking booking = new Booking(
            1L,
            start,
            end,
            item,
            user,
            Status.APPROVED);

    private final Booking bookingWaiting = new Booking(
            1L,
            start,
            end,
            item,
            anotherUser,
            Status.WAITING);

    @Test
    void createBookingTest() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingStorage.save(any(Booking.class))).thenReturn(booking);

        BookingOutDto newBooking = bookingService.create(userDto.getId(), bookingInDto);

        Assertions.assertNotNull(newBooking);
        Assertions.assertEquals(bookingOutDto.getId(), newBooking.getId());
        Assertions.assertEquals(bookingOutDto.getStart(), newBooking.getStart());
        Assertions.assertEquals(bookingOutDto.getEnd(), newBooking.getEnd());
        Assertions.assertEquals(bookingOutDto.getItem().getId(), newBooking.getItem().getId());
        Assertions.assertEquals(bookingOutDto.getBooker().getId(), newBooking.getBooker().getId());
        Assertions.assertEquals(bookingOutDto.getStatus(), newBooking.getStatus());
    }

    @Test
    void createBookingUnavailableTest() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(itemUnavailable));
        Assertions.assertThrows(BadRequestException.class,
                () -> bookingService.create(userDto.getId(), bookingInDto));
    }

    @Test
    void createBookingWrongEndDateTest() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item));
        Assertions.assertThrows(BadRequestException.class,
                () -> bookingService.create(userDto.getId(), bookingInDtoWithInvalidEnd));
    }

    @Test
    void createBookingWrongStartDateTest() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item));
        Assertions.assertThrows(BadRequestException.class,
                () -> bookingService.create(userDto.getId(), bookingInDateDtoWithInvalidStart));
    }

    @Test
    void createBookingEqualDatesTest() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item));
        Assertions.assertThrows(BadRequestException.class,
                () -> bookingService.create(userDto.getId(), bookingInDtoWithSameStartDate));
    }

    @Test
    void approvedByOwnerTest() {
        when(bookingStorage.save(any(Booking.class))).thenReturn(bookingWaiting);
        when(bookingStorage.findById(anyLong())).thenReturn(Optional.of(bookingWaiting));

        BookingOutDto approvedBooking = bookingService.approveOrReject(2L, 1L, true);

        Assertions.assertNotNull(approvedBooking);
        Assertions.assertEquals(bookingApprovedDto.getId(), approvedBooking.getId());
        Assertions.assertEquals(bookingApprovedDto.getStart(), approvedBooking.getStart());
        Assertions.assertEquals(bookingApprovedDto.getEnd(), approvedBooking.getEnd());
        Assertions.assertEquals(bookingApprovedDto.getItem().getId(), approvedBooking.getItem().getId());
        Assertions.assertEquals(bookingApprovedDto.getBooker().getId(), approvedBooking.getBooker().getId());
        Assertions.assertEquals(bookingApprovedDto.getStatus(), approvedBooking.getStatus());
    }

    @Test
    void approvedByOwnerBookingWithStatusApprovedTest() {
        when(bookingStorage.findById(anyLong())).thenReturn(Optional.of(booking));

        Assertions.assertThrows(BadRequestException.class,
                () -> bookingService.approveOrReject(2L, 1L, true));
    }

    @Test
    void approvedByOwnerWithoutItemTest() {
        when(bookingStorage.findById(anyLong())).thenReturn(Optional.of(booking));

        Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.approveOrReject(1L, 1L, true));
    }

    @Test
    void getBookingByIdTest() {
        when(bookingStorage.findById(anyLong())).thenReturn(Optional.of(booking));

        BookingOutDto bookingOutput = bookingService.getById(1L, 1L);

        Assertions.assertNotNull(bookingOutput);
        Assertions.assertEquals(bookingOutDto.getId(), bookingOutput.getId());
        Assertions.assertEquals(bookingOutDto.getStart(), bookingOutput.getStart());
        Assertions.assertEquals(bookingOutDto.getEnd(), bookingOutput.getEnd());
        Assertions.assertEquals(bookingOutDto.getItem().getId(), bookingOutput.getItem().getId());
        Assertions.assertEquals(bookingOutDto.getBooker().getId(), bookingOutput.getBooker().getId());
        Assertions.assertEquals(bookingOutDto.getStatus(), bookingOutput.getStatus());
    }

    @Test
    void getBookingByInvalidIdTest() {
        Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.getById(99L, 2L));
    }

    @Test
    void findAllByBookerTest() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingStorage.getBookingsByBookerIdOrderByStartDesc(1L, PageRequest.of(0, 1)))
                .thenReturn(List.of(booking));

        List<BookingOutDto> bookings = bookingService.getAllByBooker(1L, State.ALL, 0, 1);

        Assertions.assertEquals(1, bookings.size());
    }

    @Test
    void findAllCurrentByBookerTest() {
        LocalDateTime currentTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingStorage.getCurrentBookingsByBooker(1L, currentTime, PageRequest.of(0, 1)))
                .thenReturn(List.of(booking));

        List<BookingOutDto> bookingsCurrent = bookingService.getAllByBooker(1L, State.CURRENT, 0, 1);

        Assertions.assertEquals(1, bookingsCurrent.size());
    }

    @Test
    void findAllWaitingByBookerTest() {
        LocalDateTime currentTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingStorage.getWaitingBookingsByBooker(1L, currentTime, PageRequest.of(0, 1)))
                .thenReturn(List.of(booking));

        List<BookingOutDto> bookingsCurrent = bookingService.getAllByBooker(1L, State.WAITING, 0, 1);

        Assertions.assertEquals(1, bookingsCurrent.size());
    }

    @Test
    void findAllPastByBookerTest() {
        LocalDateTime currentTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingStorage.getPastBookingsByBooker(1L, currentTime, PageRequest.of(0, 1)))
                .thenReturn(List.of(booking));

        List<BookingOutDto> bookingsCurrent = bookingService.getAllByBooker(1L, State.PAST, 0, 1);

        Assertions.assertEquals(1, bookingsCurrent.size());
    }

    @Test
    void findAllFutureByBookerTest() {
        LocalDateTime currentTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingStorage.getFutureBookingsByBooker(1L, currentTime, PageRequest.of(0, 1)))
                .thenReturn(List.of(booking));

        List<BookingOutDto> bookingsCurrent = bookingService.getAllByBooker(1L, State.FUTURE, 0, 1);

        Assertions.assertEquals(1, bookingsCurrent.size());
    }

    @Test
    void findAllRejectedByBookerTest() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingStorage.getRejectedBookingsByBooker(1L, PageRequest.of(0, 1)))
                .thenReturn(List.of(booking));

        List<BookingOutDto> bookingsCurrent = bookingService.getAllByBooker(1L, State.REJECTED, 0, 1);

        Assertions.assertEquals(1, bookingsCurrent.size());
    }

    @Test
    void findAllByOwnerTest() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingStorage.getAllBookingsByOwner(1L, PageRequest.of(0, 1)))
                .thenReturn(List.of(booking));

        List<BookingOutDto> bookings = bookingService.getAllByOwner(1L, State.ALL, 0, 1);

        Assertions.assertEquals(1, bookings.size());
    }

    @Test
    void findAllCurrentByOwnerTest() {
        LocalDateTime currentTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingStorage.getCurrentBookingsByOwner(1L, currentTime, PageRequest.of(0, 1)))
                .thenReturn(List.of(booking));

        List<BookingOutDto> bookingsCurrent = bookingService.getAllByOwner(1L, State.CURRENT, 0, 1);

        Assertions.assertEquals(1, bookingsCurrent.size());
    }

    @Test
    void findAllWaitingByOwnerTest() {
        LocalDateTime currentTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingStorage.getWaitingBookingsByOwner(1L, currentTime, PageRequest.of(0, 1)))
                .thenReturn(List.of(booking));

        List<BookingOutDto> bookingsCurrent = bookingService.getAllByOwner(1L, State.WAITING, 0, 1);

        Assertions.assertEquals(1, bookingsCurrent.size());
    }

    @Test
    void findAllPastByOwnerTest() {
        LocalDateTime currentTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingStorage.getPastBookingsByOwner(1L, currentTime, PageRequest.of(0, 1)))
                .thenReturn(List.of(booking));

        List<BookingOutDto> bookingsCurrent = bookingService.getAllByOwner(1L, State.PAST, 0, 1);

        Assertions.assertEquals(1, bookingsCurrent.size());
    }

    @Test
    void findAllFutureByOwnerTest() {
        LocalDateTime currentTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingStorage.getFutureBookingsByOwner(1L, currentTime, PageRequest.of(0, 1)))
                .thenReturn(List.of(booking));

        List<BookingOutDto> bookingsCurrent = bookingService.getAllByOwner(1L, State.FUTURE, 0, 1);

        Assertions.assertEquals(1, bookingsCurrent.size());
    }

    @Test
    void findAllRejectedByOwnerTest() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingStorage.getRejectedBookingsByOwner(1L, PageRequest.of(0, 1)))
                .thenReturn(List.of(booking));

        List<BookingOutDto> bookingsCurrent = bookingService.getAllByOwner(1L, State.REJECTED, 0, 1);

        Assertions.assertEquals(1, bookingsCurrent.size());
    }
}