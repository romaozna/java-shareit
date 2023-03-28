package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;


public interface ItemService {
    ItemDto getById(Long userId, Long itemId);

    ItemDto create(Long userId, ItemDto itemDto);

    ItemDto update(Long userId, ItemDto itemDto, Long itemId);

    List<ItemDto> searchItem(Long userId, String request);

    List<ItemDto> getUserItems(Long userId);

    void delete(Long itemId);

    CommentDto createComment(CommentDto commentDto, Long userId, Long itemId);
}
