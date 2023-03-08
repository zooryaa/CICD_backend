package com.example.demo.domain.user.command;

import com.example.demo.core.generic.AbstractCommandServiceImpl;
import com.example.demo.domain.event.EventRepository;
import com.example.demo.domain.eventuser.EventUserRepository;
import com.example.demo.domain.recommender.Gorse;
import com.example.demo.domain.user.User;
import com.example.demo.domain.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserCommandServiceImpl extends AbstractCommandServiceImpl<User> implements UserCommandService {

    private final PasswordEncoder passwordEncoder;
    private final Gorse client;
    private final EventUserRepository eventUserRepository;
    private final EventRepository eventRepository;

    @Autowired
    public UserCommandServiceImpl(UserRepository repository, PasswordEncoder passwordEncoder, Gorse client, EventUserRepository eventUserRepository, EventRepository eventRepository) {
        super(repository);
        this.passwordEncoder = passwordEncoder;
        this.client = client;
        this.eventUserRepository = eventUserRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    public User register(User user) throws IOException {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setUserId(UUID.randomUUID());
        user = save(user);

        client.insertUser(new io.gorse.gorse4j.User(
                user.getId().toString(), Collections.emptyList()
        ));

        return user;
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
       User user = repository.findById(id).orElseThrow(() -> new NoSuchElementException("The requested user can't be found."));
       eventRepository.deleteByEventOwner(user);
       eventUserRepository.deleteByUser(user);
       repository.deleteById(id);
    }

    @Override
    public User updateUserById(UUID id, User fromDTO) {
        Optional<User> toUpdateUser = repository.findById(id);
        if(toUpdateUser.isEmpty()) {
            throw new NoSuchElementException("Unable to find the provided user. Use the POST endpoint to create one.");
        }

        if(fromDTO.getRoles() == null) {
            fromDTO.setRoles(fromDTO.getRoles());
        }

        fromDTO.setId(toUpdateUser.get().getId());
        fromDTO.setPassword(toUpdateUser.get().getPassword()); //Don't set password to null.
        return repository.save(fromDTO);
    }
}
