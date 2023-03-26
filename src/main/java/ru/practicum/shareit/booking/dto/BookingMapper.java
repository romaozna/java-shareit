package ru.practicum.shareit.booking.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.BookingInfoDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;

@Component
public class BookingMapper {

    public static BookingOutDto toBookingOutDto(Booking booking) {
        ItemDto itemDto = new ItemDto(booking.getItem().getId(),
                booking.getItem().getName(),
                booking.getItem().getDescription(),
                booking.getItem().getAvailable(),
                null,
                null,
                new ArrayList<>());
        UserDto userDto = new UserDto(booking.getBooker().getId(),
                booking.getBooker().getName(),
                booking.getBooker().getEmail());
        return new BookingOutDto(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                userDto,
                itemDto);
    }

    public static BookingInfoDto toBookingInfoDto(Booking booking) {
        return new BookingInfoDto(booking.getId(), booking.getBooker().getId(),
                booking.getStart(), booking.getEnd());
    }

    public static Booking toBooking(BookingInDto inDto, Item item, User booker, Status status) {
       return new Booking(null, inDto.getStart(), inDto.getEnd(), item, booker, status);
    }
}
