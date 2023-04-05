package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dao.RequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.RequestServiceImpl;
import ru.practicum.shareit.user.dao.UserRepository;
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
    private RequestRepository requestRepository;

    @Mock
    private UserRepository userRepository;

    private UserDto userDto;
    private User user;
    private ItemRequestDto requestDto;
    private ItemRequest request;

    @BeforeEach
    public void initVarsForTests() {
        LocalDateTime now = LocalDateTime.now();

        userDto = new UserDto(
                1L,
                "Roman",
                "roman@mail.com");

        user = new User(
                1L,
                "Roman",
                "roman@mail.com");

        requestDto = new ItemRequestDto(
                1L,
                "request",
                now,
                new ArrayList<>());

        request = new ItemRequest(
                1L,
                "request",
                1L,
                now,
                new ArrayList<>());
    }

    @Test
    void createRequestTest() {
        when(requestRepository.save(any(ItemRequest.class))).thenReturn(request);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        ItemRequestDto createdRequest = requestService.create(userDto.getId(),requestDto);

        Assertions.assertNotNull(createdRequest);
        Assertions.assertEquals(requestDto.getId(), createdRequest.getId());
        Assertions.assertEquals(requestDto.getDescription(), createdRequest.getDescription());
        Assertions.assertEquals(requestDto.getCreated(), createdRequest.getCreated());
        Assertions.assertEquals(requestDto.getItems().size(), createdRequest.getItems().size());

        verify(requestRepository, times(1)).save(any(ItemRequest.class));
        verifyNoMoreInteractions(requestRepository);
    }

    @Test
    void getAllOwnRequestsByIdTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(requestRepository.findAllByRequesterIdOrderByCreatedAsc(anyLong()))
                .thenReturn(List.of(request));

        List<ItemRequestDto> requests = requestService.getAllUserRequests(anyLong());

        Assertions.assertEquals(1, requests.size());
        Assertions.assertEquals(requestDto.getId(), requests.get(0).getId());
        Assertions.assertEquals(requestDto.getDescription(), requests.get(0).getDescription());
        Assertions.assertEquals(requestDto.getCreated(), requests.get(0).getCreated());
        Assertions.assertEquals(requestDto.getItems().size(), requests.get(0).getItems().size());

        verify(requestRepository, times(1))
                .findAllByRequesterIdOrderByCreatedAsc(anyLong());
        verifyNoMoreInteractions(requestRepository);
    }

    @Test
    void getByRequestIdTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(request));

        ItemRequestDto foundRequest = requestService.getRequestById(1L, 1L);

        Assertions.assertNotNull(foundRequest);
        Assertions.assertEquals(requestDto.getId(), foundRequest.getId());
        Assertions.assertEquals(requestDto.getDescription(), foundRequest.getDescription());
        Assertions.assertEquals(requestDto.getCreated(), foundRequest.getCreated());
        Assertions.assertEquals(requestDto.getItems().size(), foundRequest.getItems().size());

        verify(requestRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(requestRepository);
    }

    @Test
    void getAllRequestsTest() {
        Pageable pageable = PageRequest.of(0, 1);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(requestRepository.findAllByRequesterIdNotOrderByCreatedAsc(1L, pageable))
                .thenReturn(List.of(request));

        List<ItemRequestDto> requests = requestService.getAllRequests(1L, 0, 1);

        Assertions.assertEquals(1, requests.size());
        Assertions.assertEquals(requestDto.getId(), requests.get(0).getId());
        Assertions.assertEquals(requestDto.getDescription(), requests.get(0).getDescription());
        Assertions.assertEquals(requestDto.getCreated(), requests.get(0).getCreated());
        Assertions.assertEquals(requestDto.getItems().size(), requests.get(0).getItems().size());

        verify(requestRepository, times(1))
                .findAllByRequesterIdNotOrderByCreatedAsc(anyLong(), any(Pageable.class));
        verifyNoMoreInteractions(requestRepository);
    }
}