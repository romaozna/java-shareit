package ru.practicum.shareit.item.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ItemStorageImpl implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();
    private Long itemCounter = 0L;

    @Override
    public Item getById(Long itemId) {
        Item savedItem = items.get(itemId);
        if (savedItem != null) {
            return savedItem;
        } else {
            log.error("ItemId not found {} ", itemId);
            throw new NotFoundException("Item with id: " + itemId + " not found");
        }
    }

    @Override
    public Item save(Item item) {
        item.setId(++itemCounter);
        items.put(itemCounter, item);
        log.info("Item with id {} saved", itemCounter);
        return items.get(itemCounter);
    }

    @Override
    public Item update(Item item, Long itemId) {
        Item savedItem = getById(itemId);
        String name = item.getName();
        String description = item.getDescription();
        Boolean availableStatus = item.getAvailable();
        if (name != null && !(name.isBlank())) {
            log.info("Item with id: {} updated name {}", itemId, name);
            savedItem.setName(name);
        }
        if (description != null && !(description.isBlank())) {
            log.info("Item with id: {} updated description {}", itemId, description);
            savedItem.setDescription(description);
        }
        if (availableStatus != null) {
            log.info("Item with id: {} updated available status {}", itemId, availableStatus);
            savedItem.setAvailable(availableStatus);
        }
        log.info("Item with id: {} updated", itemId);
        return savedItem;
    }

    @Override
    public List<Item> searchItem(String request) {
        Predicate<Item> namePredicate = item -> item
                .getName()
                .toLowerCase()
                .contains(request.toLowerCase());
        Predicate<Item> descriptionPredicate = item -> item
                .getDescription()
                .toLowerCase()
                .contains(request.toLowerCase());
        return items
                .values()
                .stream()
                .filter(Item::getAvailable)
                .filter(item -> namePredicate.test(item) || descriptionPredicate.test(item)).collect(Collectors.toList());
    }

    @Override
    public List<Item> getUserItems(Long userId) {
        return items
                .values()
                .stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long itemId) {
        getById(itemId);
        items.remove(itemId);
    }
}
