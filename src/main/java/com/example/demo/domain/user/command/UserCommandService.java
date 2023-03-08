package com.example.demo.domain.user.command;

import com.example.demo.core.generic.AbstractCommandService;
import com.example.demo.domain.user.User;

import java.io.IOException;
import java.util.UUID;

public interface UserCommandService extends AbstractCommandService<User> {
    User register(User user) throws IOException;

    void deleteUser(UUID id);

    User updateUserById(UUID id, User fromDTO);
}
