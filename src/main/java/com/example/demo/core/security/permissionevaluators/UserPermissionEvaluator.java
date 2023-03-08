package com.example.demo.core.security.permissionevaluators;

import com.example.demo.domain.event.EventRepository;
import com.example.demo.domain.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class UserPermissionEvaluator {
    private EventRepository eventRepository;

    @Autowired
    public UserPermissionEvaluator(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public UserPermissionEvaluator() {
    }

    public boolean isUserAboveAge(User principal, int age) {
        return true;
    }

    public boolean isUser(User principal, UUID uuid) {
        return principal.getId().equals(uuid);
    }

    public boolean isUser(User principal, Optional<UUID> uuid) {
        return uuid.isEmpty() || principal.getId().equals(uuid);
    }


    public boolean isEventOwner(User principal, UUID eventUuid) {
        eventRepository.findById(eventUuid).ifPresent(event -> {
            if (!event.getEventOwner().getId().equals(principal.getId())) {
                throw new RuntimeException("User is not owner of event");
            }
        });
        return true;
    }
}
