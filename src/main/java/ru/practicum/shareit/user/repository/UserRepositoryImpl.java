package ru.practicum.shareit.user.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class UserRepositoryImpl implements UserRepository{

    private Map<Integer, User> users;

    public User createUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    public Optional<User> getUserById(int id) {
        return Optional.ofNullable(users.get(id));
    }

    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    public User updateUser(int userId, User user) {
        User changedUser = users.get(userId);
        if (!(user.getName() == null)) {
            changedUser.setName(user.getName());
        }
        if (!(user.getEmail() == null)) {
            changedUser.setEmail(user.getEmail());
        }
        return changedUser;
    }

    public void deleteUser(int id) {
        users.remove(id);
    }
}
