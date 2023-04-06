package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    public static final String USER_ID_FROM_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;

    @GetMapping("{id}")
    public ItemDto getById(@RequestHeader(USER_ID_FROM_HEADER) Long userId,
                           @PathVariable Long id) {
        return itemService.getById(userId, id);
    }

    @GetMapping
    public List<ItemDto> getUserItems(
            @RequestHeader(USER_ID_FROM_HEADER) Long userId,
            @RequestParam(required = false, defaultValue = "0") Integer from,
            @RequestParam(required = false, defaultValue = "10") Integer size) {

        return itemService.getUserItems(userId, from, size);
    }

    @PostMapping
    public ItemDto create(@RequestHeader(USER_ID_FROM_HEADER) Long userId,
                          @RequestBody ItemDto itemDto) {
        return itemService.create(userId, itemDto);
    }

    @PatchMapping("{id}")
    public ItemDto update(@RequestHeader(USER_ID_FROM_HEADER) Long userId,
                          @RequestBody ItemDto itemDto, @PathVariable Long id) {
        return itemService.update(userId, itemDto, id);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(
            @RequestHeader(USER_ID_FROM_HEADER) Long userId,
            @RequestParam(name = "text") String request,
            @RequestParam(required = false, defaultValue = "0") Integer from,
            @RequestParam(required = false, defaultValue = "10") Integer size) {

        return request.isBlank() ? Collections.emptyList() : itemService.searchItem(userId, request, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestBody CommentDto commentDto,
                                    @RequestHeader(USER_ID_FROM_HEADER) Long userId,
                                    @PathVariable Long itemId) {

        return itemService.createComment(commentDto, userId, itemId, LocalDateTime.now());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("{id}")
    public void delete(@PathVariable Long id) {
        itemService.delete(id);
    }
}
