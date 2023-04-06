package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.validation.Create;

import javax.validation.constraints.Min;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
@Validated
public class RequestController {
    private final RequestClient requestClient;
    public static final String USER_ID_FROM_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER_ID_FROM_HEADER) Long userId,
                                         @Validated({Create.class}) @RequestBody ItemRequestDto requestDto) {
        return requestClient.create(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllRequests(@RequestHeader(USER_ID_FROM_HEADER) Long userId) {
        return requestClient.getAllUserRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader(USER_ID_FROM_HEADER) Long userId,
                                                 @PathVariable Long requestId) {
        return requestClient.getRequestById(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(
            @RequestHeader(USER_ID_FROM_HEADER) Long userId,
            @RequestParam(required = false, defaultValue = "0") @Min(0) Integer from,
            @RequestParam(required = false, defaultValue = "10") @Min(1) Integer size) {

        return requestClient.getAllRequests(userId, from, size);
    }
}