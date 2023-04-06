package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.booking.dto.BookingInDto;

import javax.validation.constraints.Min;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {
    private final BookingClient bookingClient;
    public static final String USER_ID_FROM_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER_ID_FROM_HEADER) Long userId,
                                 @Validated({Create.class}) @RequestBody BookingInDto bookingInDto) {
        return bookingClient.create(userId, bookingInDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveOrReject(@RequestHeader(USER_ID_FROM_HEADER) Long userId,
                                                    @RequestParam Boolean approved,
                                                    @PathVariable Long bookingId) {
        return bookingClient.approveOrReject(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getById(@RequestHeader(USER_ID_FROM_HEADER) Long userId,
                                 @PathVariable Long bookingId) {
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll(
            @RequestHeader(USER_ID_FROM_HEADER) Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(required = false, defaultValue = "0") @Min(0) Integer from,
            @RequestParam(required = false, defaultValue = "10") @Min(1) Integer size) {

        State bookingState = State.from(state);
        if (bookingState == null) {
            throw new BadRequestException("Unknown state: " + state);
        }
        return bookingClient.getBookings(userId, bookingState, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllByOwner(
            @RequestHeader(USER_ID_FROM_HEADER) Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(required = false, defaultValue = "0") @Min(0) Integer from,
            @RequestParam(required = false, defaultValue = "10") @Min(1) Integer size) {

        State bookingState = State.from(state);
        if (bookingState == null) {
            throw new BadRequestException("Unknown state: " + state);
        }
        return bookingClient.getAllByOwner(userId, bookingState, from, size);
    }
}
