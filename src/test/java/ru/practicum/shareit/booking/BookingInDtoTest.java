package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingInDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingInDtoTest {

    @Autowired
    private JacksonTester<BookingInDto> json;

    private static final String DATE_TIME = "2023-04-02T12:15:45";

    private BookingInDto bookingInDto = null;

    @BeforeEach
    public void initVarsForTests() {
        bookingInDto = new BookingInDto(
                2L,
                LocalDateTime.parse("2023-04-02T12:15:45.100"),
                LocalDateTime.parse("2023-04-02T12:15:45.100"));
    }

    @Test
    public void startSerializesTest() throws IOException {
        assertThat(json.write(bookingInDto))
                .extractingJsonPathStringValue("$.start")
                .isEqualTo(DATE_TIME);
    }

    @Test
    public void endSerializesTest() throws IOException {
        assertThat(json.write(bookingInDto))
                .extractingJsonPathStringValue("$.end")
                .isEqualTo(DATE_TIME);
    }
}