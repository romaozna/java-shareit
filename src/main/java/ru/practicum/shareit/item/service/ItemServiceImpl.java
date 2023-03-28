package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dao.BookingStorage;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.CommentStorage;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.dto.CommentMapper.toComment;
import static ru.practicum.shareit.item.dto.CommentMapper.toCommentDto;
import static ru.practicum.shareit.item.dto.ItemMapper.toItem;
import static ru.practicum.shareit.item.dto.ItemMapper.toItemDto;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final BookingStorage bookingStorage;
    private final CommentStorage commentStorage;

    @Override
    public ItemDto getById(Long userId, Long itemId) {
        Item item = validateItemByIdOrException(itemId);
        List<CommentDto> comments = getItemComments(itemId);

        if (item.getOwner().getId().equals(userId)) {
            return toItemDto(item, getLastBooking(itemId), getNextBooking(itemId), comments);
        }

        return toItemDto(item, comments);
    }

    @Override
    @Transactional
    public ItemDto create(Long userId, ItemDto itemDto) {
        User owner = validateUserByIdOrException(userId);
        return toItemDto(itemStorage.save(toItem(itemDto, owner, null)));
    }

    @Override
    @Transactional
    public ItemDto update(Long userId, ItemDto itemDto, Long itemId) {
        Item savedItem = itemStorage.getReferenceById(itemId);
        User owner = savedItem.getOwner();
        if (!owner.getId().equals(userId)) {
            log.warn("User with id={} can`t edit item with id={}", userId, itemId);
            throw new NotFoundException("User not rights to edit item");
        }

        String name = itemDto.getName();
        String description = itemDto.getDescription();
        Boolean isAvailable = itemDto.getAvailable();

        if (name != null && !name.isBlank()) {
            savedItem.setName(name);
        }
        if (description != null && !description.isBlank()) {
            savedItem.setDescription(description);
        }
        if (isAvailable != null) {
            savedItem.setAvailable(isAvailable);
        }

        return toItemDto(savedItem, getLastBooking(itemId), getNextBooking(itemId), getItemComments(itemId));
    }

    @Override
    public List<ItemDto> searchItem(Long userId, String request) {
        return itemStorage.search(request).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getUserItems(Long userId) {
        return itemStorage.findAllByOwnerIdOrderByIdAsc(userId).stream()
                .map(item -> toItemDto(item, getLastBooking(item.getId()),
                        getNextBooking(item.getId()), getItemComments(item.getId())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Long itemId) {
        itemStorage.deleteById(itemId);
    }

    @Override
    @Transactional
    public CommentDto createComment(CommentDto commentDto, Long userId, Long itemId) {
        User user = validateUserByIdOrException(userId);
        Item item = validateItemByIdOrException(itemId);
        List<Booking> bookings = bookingStorage.getAllPastAndApprovedUserBooking(userId, itemId, LocalDateTime.now());

        if (bookings.isEmpty()) {
            throw new BadRequestException("User id=" + userId + " has never booked item id=" + itemId);
        }
        Comment comment = toComment(commentDto, item, user);

        return toCommentDto(commentStorage.save(comment));
    }

    private BookingInfoDto getNextBooking(Long itemId) {
        return bookingStorage.getNextBooking(itemId, LocalDateTime.now())
                .map(BookingMapper::toBookingInfoDto)
                .orElse(null);
    }

    private BookingInfoDto getLastBooking(Long itemId) {
        return bookingStorage.getLastBooking(itemId, LocalDateTime.now())
                .map(BookingMapper::toBookingInfoDto)
                .orElse(null);
    }

    private User validateUserByIdOrException(Long userId) {
        return userStorage.findById(userId).orElseThrow(() ->
                new NotFoundException("User id=" + userId + " not found!"));
    }

    private Item validateItemByIdOrException(Long itemId) {
        return itemStorage.findById(itemId).orElseThrow(() ->
                new NotFoundException("Item id=" + itemId + " not found!"));
    }

    private List<CommentDto> getItemComments(Long itemId) {
        return commentStorage.findAllByItemId(itemId).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }
}
