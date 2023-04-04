package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mvc;

    private ItemDto itemDto;
    private CommentDto commentDto;

    public static final String STANDARD_CHARSET = "StandardCharsets.UTF_8";
    public static final String SHARER_USER_ID = "X-Sharer-User-Id";
    public static final String ITEM_ID = "$.id";
    public static final String ITEM_NAME = "$.name";
    public static final String ITEM_DESCRIPTION = "$.description";
    public static final String ITEM_AVAILABLE = "$.available";
    public static final String LAST_BOOKING = "$.lastBooking";
    public static final String NEXT_BOOKING = "$.nextBooking";
    public static final String COMMENTS = "$.comments";
    public static final String REQUEST_ID = "$.requestId";

    @BeforeEach
    public void initVarsForTests() {

        itemDto = new ItemDto(
                1L,
                " Brush",
                "Best brush",
                true,
                null,
                null,
                new ArrayList<>(),
                1L);

        commentDto = new CommentDto(
                1L,
                "Cowabunga!",
                "Roman",
                LocalDateTime.now()
                        .withNano(0));
    }

    @Test
    void createNewItemTest() throws Exception {
        when(itemService.create(anyLong(), any(ItemDto.class))).thenReturn(itemDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(STANDARD_CHARSET)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER_ID, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(ITEM_ID, is(itemDto.getId()), Long.class))
                .andExpect(jsonPath(ITEM_NAME, is(itemDto.getName())))
                .andExpect(jsonPath(ITEM_DESCRIPTION, is(itemDto.getDescription())))
                .andExpect(jsonPath(ITEM_AVAILABLE, is(itemDto.getAvailable())))
                .andExpect(jsonPath(LAST_BOOKING, is(itemDto.getLastBooking())))
                .andExpect(jsonPath(NEXT_BOOKING, is(itemDto.getNextBooking())))
                .andExpect(jsonPath(COMMENTS, hasSize(0)))
                .andExpect(jsonPath(REQUEST_ID, is(itemDto.getRequestId()), Long.class));
    }

    @Test
    void getAllItemsByUserIdTest() throws Exception {
        when(itemService.getUserItems(any(), anyInt(), anyInt())).thenReturn(List.of(itemDto));

        mvc.perform(get("/items")
                        .characterEncoding(STANDARD_CHARSET)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER_ID, "1")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$[0].lastBooking", is(itemDto.getLastBooking())))
                .andExpect(jsonPath("$[0].nextBooking", is(itemDto.getNextBooking())))
                .andExpect(jsonPath("$[0].comments", hasSize(0)))
                .andExpect(jsonPath("$[0].requestId", is(itemDto.getRequestId()), Long.class));
    }

    @Test
    void addCommentTest() throws Exception {
        when(itemService.getById(anyLong(), anyLong())).thenReturn(itemDto);
        when(itemService.createComment(any(CommentDto.class), anyLong(),
                anyLong(), any(LocalDateTime.class))).thenReturn(commentDto);

        mvc.perform(post("/items/{id}/comment", "1")
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(STANDARD_CHARSET)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER_ID, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.created", is(commentDto.getCreated().toString())));
    }

    @Test
    void getItemByIdTest() throws Exception {
        when(itemService.getById(any(), any())).thenReturn(itemDto);

        mvc.perform(get("/items/{id}", "1")
                        .characterEncoding(STANDARD_CHARSET)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER_ID, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(ITEM_ID, is(itemDto.getId()), Long.class))
                .andExpect(jsonPath(ITEM_NAME, is(itemDto.getName())))
                .andExpect(jsonPath(ITEM_DESCRIPTION, is(itemDto.getDescription())))
                .andExpect(jsonPath(ITEM_AVAILABLE, is(itemDto.getAvailable())))
                .andExpect(jsonPath(LAST_BOOKING, is(itemDto.getLastBooking())))
                .andExpect(jsonPath(NEXT_BOOKING, is(itemDto.getNextBooking())))
                .andExpect(jsonPath(COMMENTS, hasSize(0)))
                .andExpect(jsonPath(REQUEST_ID, is(itemDto.getRequestId()), Long.class));
    }

    @Test
    void getItemByTextTest() throws Exception {
        when(itemService.searchItem(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search")
                        .characterEncoding(STANDARD_CHARSET)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER_ID, "1")
                        .param("text", "text")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$[0].lastBooking", is(itemDto.getLastBooking())))
                .andExpect(jsonPath("$[0].nextBooking", is(itemDto.getNextBooking())))
                .andExpect(jsonPath("$[0].comments", hasSize(0)))
                .andExpect(jsonPath("$[0].requestId", is(itemDto.getRequestId()), Long.class));
    }

    @Test
    void getItemByEmptyTextTest() throws Exception {
        when(itemService.searchItem(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search")
                        .characterEncoding(STANDARD_CHARSET)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER_ID, "1")
                        .param("text", "")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void updateItemByIdTest() throws Exception {
        when(itemService.update(anyLong(), any(ItemDto.class), anyLong())).thenReturn(itemDto);

        mvc.perform(patch("/items/{id}", "1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(STANDARD_CHARSET)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER_ID, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(ITEM_ID, is(itemDto.getId()), Long.class))
                .andExpect(jsonPath(ITEM_NAME, is(itemDto.getName())))
                .andExpect(jsonPath(ITEM_DESCRIPTION, is(itemDto.getDescription())))
                .andExpect(jsonPath(ITEM_AVAILABLE, is(itemDto.getAvailable())))
                .andExpect(jsonPath(LAST_BOOKING, is(itemDto.getLastBooking())))
                .andExpect(jsonPath(NEXT_BOOKING, is(itemDto.getNextBooking())))
                .andExpect(jsonPath(COMMENTS, hasSize(0)))
                .andExpect(jsonPath(REQUEST_ID, is(itemDto.getRequestId()), Long.class));
    }
}
