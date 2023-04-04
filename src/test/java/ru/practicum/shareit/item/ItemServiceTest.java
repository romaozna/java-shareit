package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dao.UserRepository;
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
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BookingRepository bookingRepository;

    private UserDto userDto;
    private User user;
    private Item item;
    private Comment comment;
    private CommentDto commentDto;
    private ItemDto itemDto;
    private ItemDto updatedItemDto;
    private Booking booking;
    private BookingOutDto bookingOutDto;

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

        item = new Item(
                1L,
                "Brush",
                "Best brush",
                true,
                user,
                null);

        comment = new Comment(
                1L,
                "Great!",
                item,
                user,
                now);

        commentDto = new CommentDto(
                null,
                "Great!",
                "Roman",
                now);

        itemDto = new ItemDto(
                1L,
                "Brush",
                "Best brush",
                true,
                null,
                null,
                new ArrayList<>(),
                null);

        updatedItemDto = new ItemDto(
                1L,
                "Updated brush",
                "Brush with wash",
                true,
                null,
                null,
                new ArrayList<>(),
                null);


        booking = new Booking(
                1L,
                now,
                now,
                item,
                user,
                Status.WAITING);

        bookingOutDto = new BookingOutDto(
                1L,
                now,
                now,
                Status.WAITING.name(),
                userDto,
                itemDto);
    }

    @Test
    void createItemTest() {
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

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

        verify(itemRepository, times(1)).save(any(Item.class));
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void getByItemIdTest() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.getLastBooking(anyLong(), any(LocalDateTime.class))).thenReturn(Optional.of(booking));
        when(bookingRepository.getNextBooking(anyLong(), any(LocalDateTime.class))).thenReturn(Optional.of(booking));

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

        verify(itemRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(itemRepository);
        verify(bookingRepository, times(1)).getLastBooking(anyLong(), any(LocalDateTime.class));
        verify(bookingRepository, times(1)).getNextBooking(anyLong(), any(LocalDateTime.class));
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    void getByItemIdWithoutBookingsTest() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

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

        verify(itemRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void getUserItemsTest() {
        when(itemRepository.findAllByOwnerIdOrderByIdAsc(anyLong(), any(Pageable.class))).thenReturn(List.of(item));
        when(bookingRepository.getLastBooking(anyLong(), any(LocalDateTime.class))).thenReturn(Optional.of(booking));
        when(bookingRepository.getNextBooking(anyLong(), any(LocalDateTime.class))).thenReturn(Optional.of(booking));

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

        verify(itemRepository, times(1)).findAllByOwnerIdOrderByIdAsc(anyLong(), any(Pageable.class));
        verifyNoMoreInteractions(itemRepository);
        verify(bookingRepository, times(1)).getLastBooking(anyLong(), any(LocalDateTime.class));
        verify(bookingRepository, times(1)).getNextBooking(anyLong(), any(LocalDateTime.class));
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    void searchItemTest() {
        when(itemRepository.search(anyString(), any(Pageable.class))).thenReturn(List.of(item));

        List<ItemDto> items = itemService.searchItem(1L,"Best", 0, 1);

        Assertions.assertEquals(1, items.size());
        Assertions.assertEquals(1, items.get(0).getId());
        Assertions.assertEquals(itemDto.getName(), items.get(0).getName());
        Assertions.assertEquals(itemDto.getDescription(), items.get(0).getDescription());
        Assertions.assertTrue(items.get(0).getAvailable());
        Assertions.assertEquals(0, items.get(0).getComments().size());
        Assertions.assertEquals(itemDto.getRequestId(), items.get(0).getRequestId());

        verify(itemRepository, times(1)).search(anyString(), any(Pageable.class));
        verifyNoMoreInteractions(itemRepository);
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    void getAllItemsByEmptyTextTest() {
        List<ItemDto> items = itemService.searchItem(1L, "", 0, 1);

        Assertions.assertEquals(items.size(), 0);
    }

    @Test
    void updateItemTest() {
        final Item newItem = new Item(
                1L,
                "Updated brush",
                "Brush with wash",
                true,
                user,
                null);

        when(itemRepository.getReferenceById(anyLong())).thenReturn(item);
        when(bookingRepository.getLastBooking(anyLong(), any(LocalDateTime.class))).thenReturn(Optional.of(booking));
        when(bookingRepository.getNextBooking(anyLong(), any(LocalDateTime.class))).thenReturn(Optional.of(booking));

        ItemDto updatedItem = itemService.update(userDto.getId(), updatedItemDto, 1L);

        Assertions.assertNotNull(updatedItem);
        Assertions.assertEquals(1, updatedItem.getId());
        Assertions.assertEquals(newItem.getName(), updatedItem.getName());
        Assertions.assertEquals(updatedItemDto.getDescription(), updatedItem.getDescription());
        Assertions.assertTrue(updatedItem.getAvailable());
        Assertions.assertEquals(bookingOutDto.getId(), updatedItem.getLastBooking().getId());
        Assertions.assertEquals(bookingOutDto.getId(), updatedItem.getNextBooking().getId());
        Assertions.assertEquals(updatedItemDto.getRequestId(), updatedItem.getRequestId());
        verify(itemRepository, times(1)).getReferenceById(anyLong());
        verifyNoMoreInteractions(itemRepository);
        verify(bookingRepository, times(1)).getLastBooking(anyLong(), any(LocalDateTime.class));
        verify(bookingRepository, times(1)).getNextBooking(anyLong(), any(LocalDateTime.class));
        verifyNoMoreInteractions(bookingRepository);

    }

    @Test
    void updateWithNotFoundItemTest() {
        Long itemId = 1L;

        when(itemRepository.getReferenceById(anyLong())).thenReturn(null);

        Assertions.assertThrows(NullPointerException.class,
                () -> itemService.update(userDto.getId(), itemDto, itemId));
    }

    @Test
    void createCommentTest() {
        when(bookingRepository.getAllPastAndApprovedUserBooking(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        CommentDto createdComment = itemService.createComment(commentDto, userDto.getId(), itemDto.getId(), LocalDateTime.now());

        Assertions.assertNotNull(createdComment);
        Assertions.assertEquals(comment.getText(), createdComment.getText());
        Assertions.assertEquals(comment.getId(), createdComment.getId());
        Assertions.assertEquals(comment.getCreated().toString(), createdComment.getCreated().toString());
        Assertions.assertEquals(comment.getAuthor().getName(), createdComment.getAuthorName());

        verify(bookingRepository, times(1))
                .getAllPastAndApprovedUserBooking(anyLong(), anyLong(), any(LocalDateTime.class));
        verifyNoMoreInteractions(bookingRepository);
        verify(commentRepository, times(1)).save(any(Comment.class));
        verifyNoMoreInteractions(commentRepository);
    }

    @Test
    void createCommentWithEmptyBookingsTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.getAllPastAndApprovedUserBooking(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        Assertions.assertThrows(BadRequestException.class,
                () -> itemService.createComment(commentDto, userDto.getId(), itemDto.getId(), LocalDateTime.now()));
    }
}