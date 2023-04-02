package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.BookingInfoDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingInfoDtoTest {

    @Autowired
    private JacksonTester<BookingInfoDto> json;

    private static final String DATE_TIME = "2023-04-02T12:15:45";

    private BookingInfoDto bookerInfoDto = null;

    @BeforeEach
    public void setup() {
        bookerInfoDto = new BookingInfoDto(
                4L,
                5L,
                LocalDateTime.parse("2023-04-02T12:15:45.100"),
                LocalDateTime.parse("2023-04-02T12:15:45.100")
        );
    }

    @Test
    public void startSerializes() throws IOException {
        assertThat(json.write(bookerInfoDto))
                .extractingJsonPathStringValue("$.start")
                .isEqualTo(DATE_TIME);
    }

    @Test
    public void endSerializes() throws IOException {
        assertThat(json.write(bookerInfoDto))
                .extractingJsonPathStringValue("$.end")
                .isEqualTo(DATE_TIME);
    }
}
