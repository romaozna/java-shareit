package ru.practicum.shareit.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ErrorHandlerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    public static final String STANDARD_CHARSET = "StandardCharsets.UTF_8";
    public static final String SHARER_USER_ID = "X-Sharer-User-Id";
    private UserDto userDto;
    private UserDto userDtoWithErrorEmail;
    private ItemDto itemDto;

    @BeforeEach
    public void initVarsForTests() {
        userDto = new UserDto(
                null,
                "Roman",
                "roman@mail.com");

        userDtoWithErrorEmail = new UserDto(
                null,
                "Roman",
                "romanmail.com");

        itemDto = new ItemDto(
                null,
                "Brush",
                "Best brush",
                false,
                null,
                null,
                new ArrayList<>(),
                null);
    }

    @Test
    void badPathVariableTest() throws Exception {
        mvc.perform(get("/items/hello")
                        .characterEncoding(STANDARD_CHARSET)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER_ID, "1"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void notFoundTest() throws Exception {
        mvc.perform(get("/requests/99")
                        .characterEncoding(STANDARD_CHARSET)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER_ID, "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void badParamsTest() throws Exception {
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDtoWithErrorEmail))
                        .characterEncoding(STANDARD_CHARSET)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void conflictTest() throws Exception {
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(STANDARD_CHARSET)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(STANDARD_CHARSET)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void badRequestTest() throws Exception {
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(STANDARD_CHARSET)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER_ID, "1"))
                .andExpect(status().isOk());

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(STANDARD_CHARSET)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER_ID, "1"))
                .andExpect(status().isOk());

        mvc.perform(get("/bookings")
                        .queryParam("state", "UNKNOWN")
                        .characterEncoding(STANDARD_CHARSET)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER_ID, "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void validationTest() throws Exception {
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDtoWithErrorEmail))
                        .characterEncoding(STANDARD_CHARSET)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }
}