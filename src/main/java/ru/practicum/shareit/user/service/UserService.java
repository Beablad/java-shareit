package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    User createUser(User user);

    User getUserById(int id);

    List<User> getUsers();

    User updateUser(int userId, User user);

    void deleteUser(int id);
}
