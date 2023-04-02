package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.model.Status;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingOutDtoTest {

    @Autowired
    private JacksonTester<BookingOutDto> json;

    private static final String DATE_TIME = "2023-04-02T12:15:45";

    private BookingOutDto bookingOutputDto = null;

    @BeforeEach
    public void setup() {
        bookingOutputDto = new BookingOutDto(
                2L,
                LocalDateTime.parse("2023-04-02T12:15:45.100"),
                LocalDateTime.parse("2023-04-02T12:15:45.100"),
                Status.WAITING.name(),
                null,
                null);
    }

    @Test
    public void startSerializesTest() throws IOException {
        assertThat(json.write(bookingOutputDto))
                .extractingJsonPathStringValue("$.start")
                .isEqualTo(DATE_TIME);
    }

    @Test
    public void endSerializesTest() throws IOException {
        assertThat(json.write(bookingOutputDto))
                .extractingJsonPathStringValue("$.end")
                .isEqualTo(DATE_TIME);
    }
}