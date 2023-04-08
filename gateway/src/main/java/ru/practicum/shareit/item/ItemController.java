package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

import javax.validation.constraints.Min;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    public static final String USER_ID_FROM_HEADER = "X-Sharer-User-Id";
    private final ItemClient itemClient;

    @GetMapping("{id}")
    public ResponseEntity<Object> getById(@RequestHeader(USER_ID_FROM_HEADER) Long userId,
                           @PathVariable Long id) {
        return itemClient.getById(id, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItems(
            @RequestHeader(USER_ID_FROM_HEADER) Long userId,
            @RequestParam(required = false, defaultValue = "0") @Min(0) Integer from,
            @RequestParam(required = false, defaultValue = "10") @Min(1) Integer size) {

        return itemClient.getAllByUserId(userId, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER_ID_FROM_HEADER) Long userId,
                                         @RequestBody @Validated(Create.class) ItemDto itemDto) {
        return itemClient.create(userId, itemDto);
    }

    @PatchMapping("{id}")
    public ResponseEntity<Object> update(@RequestHeader(USER_ID_FROM_HEADER) Long userId,
                          @RequestBody @Validated(Update.class) ItemDto itemDto, @PathVariable Long id) {
        return itemClient.update(userId, id, itemDto);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(
            @RequestHeader(USER_ID_FROM_HEADER) Long userId,
            @RequestParam(name = "text") String request,
            @RequestParam(required = false, defaultValue = "0") @Min(0) Integer from,
            @RequestParam(required = false, defaultValue = "10") @Min(1) Integer size) {

        return itemClient.searchItem(userId, request, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@Validated({Create.class}) @RequestBody CommentDto commentDto,
                                 @RequestHeader(USER_ID_FROM_HEADER) Long userId,
                                 @PathVariable Long itemId) {

        return itemClient.createComment(commentDto, userId, itemId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("{id}")
    public void delete(@PathVariable Long id) {
        itemClient.deleteItem(id);
    }
}
