package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface RequestService {
    ItemRequestDto create(Long userId, ItemRequestDto requestDto);

    List<ItemRequestDto> getAllUserRequests(Long userId);

    ItemRequestDto getRequestById(Long userId, Long requestId);

    List<ItemRequestDto> getAllRequests(Long userId, Integer from, Integer size);
}
