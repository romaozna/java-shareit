package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mvc;

    public static final String STANDARD_CHARSET = "StandardCharsets.UTF_8";
    public static final String USER_ID = "$.id";
    public static final String USER_NAME = "$.name";
    public static final String USER_EMAIL = "$.email";
    private UserDto userDto;

    @BeforeEach
    public void initVarsForTests() {
        userDto = new UserDto(
                1L,
                "Roman",
                "roman@mail.com");
    }

    @Test
    void createUserTest() throws Exception {
        when(userService.create(any()))
                .thenReturn(userDto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(STANDARD_CHARSET)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(USER_ID, is(userDto.getId()), Long.class))
                .andExpect(jsonPath(USER_NAME, is(userDto.getName())))
                .andExpect(jsonPath(USER_EMAIL, is(userDto.getEmail())));
    }

    @Test
    void getUserByIdTest() throws Exception {
        when(userService.getById(any())).thenReturn(userDto);

        mvc.perform(get("/users/{id}", "1")
                        .characterEncoding(STANDARD_CHARSET)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(USER_ID, is(userDto.getId()), Long.class))
                .andExpect(jsonPath(USER_NAME, is(userDto.getName())))
                .andExpect(jsonPath(USER_EMAIL, is(userDto.getEmail())));
    }

    @Test
    void updateUserTest() throws Exception {
        when(userService.update(any(), anyLong())).thenReturn(userDto);

        mvc.perform(patch("/users/{id}", "1")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(STANDARD_CHARSET)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(USER_ID, is(userDto.getId()), Long.class))
                .andExpect(jsonPath(USER_NAME, is(userDto.getName())))
                .andExpect(jsonPath(USER_EMAIL, is(userDto.getEmail())));
    }

    @Test
    void getAllUsersTest() throws Exception {
        List<UserDto> users = List.of(userDto);

        when(userService.getAll()).thenReturn(users);

        mvc.perform(get("/users")
                        .characterEncoding(STANDARD_CHARSET)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(userDto.getName())))
                .andExpect(jsonPath("$[0].email", is(userDto.getEmail())));

    }

    @Test
    void deleteUserTest() throws Exception {
        mvc.perform(delete("/users/{id}", "1")
                        .characterEncoding(STANDARD_CHARSET))
                .andExpect(status().isNoContent());
    }
}
