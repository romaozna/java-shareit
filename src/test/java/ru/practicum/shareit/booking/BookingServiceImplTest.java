package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
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
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    private UserDto userDto;
    private BookingOutDto bookingOutDto;
    private BookingOutDto bookingApprovedDto;
    private BookingInDto bookingInDto;
    private BookingInDto bookingInDtoWithInvalidEnd;
    private BookingInDto bookingInDateDtoWithInvalidStart;
    private BookingInDto bookingInDtoWithSameStartDate;
    private User user;
    private Item item;
    private Item itemUnavailable;
    private Booking booking;
    private Booking bookingWaiting;
    private LocalDateTime currentTime;

    @BeforeEach
    public void initVarsForTests() {
        currentTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        LocalDateTime start = currentTime.plusMinutes(1);

        LocalDateTime end = currentTime.plusDays(1);

        User anotherUser = new User(
                2L,
                "Max",
                "max@mail.com");

        UserDto anotherUserDto = new UserDto(
                2L,
                "Max",
                "max@mail.com");

        ItemDto itemDtoId1 = new ItemDto(
                1L,
                "brush",
                "best brush",
                true,
                null,
                null,
                new ArrayList<>(),
                2L);

        userDto = new UserDto(
                1L,
                "Roman",
                "roman@mail.com");

        bookingOutDto = new BookingOutDto(
                1L,
                start,
                end,
                Status.APPROVED.name(),
                userDto,
                itemDtoId1);

        bookingApprovedDto = new BookingOutDto(
                1L,
                start,
                end,
                Status.APPROVED.name(),
                anotherUserDto,
                itemDtoId1);

        bookingInDto = new BookingInDto(
                1L,
                start,
                end);

        bookingInDtoWithInvalidEnd = new BookingInDto(
                1L,
                start,
                end.minusDays(2));

        bookingInDateDtoWithInvalidStart = new BookingInDto(
                1L,
                start.minusDays(2),
                end);

        bookingInDtoWithSameStartDate = new BookingInDto(
                1L,
                start,
                start);

        user = new User(
                1L,
                "Roman",
                "roman@mail.com");

        item = new Item(
                1L,
                "brush",
                "best brush",
                true,
                anotherUser,
                null);

        itemUnavailable = new Item(
                1L,
                "brush",
                "best brush",
                false,
                anotherUser,
                null);

        booking = new Booking(
                1L,
                start,
                end,
                item,
                user,
                Status.APPROVED);

        bookingWaiting = new Booking(
                1L,
                start,
                end,
                item,
                anotherUser,
                Status.WAITING);
    }

    @Test
    void createBookingTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

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
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(itemUnavailable));
        Assertions.assertThrows(BadRequestException.class,
                () -> bookingService.create(userDto.getId(), bookingInDto));
    }

    @Test
    void createBookingWrongEndDateTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        Assertions.assertThrows(BadRequestException.class,
                () -> bookingService.create(userDto.getId(), bookingInDtoWithInvalidEnd));
    }

    @Test
    void createBookingWrongStartDateTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        Assertions.assertThrows(BadRequestException.class,
                () -> bookingService.create(userDto.getId(), bookingInDateDtoWithInvalidStart));
    }

    @Test
    void createBookingEqualDatesTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        Assertions.assertThrows(BadRequestException.class,
                () -> bookingService.create(userDto.getId(), bookingInDtoWithSameStartDate));
    }

    @Test
    void approvedByOwnerTest() {
        when(bookingRepository.save(any(Booking.class))).thenReturn(bookingWaiting);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(bookingWaiting));

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
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        Assertions.assertThrows(BadRequestException.class,
                () -> bookingService.approveOrReject(2L, 1L, true));
    }

    @Test
    void approvedByOwnerWithoutItemTest() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.approveOrReject(1L, 1L, true));
    }

    @Test
    void getBookingByIdTest() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

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
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.getBookingsByBookerIdOrderByStartDesc(1L, PageRequest.of(0, 1)))
                .thenReturn(List.of(booking));

        List<BookingOutDto> bookings = bookingService.getAllByBooker(1L, State.ALL, 0, 1);

        Assertions.assertEquals(1, bookings.size());
    }

    @Test
    void findAllCurrentByBookerTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.getCurrentBookingsByBooker(1L, currentTime, PageRequest.of(0, 1)))
                .thenReturn(List.of(booking));

        List<BookingOutDto> bookingsCurrent = bookingService.getAllByBooker(1L, State.CURRENT, 0, 1);

        Assertions.assertEquals(1, bookingsCurrent.size());
    }

    @Test
    void findAllWaitingByBookerTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.getWaitingBookingsByBooker(1L, currentTime, PageRequest.of(0, 1)))
                .thenReturn(List.of(booking));

        List<BookingOutDto> bookingsCurrent = bookingService.getAllByBooker(1L, State.WAITING, 0, 1);

        Assertions.assertEquals(1, bookingsCurrent.size());
    }

    @Test
    void findAllPastByBookerTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.getPastBookingsByBooker(1L, currentTime, PageRequest.of(0, 1)))
                .thenReturn(List.of(booking));

        List<BookingOutDto> bookingsCurrent = bookingService.getAllByBooker(1L, State.PAST, 0, 1);

        Assertions.assertEquals(1, bookingsCurrent.size());
    }

    @Test
    void findAllFutureByBookerTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.getFutureBookingsByBooker(1L, currentTime, PageRequest.of(0, 1)))
                .thenReturn(List.of(booking));

        List<BookingOutDto> bookingsCurrent = bookingService.getAllByBooker(1L, State.FUTURE, 0, 1);

        Assertions.assertEquals(1, bookingsCurrent.size());
    }

    @Test
    void findAllRejectedByBookerTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.getRejectedBookingsByBooker(1L, PageRequest.of(0, 1)))
                .thenReturn(List.of(booking));

        List<BookingOutDto> bookingsCurrent = bookingService.getAllByBooker(1L, State.REJECTED, 0, 1);

        Assertions.assertEquals(1, bookingsCurrent.size());
    }

    @Test
    void findAllByOwnerTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.getAllBookingsByOwner(1L, PageRequest.of(0, 1)))
                .thenReturn(List.of(booking));

        List<BookingOutDto> bookings = bookingService.getAllByOwner(1L, State.ALL, 0, 1);

        Assertions.assertEquals(1, bookings.size());
    }

    @Test
    void findAllCurrentByOwnerTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.getCurrentBookingsByOwner(1L, currentTime, PageRequest.of(0, 1)))
                .thenReturn(List.of(booking));

        List<BookingOutDto> bookingsCurrent = bookingService.getAllByOwner(1L, State.CURRENT, 0, 1);

        Assertions.assertEquals(1, bookingsCurrent.size());
    }

    @Test
    void findAllWaitingByOwnerTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.getWaitingBookingsByOwner(1L, currentTime, PageRequest.of(0, 1)))
                .thenReturn(List.of(booking));

        List<BookingOutDto> bookingsCurrent = bookingService.getAllByOwner(1L, State.WAITING, 0, 1);

        Assertions.assertEquals(1, bookingsCurrent.size());
    }

    @Test
    void findAllPastByOwnerTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.getPastBookingsByOwner(1L, currentTime, PageRequest.of(0, 1)))
                .thenReturn(List.of(booking));

        List<BookingOutDto> bookingsCurrent = bookingService.getAllByOwner(1L, State.PAST, 0, 1);

        Assertions.assertEquals(1, bookingsCurrent.size());
    }

    @Test
    void findAllFutureByOwnerTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.getFutureBookingsByOwner(1L, currentTime, PageRequest.of(0, 1)))
                .thenReturn(List.of(booking));

        List<BookingOutDto> bookingsCurrent = bookingService.getAllByOwner(1L, State.FUTURE, 0, 1);

        Assertions.assertEquals(1, bookingsCurrent.size());
    }

    @Test
    void findAllRejectedByOwnerTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.getRejectedBookingsByOwner(1L, PageRequest.of(0, 1)))
                .thenReturn(List.of(booking));

        List<BookingOutDto> bookingsCurrent = bookingService.getAllByOwner(1L, State.REJECTED, 0, 1);

        Assertions.assertEquals(1, bookingsCurrent.size());
    }
}