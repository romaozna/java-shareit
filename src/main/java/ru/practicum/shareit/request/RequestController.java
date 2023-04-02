package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.RequestService;

import javax.validation.constraints.Min;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
@Validated
public class RequestController {
    private final String userIdFromHeader = "X-Sharer-User-Id";
    private final RequestService requestService;

    @PostMapping
    public ItemRequestDto create(@RequestHeader(userIdFromHeader) Long userId,
                             @Validated({Create.class}) @RequestBody ItemRequestDto requestDto) {
        return requestService.create(userId, requestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getAllRequests(@RequestHeader(userIdFromHeader) Long userId) {
        return requestService.getAllUserRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader(userIdFromHeader) Long userId,
                                                 @PathVariable("requestId") Long requestId) {
        return requestService.getRequestById(userId, requestId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(
            @RequestHeader(userIdFromHeader) Long userId,
            @RequestParam(value = "from", required = false, defaultValue = "0") @Min(0) Integer from,
            @RequestParam(value = "size", required = false, defaultValue = "10") @Min(1) Integer size) {

        return requestService.getAllRequests(userId, from, size);
    }
}