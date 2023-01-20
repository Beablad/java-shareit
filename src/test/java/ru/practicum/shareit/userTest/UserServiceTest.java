package ru.practicum.shareit.userTest;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@FieldDefaults (level = AccessLevel.PRIVATE)
public class UserServiceTest {

    UserService userService;
    UserRepository userRepository;
    User user;

    @BeforeEach
    void beforeEach() {
        userRepository = mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);
        user = createTestUser();
    }

    User createTestUser() {
        return User.builder()
                .id(1L)
                .email("test@test.ru")
                .name("TestUser")
                .build();
    }

    @Test
    public void createUser() {
        UserDto userDto = UserMapper.toUserDto(user);
        when(userRepository.save(user)).thenReturn(user);
        userService.createUser(userDto);

        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());

        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void getUserById() {
        when(userRepository.save(user)).thenReturn(user);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        UserDto userDto = userService.getUserById(user.getId());
        userService.createUser(userDto);

        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());

        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    public void getUsers() {
        List<User> userList = List.of(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userRepository.findAll()).thenReturn(userList);
        userService.createUser(UserMapper.toUserDto(user));
        List<UserDto> userDtoList = userService.getUsers();

        assertEquals(userList.get(0).getId(), userDtoList.get(0).getId());

        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void updateValidUser() {
        User user1 = createTestUser();
        Long userId = user.getId();
        user1.setName("test1");
        when(userRepository.save(user1)).thenReturn(user1);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        UserDto userDto = userService.updateUser(userId, UserMapper.toUserDto(user1));

        assertEquals(userId, userDto.getId(), "Идентификаторы не совпадают");
        assertEquals(user1.getName(), userDto.getName(), "Имена не совпадают");
        assertEquals(user.getEmail(), userDto.getEmail(), "Почты не совпадают");

        verify(userRepository, times(1)).save(user1);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void getUnknownUser() {
        Throwable throwable = assertThrows(NotFoundException.class, () -> userService.getUserById(user.getId()));

        assertEquals("Пользователь не найден", throwable.getMessage(),
                "Пользователь не найден");
    }

    //Обновление несуществующего пользователя
    @Test
    public void updateUnknownUser() {
        User user1 = createTestUser();
        user1.setName("test1");

        Throwable throwable = assertThrows(NotFoundException.class, () ->
                userService.updateUser(user1.getId(), UserMapper.toUserDto(user1)));

        assertEquals("Пользователь не найден", throwable.getMessage(),
                "Пользователь не найден");
    }
}
