package ru.practicum.shareit.request.dao;

import ru.practicum.shareit.request.model.ItemRequest;

public interface ItemRequestStorage {
    ItemRequest getById(Long requestId);
}
