package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final String userIdFromHeader = "X-Sharer-User-Id";
    private final ItemService itemService;

    @GetMapping("{id}")
    public ItemDto getById(@RequestHeader(userIdFromHeader) Long userId,
                           @PathVariable Long id) {
        return itemService.getById(userId, id);
    }

    @GetMapping
    public List<ItemDto> getUserItems(@RequestHeader(userIdFromHeader) Long userId) {
        return itemService.getUserItems(userId);
    }

    @PostMapping
    public ItemDto create(@RequestHeader(userIdFromHeader) Long userId,
                          @RequestBody @Validated(Create.class) ItemDto itemDto) {
        return itemService.create(userId, itemDto);
    }

    @PatchMapping("{id}")
    public ItemDto update(@RequestHeader(userIdFromHeader) Long userId,
                          @RequestBody @Validated(Update.class) ItemDto itemDto, @PathVariable Long id) {
        return itemService.update(userId, itemDto, id);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestHeader(userIdFromHeader) Long userId,
                                    @RequestParam(name = "text") String request) {
        return request.isBlank() ? Collections.emptyList() : itemService.searchItem(userId, request);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@Validated({Create.class}) @RequestBody CommentDto commentDto,
                                 @RequestHeader(userIdFromHeader) Long userId,
                                 @PathVariable Long itemId) {

        return itemService.createComment(commentDto, userId, itemId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("{id}")
    public void delete(@PathVariable Long id) {
        itemService.delete(id);
    }
}
