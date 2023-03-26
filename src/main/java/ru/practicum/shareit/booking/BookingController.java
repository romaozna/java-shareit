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

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;
    private final String userIdFromHeader = "X-Sharer-User-Id";

    @PostMapping
    public BookingOutDto create(@RequestHeader(userIdFromHeader) Long userId,
                                @Validated({Create.class}) @RequestBody BookingInDto bookingInDto) {
        return bookingService.create(userId, bookingInDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingOutDto approveOrReject(@RequestHeader(userIdFromHeader) Long userId,
                                                    @RequestParam("approved") Boolean approved,
                                                    @PathVariable("bookingId") Long bookingId) {
        return bookingService.approveOrReject(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingOutDto getById(@RequestHeader(userIdFromHeader) Long userId,
                                 @PathVariable("bookingId") Long bookingId) {
        return bookingService.getById(bookingId, userId);
    }

    @GetMapping
    public List<BookingOutDto> getAll(@RequestHeader(userIdFromHeader) Long userId,
                                      @RequestParam(name = "state", defaultValue = "ALL") String state) {
        State bookingState = State.from(state);
        if (bookingState == null) {
            throw new BadRequestException("Unknown state: " + state);
        }
        return bookingService.getAllByBooker(userId, bookingState);
    }

    @GetMapping("/owner")
    public List<BookingOutDto> getAllByOwner(@RequestHeader(userIdFromHeader) Long userId,
                                             @RequestParam(name = "state", defaultValue = "ALL") String state) {
        State bookingState = State.from(state);
        if (bookingState == null) {
            throw new BadRequestException("Unknown state: " + state);
        }
        return bookingService.getAllByOwner(userId, bookingState);
    }
}
