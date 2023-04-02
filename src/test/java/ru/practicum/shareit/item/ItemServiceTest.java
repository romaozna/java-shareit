package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.dao.BookingStorage;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dao.CommentStorage;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private ItemStorage itemStorage;

    @Mock
    private UserStorage userStorage;

    @Mock
    private CommentStorage commentStorage;

    @Mock
    private BookingStorage bookingStorage;

    private final UserDto userDto = new UserDto(
            1L,
            "Roman",
            "roman@mail.com");

    private final User user = new User(
            1L,
            "Roman",
            "roman@mail.com");

    private final Item item = new Item(
            1L,
            "Brush",
            "Best brush",
            true,
            user,
            null);

    private final Comment comment = new Comment(
            1L,
            "Great!",
            item,
            user,
            LocalDateTime.now());

    private final CommentDto commentDto = new CommentDto(
            null,
            "Great!",
            "Roman",
            LocalDateTime.now());

    private final ItemDto itemDto = new ItemDto(
            1L,
            "Brush",
            "Best brush",
            true,
            null,
            null,
            new ArrayList<>(),
            null);

    private final ItemDto updatedItemDto = new ItemDto(
            1L,
            "Updated brush",
            "Brush with wash",
            true,
            null,
            null,
            new ArrayList<>(),
            null);


    private final Booking booking = new Booking(
            1L,
            LocalDateTime.now(),
            LocalDateTime.now(),
            item,
            user,
            Status.WAITING);

    private final BookingOutDto bookingOutDto = new BookingOutDto(
            1L,
            LocalDateTime.now(),
            LocalDateTime.now(),
            Status.WAITING.name(),
            userDto,
            itemDto);


    @Test
    void createItemTest() {
        when(itemStorage.save(any(Item.class))).thenReturn(item);
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));

        ItemDto createdItem = itemService.create(userDto.getId(), itemDto);

        Assertions.assertNotNull(createdItem);
        Assertions.assertEquals(1, createdItem.getId());
        Assertions.assertEquals(itemDto.getName(), createdItem.getName());
        Assertions.assertEquals(itemDto.getDescription(), createdItem.getDescription());
        Assertions.assertTrue(createdItem.getAvailable());
        Assertions.assertNull(createdItem.getLastBooking());
        Assertions.assertNull(createdItem.getNextBooking());
        Assertions.assertEquals(itemDto.getComments().size(), createdItem.getComments().size());
        Assertions.assertEquals(itemDto.getRequestId(), createdItem.getRequestId());

        verify(itemStorage, times(1)).save(any(Item.class));
        verifyNoMoreInteractions(itemStorage);
    }

    @Test
    void getByItemIdTest() {
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingStorage.getLastBooking(anyLong(), any(LocalDateTime.class))).thenReturn(Optional.of(booking));
        when(bookingStorage.getNextBooking(anyLong(), any(LocalDateTime.class))).thenReturn(Optional.of(booking));

        ItemDto itemById = itemService.getById(1L, 1L);

        Assertions.assertNotNull(itemById);
        Assertions.assertEquals(1, itemById.getId());
        Assertions.assertEquals(itemDto.getName(), itemById.getName());
        Assertions.assertEquals(itemDto.getDescription(), itemById.getDescription());
        Assertions.assertTrue(itemById.getAvailable());
        Assertions.assertEquals(bookingOutDto.getId(), itemById.getLastBooking().getId());
        Assertions.assertEquals(bookingOutDto.getId(), itemById.getNextBooking().getId());
        Assertions.assertEquals(0, itemById.getComments().size());
        Assertions.assertEquals(itemDto.getRequestId(), itemById.getRequestId());

        verify(itemStorage, times(1)).findById(anyLong());
        verifyNoMoreInteractions(itemStorage);
        verify(bookingStorage, times(1)).getLastBooking(anyLong(), any(LocalDateTime.class));
        verify(bookingStorage, times(1)).getNextBooking(anyLong(), any(LocalDateTime.class));
        verifyNoMoreInteractions(bookingStorage);
    }

    @Test
    void getByItemIdWithoutBookingsTest() {
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item));

        ItemDto itemById = itemService.getById(1L, 2L);

        Assertions.assertNotNull(itemById);
        Assertions.assertEquals(1, itemById.getId());
        Assertions.assertEquals(itemDto.getName(), itemById.getName());
        Assertions.assertEquals(itemDto.getDescription(), itemById.getDescription());
        Assertions.assertTrue(itemById.getAvailable());
        Assertions.assertNull(itemById.getLastBooking());
        Assertions.assertNull(itemById.getNextBooking());
        Assertions.assertEquals(0, itemById.getComments().size());
        Assertions.assertEquals(itemDto.getRequestId(), itemById.getRequestId());

        verify(itemStorage, times(1)).findById(anyLong());
        verifyNoMoreInteractions(itemStorage);
    }

    @Test
    void getUserItemsTest() {
        when(itemStorage.findAllByOwnerIdOrderByIdAsc(anyLong(), any(Pageable.class))).thenReturn(List.of(item));
        when(bookingStorage.getLastBooking(anyLong(), any(LocalDateTime.class))).thenReturn(Optional.of(booking));
        when(bookingStorage.getNextBooking(anyLong(), any(LocalDateTime.class))).thenReturn(Optional.of(booking));

        List<ItemDto> items = itemService.getUserItems(1L, 0, 1);

        Assertions.assertEquals(items.size(), 1);
        Assertions.assertEquals(1, items.get(0).getId());
        Assertions.assertEquals(itemDto.getName(), items.get(0).getName());
        Assertions.assertEquals(itemDto.getDescription(), items.get(0).getDescription());
        Assertions.assertTrue(items.get(0).getAvailable());
        Assertions.assertEquals(bookingOutDto.getId(), items.get(0).getLastBooking().getId());
        Assertions.assertEquals(bookingOutDto.getId(), items.get(0).getNextBooking().getId());
        Assertions.assertEquals(0, items.get(0).getComments().size());
        Assertions.assertEquals(itemDto.getRequestId(), items.get(0).getRequestId());

        verify(itemStorage, times(1)).findAllByOwnerIdOrderByIdAsc(anyLong(), any(Pageable.class));
        verifyNoMoreInteractions(itemStorage);
        verify(bookingStorage, times(1)).getLastBooking(anyLong(), any(LocalDateTime.class));
        verify(bookingStorage, times(1)).getNextBooking(anyLong(), any(LocalDateTime.class));
        verifyNoMoreInteractions(bookingStorage);
    }

    @Test
    void searchItemTest() {
        when(itemStorage.search(anyString(), any(Pageable.class))).thenReturn(List.of(item));

        List<ItemDto> items = itemService.searchItem(1L,"Best", 0, 1);

        Assertions.assertEquals(1, items.size());
        Assertions.assertEquals(1, items.get(0).getId());
        Assertions.assertEquals(itemDto.getName(), items.get(0).getName());
        Assertions.assertEquals(itemDto.getDescription(), items.get(0).getDescription());
        Assertions.assertTrue(items.get(0).getAvailable());
        Assertions.assertEquals(0, items.get(0).getComments().size());
        Assertions.assertEquals(itemDto.getRequestId(), items.get(0).getRequestId());

        verify(itemStorage, times(1)).search(anyString(), any(Pageable.class));
        verifyNoMoreInteractions(itemStorage);
        verifyNoMoreInteractions(bookingStorage);
    }

    @Test
    void getAllItemsByEmptyTextTest() {
        List<ItemDto> items = itemService.searchItem(1L, "", 0, 1);

        Assertions.assertEquals(items.size(), 0);
    }

    @Test
    void updateItem() {
        final Item newItem = new Item(
                1L,
                "Updated brush",
                "Brush with wash",
                true,
                user,
                null);

        when(itemStorage.getReferenceById(anyLong())).thenReturn(item);
        when(bookingStorage.getLastBooking(anyLong(), any(LocalDateTime.class))).thenReturn(Optional.of(booking));
        when(bookingStorage.getNextBooking(anyLong(), any(LocalDateTime.class))).thenReturn(Optional.of(booking));

        ItemDto updatedItem = itemService.update(userDto.getId(), updatedItemDto, 1L);

        Assertions.assertNotNull(updatedItem);
        Assertions.assertEquals(1, updatedItem.getId());
        Assertions.assertEquals(newItem.getName(), updatedItem.getName());
        Assertions.assertEquals(updatedItemDto.getDescription(), updatedItem.getDescription());
        Assertions.assertTrue(updatedItem.getAvailable());
        Assertions.assertEquals(bookingOutDto.getId(), updatedItem.getLastBooking().getId());
        Assertions.assertEquals(bookingOutDto.getId(), updatedItem.getNextBooking().getId());
        Assertions.assertEquals(updatedItemDto.getRequestId(), updatedItem.getRequestId());
        verify(itemStorage, times(1)).getReferenceById(anyLong());
        verifyNoMoreInteractions(itemStorage);
        verify(bookingStorage, times(1)).getLastBooking(anyLong(), any(LocalDateTime.class));
        verify(bookingStorage, times(1)).getNextBooking(anyLong(), any(LocalDateTime.class));
        verifyNoMoreInteractions(bookingStorage);

    }

    @Test
    void updateWithNotFoundItemTest() {
        Long itemId = 1L;

        when(itemStorage.getReferenceById(anyLong())).thenReturn(null);

        Assertions.assertThrows(NullPointerException.class,
                () -> itemService.update(userDto.getId(), itemDto, itemId));
    }

    @Test
    void createCommentTest() {
        when(bookingStorage.getAllPastAndApprovedUserBooking(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));
        when(commentStorage.save(any(Comment.class))).thenReturn(comment);
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item));

        CommentDto createdComment = itemService.createComment(commentDto, userDto.getId(), itemDto.getId(), LocalDateTime.now());

        Assertions.assertNotNull(createdComment);
        Assertions.assertEquals(comment.getText(), createdComment.getText());
        Assertions.assertEquals(comment.getId(), createdComment.getId());
        Assertions.assertEquals(comment.getCreated().toString(), createdComment.getCreated().toString());
        Assertions.assertEquals(comment.getAuthor().getName(), createdComment.getAuthorName());

        verify(bookingStorage, times(1))
                .getAllPastAndApprovedUserBooking(anyLong(), anyLong(), any(LocalDateTime.class));
        verifyNoMoreInteractions(bookingStorage);
        verify(commentStorage, times(1)).save(any(Comment.class));
        verifyNoMoreInteractions(commentStorage);
    }

    @Test
    void createCommentWithEmptyBookingsTest() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingStorage.getAllPastAndApprovedUserBooking(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        Assertions.assertThrows(BadRequestException.class,
                () -> itemService.createComment(commentDto, userDto.getId(), itemDto.getId(), LocalDateTime.now()));
    }
}