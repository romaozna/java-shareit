package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private BookingOutDto bookingOutDto;
    private BookingInDto bookingInDto;
    public static final String STANDARD_CHARSET = "StandardCharsets.UTF_8";
    public static final String SHARER_USER_ID = "X-Sharer-User-Id";
    public static final String BOOKING_ID = "$.id";
    public static final String BOOKING_START = "$.start";
    public static final String BOOKING_END = "$.end";
    public static final String BOOKING_STATUS = "$.status";
    public static final String BOOKING_BOOKER_ID = "$.booker.id";
    public static final String BOOKING_ITEM_ID = "$.item.id";


    @BeforeEach
    public void initVarsForTests() {
        LocalDateTime now = LocalDateTime.now();

        UserDto userDto = new UserDto(
                1L,
                "Roman",
                "Roman@mail.com");

        ItemDto itemDto = new ItemDto(
                1L,
                "brush",
                "best brush",
                true,
                null,
                null,
                new ArrayList<>(),
                1L);

        bookingOutDto = new BookingOutDto(
                1L,
                now.truncatedTo(ChronoUnit.SECONDS),
                now.plusDays(1).truncatedTo(ChronoUnit.SECONDS),
                Status.WAITING.name(),
                userDto,
                itemDto);

        bookingInDto = new BookingInDto(
                1L,
                now,
                now.plusDays(1));
    }

    @Test
    void createNewBookingTest() throws Exception {
        when(bookingService.create(anyLong(), any(BookingInDto.class)))
                .thenReturn(bookingOutDto);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingInDto))
                        .characterEncoding(STANDARD_CHARSET)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER_ID, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(BOOKING_ID, is(bookingOutDto.getId()), Long.class))
                .andExpect(jsonPath(BOOKING_START, is(bookingOutDto.getStart().toString())))
                .andExpect(jsonPath(BOOKING_END, is(bookingOutDto.getEnd().toString())))
                .andExpect(jsonPath(BOOKING_STATUS, is(bookingOutDto.getStatus())))
                .andExpect(jsonPath(BOOKING_BOOKER_ID, is(bookingOutDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath(BOOKING_ITEM_ID, is(bookingOutDto.getItem().getId()), Long.class));
    }

    @Test
    void approvedOrRejectTest() throws Exception {
        when(bookingService.approveOrReject(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingOutDto);

        mvc.perform(patch("/bookings/{bookingId}", "1")
                        .content(mapper.writeValueAsString(bookingInDto))
                        .characterEncoding(STANDARD_CHARSET)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER_ID, "1")
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(BOOKING_ID, is(bookingOutDto.getId()), Long.class))
                .andExpect(jsonPath(BOOKING_START, is(bookingOutDto.getStart().toString())))
                .andExpect(jsonPath(BOOKING_END, is(bookingOutDto.getEnd().toString())))
                .andExpect(jsonPath(BOOKING_STATUS, is(bookingOutDto.getStatus())))
                .andExpect(jsonPath(BOOKING_BOOKER_ID, is(bookingOutDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath(BOOKING_ITEM_ID, is(bookingOutDto.getItem().getId()), Long.class));
    }

    @Test
    void getByIdTest() throws Exception {
        when(bookingService.getById(anyLong(), anyLong())).thenReturn(bookingOutDto);

        mvc.perform(get("/bookings/{bookingId}", "1")
                        .content(mapper.writeValueAsString(bookingInDto))
                        .characterEncoding(STANDARD_CHARSET)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER_ID, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(BOOKING_ID, is(bookingOutDto.getId()), Long.class))
                .andExpect(jsonPath(BOOKING_START, is(bookingOutDto.getStart().toString())))
                .andExpect(jsonPath(BOOKING_END, is(bookingOutDto.getEnd().toString())))
                .andExpect(jsonPath(BOOKING_STATUS, is(bookingOutDto.getStatus())))
                .andExpect(jsonPath(BOOKING_BOOKER_ID, is(bookingOutDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath(BOOKING_ITEM_ID, is(bookingOutDto.getItem().getId()), Long.class));
    }

    @Test
    void getAllByBookerTest() throws Exception {
        when(bookingService.getAllByBooker(anyLong(), any(State.class), anyInt(), anyInt()))
                .thenReturn(List.of(bookingOutDto));

        mvc.perform(get("/bookings")
                        .content(mapper.writeValueAsString(bookingInDto))
                        .characterEncoding(STANDARD_CHARSET)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER_ID, "1")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingOutDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingOutDto.getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(bookingOutDto.getEnd().toString())))
                .andExpect(jsonPath("$[0].status", is(bookingOutDto.getStatus())))
                .andExpect(jsonPath("$[0].booker.id", is(bookingOutDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(bookingOutDto.getItem().getId()), Long.class));
    }

    @Test
    void getAllByBookerTestWithInvalidState() throws Exception {
        when(bookingService.getAllByBooker(anyLong(), any(State.class), anyInt(), anyInt()))
                .thenReturn(List.of(bookingOutDto));

        mvc.perform(get("/bookings")
                        .content(mapper.writeValueAsString(bookingInDto))
                        .characterEncoding(STANDARD_CHARSET)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER_ID, "1")
                        .param("state", "UNKNOWN")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().is4xxClientError())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadRequestException))
                .andExpect(result -> assertEquals("Unknown state: UNKNOWN", Objects.requireNonNull(result
                        .getResolvedException()).getMessage()));
    }

    @Test
    void getAllByOwnerTest() throws Exception {
        when(bookingService.getAllByOwner(anyLong(), any(State.class), anyInt(), anyInt()))
                .thenReturn(List.of(bookingOutDto));

        mvc.perform(get("/bookings/owner")
                        .content(mapper.writeValueAsString(bookingInDto))
                        .characterEncoding(STANDARD_CHARSET)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER_ID, "1")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingOutDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingOutDto.getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(bookingOutDto.getEnd().toString())))
                .andExpect(jsonPath("$[0].status", is(bookingOutDto.getStatus())))
                .andExpect(jsonPath("$[0].booker.id", is(bookingOutDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(bookingOutDto.getItem().getId()), Long.class));
    }

    @Test
    void getAllByOwnerTestWithInvalidState() throws Exception {
        when(bookingService.getAllByOwner(anyLong(), any(State.class), anyInt(), anyInt()))
                .thenReturn(List.of(bookingOutDto));

        mvc.perform(get("/bookings/owner")
                        .content(mapper.writeValueAsString(bookingInDto))
                        .characterEncoding(STANDARD_CHARSET)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER_ID, "1")
                        .param("state", "UNKNOWN")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().is4xxClientError())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadRequestException))
                .andExpect(result -> assertEquals("Unknown state: UNKNOWN", Objects.requireNonNull(result
                        .getResolvedException()).getMessage()));
    }
}