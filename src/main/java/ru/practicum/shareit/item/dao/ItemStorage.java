package ru.practicum.shareit.item.dao;



import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item getById(Long itemId);

    Item save(Item item);

    Item update(Item item, Long itemId);

    List<Item> searchItem(String request);

    List<Item> getUserItems(Long userId);

    void delete(Long itemId);
}
