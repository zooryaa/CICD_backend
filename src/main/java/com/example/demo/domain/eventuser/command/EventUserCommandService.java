package com.example.demo.domain.eventuser.command;

import com.example.demo.core.generic.AbstractCommandServiceImpl;
import com.example.demo.core.generic.StatusOr;
import com.example.demo.domain.event.Event;
import com.example.demo.domain.event.EventRepository;
import com.example.demo.domain.event.query.EventQueryService;
import com.example.demo.domain.eventuser.EventUser;
import com.example.demo.domain.eventuser.EventUserRepository;
import com.example.demo.domain.recommender.Gorse;
import com.example.demo.domain.user.User;
import com.example.demo.domain.user.UserRepository;
import io.gorse.gorse4j.Feedback;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@Log4j2
public class EventUserCommandService extends AbstractCommandServiceImpl<EventUser> {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final EventUserRepository eventUserRepository;
    private final EventQueryService eventQueryService;

    private final Gorse client;

    @Autowired
    protected EventUserCommandService(EventUserRepository repository, UserRepository userRepository, EventRepository eventRepository, EventUserRepository eventUserRepository, EventQueryService eventQueryService, Gorse client) {
        super(repository);
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.eventUserRepository = eventUserRepository;
        this.eventQueryService = eventQueryService;
        this.client = client;
    }

    HttpStatus convertNumberOfDeletedEntitiesToStatus(int numberOfEntities) {
        if (numberOfEntities == 0) {
            return HttpStatus.BAD_REQUEST; // nothing was deleted, and we expect something to be deleted
        }
        return HttpStatus.OK;
    }

    HttpStatus deleteUserFromEvent(UUID userId, UUID eventId) {
        Optional<User> user = userRepository.findById(userId);
        Optional<Event> event = eventRepository.findById(eventId);

        boolean areEntitiesNotFound = areEventAndUserNotFound(event, user);

        if (areEntitiesNotFound) {
            return HttpStatus.NOT_FOUND;
        }

        int numberOfDeletedEntities = eventUserRepository.deleteByUserAndEvent(user.get(), event.get());
        return convertNumberOfDeletedEntitiesToStatus(numberOfDeletedEntities);
    }

    boolean areEventAndUserNotFound(Optional<Event> event, Optional<User> user) {
        return user.isEmpty() || event.isEmpty();
    }

    HttpStatus getEventUserStatus(Optional<Event> event, Optional<User> user) {
        if (areEventAndUserNotFound(event, user)) {
            return HttpStatus.NOT_FOUND;
        }

        if (user.get().getRoles().stream().anyMatch(role -> role.getName().equals("ADMIN"))) {
            return HttpStatus.BAD_REQUEST;
        }

        if (eventUserRepository.existsByUserAndEvent(user.get(), event.get())) {
            return HttpStatus.CONFLICT;
        }

        return HttpStatus.OK;
    }

    /**
     * Register a user for an event
     * @param userId Which user should be registered
     * @param eventId For which event should the user be registered
     * @param shouldAddFeedbacks Whether this enlisment should be used for the recommendation engine.
     *                           -> If the user performed the action, then yes
     *                           -> In other cases, another personen enlisted the user, so the feedback should not be used.
     * @return Either an error code or if everything went well, the created enlistment
     * @throws IOException Gets thrown if something goes wrong in inserting the feedback
     */
    StatusOr<EventUser> registerUserForEvent(UUID userId, UUID eventId, boolean shouldAddFeedbacks) throws IOException {
        if(shouldAddFeedbacks) {
            List<Feedback> feedbacks = List.of(
                    new Feedback("registerEvent", userId.toString(), eventId.toString(),
                            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyy-MM-dd hh:mm:ss +01:00")))
            );
            client.insertFeedback(feedbacks);
        }

        Optional<User> user = userRepository.findById(userId);
        Optional<Event> event = eventRepository.findById(eventId);

        HttpStatus validationStatus = getEventUserStatus(event, user);

        if (validationStatus.isError()) {
            log.error("Request has wrong form. Failed with status: " + validationStatus.toString());
            return new StatusOr<>(validationStatus);
        }

        EventUser eventUser = new EventUser(user.get(), event.get());
        eventUser = eventUserRepository.save(eventUser);

        return new StatusOr<>(eventUser);
    }

    String getMessageForUserEventDeletion(HttpStatus status) {
        if (!status.isError()) {
            return "The request succeeded.";
        }
        switch (status) {
            case BAD_REQUEST -> {
                return "Unknown error - most likely, the to-delete entity was not found.";
            }
            case NOT_FOUND -> {
                return "Either the user or the event have not been found.";
            }
        }
        return "Can't create error message - HttpStatus is not known";
    }

    String getMessageForEventRegistration(HttpStatus status) {
        if (!status.isError()) {
            return "The request succeeded.";
        }
        switch (status) {
            case TOO_MANY_REQUESTS -> {
                return "The number of participants for the have been succeeded.";
            }
            case NOT_FOUND -> {
                return "Either the user or the event have not been found.";
            }
            case BAD_REQUEST -> {
                return "Admins aren't allowed to enlist in an event. Please use a personal account.";
            }
            case CONFLICT -> {
                return "The requested user is already enrolled in the event.";
            }
        }
        return "Can't create error message - HttpStatus is not known";
    }

    public Event createManyEnrollmentsForEvent(UUID eventId, UUID[] userIds) throws IOException {
        for(UUID userId: userIds) {
            registerUserForEvent(userId, eventId, false);
        }

        return eventRepository.findById(eventId).orElseThrow(() -> {
            log.error("Tried to create enrollments for non-existing event");
            return new NoSuchElementException("Unable to find event with id: " + eventId.toString());
        });
    }
}
