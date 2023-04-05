package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.BadRequestException;

import javax.validation.constraints.Min;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {
    private final BookingService bookingService;
    public static final String USER_ID_FROM_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public BookingOutDto create(@RequestHeader(USER_ID_FROM_HEADER) Long userId,
                                @Validated({Create.class}) @RequestBody BookingInDto bookingInDto) {
        return bookingService.create(userId, bookingInDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingOutDto approveOrReject(@RequestHeader(USER_ID_FROM_HEADER) Long userId,
                                                    @RequestParam Boolean approved,
                                                    @PathVariable Long bookingId) {
        return bookingService.approveOrReject(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingOutDto getById(@RequestHeader(USER_ID_FROM_HEADER) Long userId,
                                 @PathVariable Long bookingId) {
        return bookingService.getById(bookingId, userId);
    }

    @GetMapping
    public List<BookingOutDto> getAll(
            @RequestHeader(USER_ID_FROM_HEADER) Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(required = false, defaultValue = "0") @Min(0) Integer from,
            @RequestParam(required = false, defaultValue = "10") @Min(1) Integer size) {

        State bookingState = State.from(state);
        if (bookingState == null) {
            throw new BadRequestException("Unknown state: " + state);
        }
        return bookingService.getAllByBooker(userId, bookingState, from, size);
    }

    @GetMapping("/owner")
    public List<BookingOutDto> getAllByOwner(
            @RequestHeader(USER_ID_FROM_HEADER) Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(required = false, defaultValue = "0") @Min(0) Integer from,
            @RequestParam(required = false, defaultValue = "10") @Min(1) Integer size) {

        State bookingState = State.from(state);
        if (bookingState == null) {
            throw new BadRequestException("Unknown state: " + state);
        }
        return bookingService.getAllByOwner(userId, bookingState, from, size);
    }
}
