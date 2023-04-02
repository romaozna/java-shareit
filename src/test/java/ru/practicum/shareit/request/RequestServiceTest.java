package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dao.RequestStorage;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.RequestServiceImpl;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RequestServiceTest {

    @InjectMocks
    private RequestServiceImpl requestService;

    @Mock
    private RequestStorage requestStorage;

    @Mock
    private UserStorage userStorage;

    private final UserDto userDto = new UserDto(
            1L,
            "Roman",
            "roman@mail.com");

    private final User user = new User(
            1L,
            "Roman",
            "roman@mail.com");

    private final LocalDateTime time = LocalDateTime.now();

    private final ItemRequestDto requestDto = new ItemRequestDto(
            1L,
            "request",
            time,
            new ArrayList<>());

    private final ItemRequest request = new ItemRequest(
            1L,
            "request",
            1L,
            time,
            new ArrayList<>());

    @Test
    void createRequest() {
        when(requestStorage.save(any(ItemRequest.class))).thenReturn(request);
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));

        ItemRequestDto createdRequest = requestService.create(userDto.getId(),requestDto);

        Assertions.assertNotNull(createdRequest);
        Assertions.assertEquals(requestDto.getId(), createdRequest.getId());
        Assertions.assertEquals(requestDto.getDescription(), createdRequest.getDescription());
        Assertions.assertEquals(requestDto.getCreated(), createdRequest.getCreated());
        Assertions.assertEquals(requestDto.getItems().size(), createdRequest.getItems().size());

        verify(requestStorage, times(1)).save(any(ItemRequest.class));
        verifyNoMoreInteractions(requestStorage);
    }

    @Test
    void getAllOwnRequestsById() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(requestStorage.findAllByRequesterIdOrderByCreatedAsc(anyLong()))
                .thenReturn(List.of(request));

        List<ItemRequestDto> requests = requestService.getAllUserRequests(anyLong());

        Assertions.assertEquals(1, requests.size());
        Assertions.assertEquals(requestDto.getId(), requests.get(0).getId());
        Assertions.assertEquals(requestDto.getDescription(), requests.get(0).getDescription());
        Assertions.assertEquals(requestDto.getCreated(), requests.get(0).getCreated());
        Assertions.assertEquals(requestDto.getItems().size(), requests.get(0).getItems().size());

        verify(requestStorage, times(1))
                .findAllByRequesterIdOrderByCreatedAsc(anyLong());
        verifyNoMoreInteractions(requestStorage);
    }

    @Test
    void getByRequestId() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(requestStorage.findById(anyLong())).thenReturn(Optional.of(request));

        ItemRequestDto foundRequest = requestService.getRequestById(1L, 1L);

        Assertions.assertNotNull(foundRequest);
        Assertions.assertEquals(requestDto.getId(), foundRequest.getId());
        Assertions.assertEquals(requestDto.getDescription(), foundRequest.getDescription());
        Assertions.assertEquals(requestDto.getCreated(), foundRequest.getCreated());
        Assertions.assertEquals(requestDto.getItems().size(), foundRequest.getItems().size());

        verify(requestStorage, times(1)).findById(anyLong());
        verifyNoMoreInteractions(requestStorage);
    }

    @Test
    void getAllRequests() {
        Pageable pageable = PageRequest.of(0, 1);

        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(requestStorage.findAllByRequesterIdNotOrderByCreatedAsc(1L, pageable))
                .thenReturn(List.of(request));

        List<ItemRequestDto> requests = requestService.getAllRequests(1L, 0, 1);

        Assertions.assertEquals(1, requests.size());
        Assertions.assertEquals(requestDto.getId(), requests.get(0).getId());
        Assertions.assertEquals(requestDto.getDescription(), requests.get(0).getDescription());
        Assertions.assertEquals(requestDto.getCreated(), requests.get(0).getCreated());
        Assertions.assertEquals(requestDto.getItems().size(), requests.get(0).getItems().size());

        verify(requestStorage, times(1))
                .findAllByRequesterIdNotOrderByCreatedAsc(anyLong(), any(Pageable.class));
        verifyNoMoreInteractions(requestStorage);
    }
}