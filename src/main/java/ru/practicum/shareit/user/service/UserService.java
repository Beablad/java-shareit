package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    public User createUser(User user);

    public User getUserById(int id);

    public List<User> getUsers();

    public User updateUser(int userId, User user);

    public void deleteUser(int id);
}
