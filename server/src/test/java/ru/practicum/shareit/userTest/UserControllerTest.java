package ru.practicum.shareit.userTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserControllerTest {

    @Autowired
    MockMvc mvc;
    final ObjectMapper objectMapper = new ObjectMapper();
    @MockBean
    UserService userService;
    UserDto userDto;

    @BeforeEach
    public void beforeEach() {
        userDto = createTestUser();
    }

    private UserDto createTestUser() {
        return UserDto.builder()
                .id(1L)
                .name("Example")
                .email("example@example.ru")
                .build();
    }

    @Test
    public void createValidUser() throws Exception {
        when(userService.createUser(userDto)).thenReturn(userDto);

        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\": 1, \"name\": \"Example\", \"email\": \"example@example.ru\"}"));

    }

    @Test
    public void getUserById() throws Exception {
        when(userService.getUserById(userDto.getId())).thenReturn(userDto);

        mvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\": 1, \"name\": \"Example\", \"email\": \"example@example.ru\"}"));
    }

    @Test
    public void getUsers() throws Exception {
        List<UserDto> list = List.of(userDto);
        when(userService.getUsers()).thenReturn(list);
        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\": 1, \"name\": \"Example\", \"email\": \"example@example.ru\"}]"));
    }

    @Test
    public void updateUser() throws Exception {
        UserDto userDto1 = createTestUser();
        userDto1.setName("Example1");
        userService.createUser(userDto);
        when(userService.updateUser(1, userDto1)).thenReturn(userDto1);
        mvc.perform(patch("/users/1")
                        .content(objectMapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\": 1, \"name\": \"Example1\", \"email\": \"example@example.ru\"}"));
    }

    @Test
    public void deleteUser() throws Exception {
        userService.createUser(userDto);
        mvc.perform(delete("/users/1")).andExpect(status().isOk());
    }
}
