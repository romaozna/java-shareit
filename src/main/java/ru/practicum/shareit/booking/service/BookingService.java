package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.model.State;

import java.util.List;

public interface BookingService {

    BookingOutDto create(Long userId, BookingInDto bookingInDto);

    BookingOutDto approveOrReject(Long userId, Long bookingId, Boolean approved);

    BookingOutDto getById(Long bookingId, Long userId);

    List<BookingOutDto> getAllByBooker(Long userId, State state, Integer from, Integer size);

    List<BookingOutDto> getAllByOwner(Long userId, State state, Integer from, Integer size);
}
