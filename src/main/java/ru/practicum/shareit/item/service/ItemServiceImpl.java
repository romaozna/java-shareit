package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.model.User;


import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto getById(Long itemId) {
        return itemMapper.toItemDto(itemStorage.getById(itemId));
    }

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        User owner = userStorage.getById(userId);
        return itemMapper.toItemDto(itemStorage.save(itemMapper.toItem(itemDto, owner, null)));
    }

    @Override
    public ItemDto update(Long userId, ItemDto itemDto, Long itemId) {
        Item savedItem = itemStorage.getById(itemId);
        User owner = savedItem.getOwner();
        if (!owner.getId().equals(userId)) {
            log.warn("User with id={} can`t edit item with id={}", userId, itemId);
            throw new NotFoundException("User not rights to edit item");
        }
        Item updateItem = itemMapper.toItem(itemDto, owner, null);
        return itemMapper.toItemDto(itemStorage.update(updateItem, itemId));
    }

    @Override
    public List<ItemDto> searchItem(String request) {
        return itemStorage
                .searchItem(request)
                .stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getUserItems(Long userId) {
        return itemStorage
                .getUserItems(userId)
                .stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long itemId) {
        itemStorage.delete(itemId);
    }
}
