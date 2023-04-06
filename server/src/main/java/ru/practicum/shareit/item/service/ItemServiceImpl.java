package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.RequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
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
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final RequestRepository requestRepository;

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
        return toItemDto(itemRepository.save(toItem(itemDto, owner)));

    }

    @Override
    @Transactional
    public ItemDto update(Long userId, ItemDto itemDto, Long itemId) {
        Item savedItem = itemRepository.getReferenceById(itemId);
        User owner = savedItem.getOwner();
        if (!owner.getId().equals(userId)) {
            log.warn("User with id={} can`t edit item with id={}", userId, itemId);
            throw new NotFoundException("User not rights to edit item");
        }

        String name = itemDto.getName();
        String description = itemDto.getDescription();
        Boolean isAvailable = itemDto.getAvailable();

        if (name != null && !name.isEmpty()) {
            savedItem.setName(name);
        }
        if (description != null && !description.isEmpty()) {
            savedItem.setDescription(description);
        }
        if (isAvailable != null) {
            savedItem.setAvailable(isAvailable);
        }

        return toItemDto(savedItem, getLastBooking(itemId), getNextBooking(itemId), getItemComments(itemId));
    }

    @Override
    public List<ItemDto> searchItem(Long userId, String request, Integer from, Integer size) {
        return itemRepository.search(request, PageRequest.of(from / size, size)).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getUserItems(Long userId, Integer from, Integer size) {
        return itemRepository.findAllByOwnerIdOrderByIdAsc(userId, PageRequest.of(from / size, size)).stream()
                .map(item -> toItemDto(item, getLastBooking(item.getId()),
                        getNextBooking(item.getId()), getItemComments(item.getId())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Long itemId) {
        itemRepository.deleteById(itemId);
    }

    @Override
    @Transactional
    public CommentDto createComment(CommentDto commentDto, Long userId, Long itemId, LocalDateTime timestamp) {
        User user = validateUserByIdOrException(userId);
        Item item = validateItemByIdOrException(itemId);
        List<Booking> bookings = bookingRepository.getAllPastAndApprovedUserBooking(userId, itemId, timestamp);

        if (bookings.isEmpty()) {
            throw new BadRequestException("User id=" + userId + " has never booked item id=" + itemId);
        }
        Comment comment = toComment(commentDto, item, user);

        return toCommentDto(commentRepository.save(comment));
    }

    private BookingInfoDto getNextBooking(Long itemId) {
        return bookingRepository.getNextBooking(itemId, LocalDateTime.now())
                .map(BookingMapper::toBookingInfoDto)
                .orElse(null);
    }

    private BookingInfoDto getLastBooking(Long itemId) {
        return bookingRepository.getLastBooking(itemId, LocalDateTime.now())
                .map(BookingMapper::toBookingInfoDto)
                .orElse(null);
    }

    private User validateUserByIdOrException(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User id=" + userId + " not found!"));
    }

    private Item validateItemByIdOrException(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Item id=" + itemId + " not found!"));
    }

    private ItemRequest validateRequestByIdOrException(Long requestId) {
        return requestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException("Request id=" + requestId + " not found!"));
    }

    private List<CommentDto> getItemComments(Long itemId) {
        return commentRepository.findAllByItemId(itemId).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

}
