package ru.mfa.rbpo2026.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mfa.rbpo2026.entity.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final ShipmentService shipmentService;

    Map<UUID, User> users = new HashMap<>();

    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    public User getUser(UUID id) {
        return users.get(id);
    }

    public User addUser(User user) {
        user.setId(UUID.randomUUID());
        users.put(user.getId(), user);
        return user;
    }

    public User updateUser(UUID id, User patch) {
        var user = users.get(id);
        if (user == null) {
            return null;
        }
        if (patch.getName() != null) user.setName(patch.getName());
        if (patch.getEmail() != null) user.setEmail(patch.getEmail());
        return user;
    }

    public void deleteUser(UUID id) {
        shipmentService.deleteBySenderId(id);
        users.remove(id);
    }

}