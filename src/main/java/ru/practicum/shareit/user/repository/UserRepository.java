package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User createUser(User user);

    Optional<User> getUserById(int id);

    List<User> getUsers();

    User updateUser(int userId, User user);

    void deleteUser(int id);
}
