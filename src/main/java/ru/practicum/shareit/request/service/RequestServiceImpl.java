package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dao.RequestStorage;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.model.User;


import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.request.dto.ItemRequestMapper.toRequest;
import static ru.practicum.shareit.request.dto.ItemRequestMapper.toRequestDto;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestStorage requestStorage;
    private final UserStorage userStorage;

    @Override
    @Transactional
    public ItemRequestDto create(Long userId, ItemRequestDto requestDto) {
        validateUserByIdOrException(userId);
        ItemRequest request = toRequest(requestDto, userId);
        return toRequestDto(requestStorage.save(request));
    }

    @Override
    public List<ItemRequestDto> getAllUserRequests(Long userId) {
        validateUserByIdOrException(userId);
        return mapToDto(requestStorage.findAllByRequesterIdOrderByCreatedAsc(userId));
    }

    @Override
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        validateUserByIdOrException(userId);
        return toRequestDto(requestStorage.findById(requestId).orElseThrow(() ->
                new NotFoundException("Request id=" + requestId + " not found!")));
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId, Integer from, Integer size) {
        validateUserByIdOrException(userId);
        return mapToDto(requestStorage
                .findAllByRequesterIdNotOrderByCreatedAsc(userId, PageRequest.of(from / size, size)));
    }

    private List<ItemRequestDto> mapToDto(List<ItemRequest> requests) {
        return requests.stream()
                .map(ItemRequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    private User validateUserByIdOrException(Long userId) {
        return userStorage.findById(userId).orElseThrow(() ->
                new NotFoundException("User id=" + userId + " not found!"));
    }
}
