package com.example.demo.domain.eventuser.query;

import com.example.demo.core.generic.AbstractQueryServiceImpl;
import com.example.demo.domain.event.Event;
import com.example.demo.domain.event.EventRepository;
import com.example.demo.domain.eventuser.EventUser;
import com.example.demo.domain.eventuser.EventUserRepository;
import com.example.demo.domain.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
public class EventUserQueryService extends AbstractQueryServiceImpl<EventUser> {
    private final EventRepository eventRepository;

    @Autowired
    public EventUserQueryService(EventUserRepository repository, EventRepository eventRepository) {
        super(repository);
        this.eventRepository = eventRepository;
    }

    private List<Event> getAllEventsOfUser(UUID user) {
        return repository
                .findAll()
                .stream()
                .map(EventUser::getEvent)
                .toList();
    }

    List<Event> getAllEventsOfUser(UUID userId,
                                   Optional<Integer> event_start) {
        return getAllEventsOfUser(userId)
                .stream()
                .filter(event -> event.getStartDate()
                        .isAfter(LocalDateTime.ofEpochSecond(event_start.orElse(0), 0, ZoneOffset.of("+1"))))
                .toList();
    }

    private boolean areAnyUserRoles(String roleName, User user) {
        return !user.getRoles()
                .stream()
                .filter(role -> role.getName().equals(roleName))
                .toList()
                .isEmpty();
    }

    public List<User> getAllParticipantsOfEvent(UUID eventId, int page, int pageLength) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchElementException("Unable to find event with id: " + eventId));

        return ((EventUserRepository) repository).findAllByEvent(event, PageRequest.of(page, pageLength))
                .stream()
                .map(EventUser::getUser)
                .toList();
    }
}
