package ru.practicum.shareit.user.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepositoryImpl;

import javax.validation.ValidationException;
import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserServiceImpl implements UserService {

    UserRepositoryImpl userRepositoryImpl;
    int id;

    public UserServiceImpl(UserRepositoryImpl userRepositoryImpl) {
        this.userRepositoryImpl = userRepositoryImpl;
        id = 1;
    }

    @Override
    public User createUser(User user) {
        for (User checkedUser : userRepositoryImpl.getUsers()) {
            if (checkedUser.getEmail().equals(user.getEmail())) {
                throw new ValidationException("Ошибка валидации");
            }
        }
        user.setId(getNextId());
        return userRepositoryImpl.createUser(user);
    }

    @Override
    public User getUserById(int id) {
        return userRepositoryImpl.getUserById(id).orElseThrow(() -> new NotFoundException("Неверный идентификатор"));
    }

    @Override
    public List<User> getUsers() {
        return userRepositoryImpl.getUsers();
    }

    @Override
    public User updateUser(int userId, User user) {
        for (User checkedUser : userRepositoryImpl.getUsers()) {
            if (checkedUser.getEmail().equals(user.getEmail())) {
                throw new ValidationException("Ошибка валидации");
            }
        }
        return userRepositoryImpl.updateUser(userId, user);
    }

    @Override
    public void deleteUser(int id) {
        userRepositoryImpl.deleteUser(id);
    }

    private int getNextId() {
        return id++;
    }
}
