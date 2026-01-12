package com.miniioc.examples.simpleapp.service;

import com.miniioc.core.annotation.injection.Service;
import com.miniioc.core.annotation.injection.Scope;
import com.miniioc.core.annotation.injection.ScopeType;
import com.miniioc.examples.simpleapp.model.User;

import java.util.*;

@Service
@Scope(ScopeType.PROTOTYPE)
public class UserService {
    private final Map<Integer, User> users = new HashMap<>();
    private int nextId = 1;

    public User createUser(String name, String email) {
        User user = new User(nextId++, name, email);
        users.put(user.getId(), user);
        return user;
    }

    public User getUserById(int id) {
        return users.get(id);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public boolean deleteUser(int id) {
        return users.remove(id) != null;
    }
}
