package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User createUser(User user);

    public Optional<User> getUserById(int id);

    public List<User> getUsers();

    public User updateUser(int userId, User user);

    public void deleteUser(int id);
}
