package ru.practicum.shareit.request.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemRequestMapper {
    public static ItemRequest toRequest(ItemRequestDto requestDto, Long userId) {
        return new ItemRequest(requestDto.getId(), requestDto.getDescription(),
                userId, requestDto.getCreated(), new ArrayList<>());
    }

    public static ItemRequestDto toRequestDto(ItemRequest request) {
        List<ItemDto> itemDtoList = request
                .getItems()
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());

        return new ItemRequestDto(
                request.getId(),
                request.getDescription(),
                request.getCreated(),
                itemDtoList);
    }
}
