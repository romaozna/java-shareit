package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import java.time.temporal.ChronoUnit;
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
    public List<BookingOutDto> getAllByBooker(Long userId, State state, Integer from, Integer size) {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);;
        validateUserByIdOrException(userId);
        Pageable pageable = PageRequest.of(from / size, size);

        switch (state) {

            case CURRENT:
                return mapToDto(bookingStorage.getCurrentBookingsByBooker(userId, now, pageable));

            case PAST:
                return mapToDto(bookingStorage.getPastBookingsByBooker(userId, now, pageable));

            case FUTURE:
                return mapToDto(bookingStorage.getFutureBookingsByBooker(userId, now, pageable));

            case WAITING:
                return mapToDto(bookingStorage.getWaitingBookingsByBooker(userId, now, pageable));

            case REJECTED:
                return mapToDto(bookingStorage.getRejectedBookingsByBooker(userId, pageable));

            default:
                return mapToDto(bookingStorage.getBookingsByBookerIdOrderByStartDesc(userId, pageable));
        }
    }

    @Override
    public List<BookingOutDto> getAllByOwner(Long userId, State state, Integer from, Integer size) {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);;
        validateUserByIdOrException(userId);
        Pageable pageable = PageRequest.of(from / size, size);

        switch (state) {

            case CURRENT:
                return mapToDto(bookingStorage.getCurrentBookingsByOwner(userId, now, pageable));

            case PAST:
                return mapToDto(bookingStorage.getPastBookingsByOwner(userId, now, pageable));

            case FUTURE:
                return mapToDto(bookingStorage.getFutureBookingsByOwner(userId, now, pageable));

            case WAITING:
                return mapToDto(bookingStorage.getWaitingBookingsByOwner(userId, now, pageable));

            case REJECTED:
                return mapToDto(bookingStorage.getRejectedBookingsByOwner(userId, pageable));

            default:
                return mapToDto(bookingStorage.getAllBookingsByOwner(userId, pageable));
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

    private List<BookingOutDto> mapToDto(List<Booking> bookings) {
        return bookings.stream()
                .map(BookingMapper::toBookingOutDto)
                .collect(Collectors.toList());
    }
}
