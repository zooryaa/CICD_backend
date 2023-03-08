package com.example.demo.domain.event.command;

import com.example.demo.domain.event.Event;
import com.example.demo.domain.event.EventRepository;
import com.example.demo.domain.event.asyncevents.EventCreatedMessage;
import com.example.demo.domain.eventuser.EventUserRepository;
import com.example.demo.domain.user.query.UserQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class EventCommandService {
    private final EventRepository eventRepository;
    private final EventUserRepository eventUserRepository;
    private final UserQueryService userQueryService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public EventCommandService(EventRepository eventRepository, EventUserRepository eventUserRepository, UserQueryService userQueryService, ApplicationEventPublisher applicationEventPublisher) {
        this.eventRepository = eventRepository;
        this.eventUserRepository = eventUserRepository;
        this.userQueryService = userQueryService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void publishEventCreationEvent(Event event) {
        System.out.println("Publishing custom event. ");
        EventCreatedMessage customSpringEvent = new EventCreatedMessage(this, event, LocalDateTime.now());
        applicationEventPublisher.publishEvent(customSpringEvent);

    }

    public Event createEvent(Event fromDTO, Principal principal) {
        publishEventCreationEvent(fromDTO);
        fromDTO.setEventOwner(userQueryService.findByEmail(principal.getName()));
        return eventRepository.save(fromDTO);
    }

    public Event updateEvent(Event fromDTO, UUID id) {
        fromDTO.setId(id);
        return eventRepository.save(fromDTO);
    }

    @Transactional
    public UUID deleteEvent(UUID id) {
        Optional<Event> toDeleteEvent = eventRepository.findById(id);

        if (toDeleteEvent.isEmpty()) {
            return id;
        }
        eventUserRepository.deleteByEvent(toDeleteEvent.get());
        eventRepository.deleteById(id);
        return id;
    }
}
