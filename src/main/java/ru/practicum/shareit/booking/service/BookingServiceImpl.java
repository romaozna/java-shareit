package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dao.BookingStorage;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.dto.BookingMapper.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingStorage bookingStorage;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    @Override
    @Transactional
    public BookingOutDto create(Long userId, BookingInDto bookingInDto) {
        User user =  validateUserByIdOrException(userId);
        Item item = validateItemByIdOrException(bookingInDto.getItemId());

        LocalDateTime start = bookingInDto.getStart();
        LocalDateTime end = bookingInDto.getEnd();

        if (item.getOwner().equals(user)) {
            throw new NotFoundException("Item id=" + item.getId() + " not available for booking");
        }
        if (!item.getAvailable()) {
            throw new BadRequestException("Item id=" + item.getId() + " not available for booking");
        }
        if (start.isAfter(end)) {
            throw new BadRequestException("Start time later than the end time");
        }
        if (start.isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Start time earlier than the current time");
        }
        if (start.isEqual(end)) {
            throw new BadRequestException("Start time must be no equal end time");
        }

        return toBookingOutDto(bookingStorage.save(toBooking(bookingInDto, item, user, Status.WAITING)));
    }

    @Override
    @Transactional
    public BookingOutDto approveOrReject(Long userId, Long bookingId, Boolean approved) {
        Booking booking = validateBookingByIdOrException(bookingId);

        if (!Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            throw new NotFoundException("User is not the owner of the item");
        }

        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new BadRequestException("Booking is already approved");
        }

        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        return toBookingOutDto(bookingStorage.save(booking));
    }

    @Override
    public BookingOutDto getById(Long bookingId, Long userId) {
        Booking booking = validateBookingByIdOrException(bookingId);

        if (!Objects.equals(booking.getBooker().getId(), userId)
                && !Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            throw new NotFoundException("Booking not found");
        }

        return toBookingOutDto(booking);
    }

    @Override
    public List<BookingOutDto> getAllByBooker(Long userId, State state) {
        LocalDateTime now = LocalDateTime.now();
        validateUserByIdOrException(userId);

        switch (state) {

            case CURRENT:
                return bookingStorage.getCurrentBookingsByBooker(userId, now).stream()
                        .map(BookingMapper::toBookingOutDto)
                        .collect(Collectors.toList());
            case PAST:
                return bookingStorage.getPastBookingsByBooker(userId, now).stream()
                        .map(BookingMapper::toBookingOutDto)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingStorage.getFutureBookingsByBooker(userId, now).stream()
                        .map(BookingMapper::toBookingOutDto)
                        .collect(Collectors.toList());
            case WAITING:
                return bookingStorage.getWaitingBookingsByBooker(userId, now).stream()
                        .map(BookingMapper::toBookingOutDto)
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingStorage.getRejectedBookingsByBooker(userId).stream()
                        .map(BookingMapper::toBookingOutDto)
                        .collect(Collectors.toList());
            default:
                return bookingStorage.getBookingsByBookerIdOrderByStartDesc(userId).stream()
                        .map(BookingMapper::toBookingOutDto)
                        .collect(Collectors.toList());
        }
    }

    @Override
    public List<BookingOutDto> getAllByOwner(Long userId, State state) {
        LocalDateTime now = LocalDateTime.now();
        validateUserByIdOrException(userId);

        switch (state) {

            case CURRENT:
                return bookingStorage.getCurrentBookingsByOwner(userId, now).stream()
                        .map(BookingMapper::toBookingOutDto)
                        .collect(Collectors.toList());
            case PAST:
                return bookingStorage.getPastBookingsByOwner(userId, now).stream()
                        .map(BookingMapper::toBookingOutDto)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingStorage.getFutureBookingsByOwner(userId, now).stream()
                        .map(BookingMapper::toBookingOutDto)
                        .collect(Collectors.toList());
            case WAITING:
                return bookingStorage.getWaitingBookingsByOwner(userId, now).stream()
                        .map(BookingMapper::toBookingOutDto)
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingStorage.getRejectedBookingsByOwner(userId).stream()
                        .map(BookingMapper::toBookingOutDto)
                        .collect(Collectors.toList());
            default:
                return bookingStorage.getAllBookingsByOwner(userId).stream()
                        .map(BookingMapper::toBookingOutDto)
                        .collect(Collectors.toList());
        }
    }

    private Booking validateBookingByIdOrException(Long bookingId) {
        return bookingStorage.findById(bookingId).orElseThrow(() ->
                new NotFoundException("Booking id=" + bookingId + " not found!"));
    }

    private User validateUserByIdOrException(Long userId) {
        return userStorage.findById(userId).orElseThrow(() ->
                new NotFoundException("User id=" + userId + " not found!"));
    }

    private Item validateItemByIdOrException(Long itemId) {
        return itemStorage.findById(itemId).orElseThrow(() ->
                new NotFoundException("Item id=" + itemId + " not found!"));
    }
}
