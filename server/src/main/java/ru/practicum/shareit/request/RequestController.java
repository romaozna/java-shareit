package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.RequestService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class RequestController {
    public static final String USER_ID_FROM_HEADER = "X-Sharer-User-Id";
    private final RequestService requestService;

    @PostMapping
    public ItemRequestDto create(@RequestHeader(USER_ID_FROM_HEADER) Long userId,
                                 @RequestBody ItemRequestDto requestDto) {
        return requestService.create(userId, requestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getAllRequests(@RequestHeader(USER_ID_FROM_HEADER) Long userId) {
        return requestService.getAllUserRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader(USER_ID_FROM_HEADER) Long userId,
                                         @PathVariable Long requestId) {
        return requestService.getRequestById(userId, requestId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(
            @RequestHeader(USER_ID_FROM_HEADER) Long userId,
            @RequestParam(required = false, defaultValue = "0") Integer from,
            @RequestParam(required = false, defaultValue = "10") Integer size) {

        return requestService.getAllRequests(userId, from, size);
    }
}