package com.example.demo.domain.event.query;


import com.example.demo.core.exception.EventNotFoundException;
import com.example.demo.core.generic.AbstractQueryServiceImpl;
import com.example.demo.domain.event.Event;
import com.example.demo.domain.event.EventRepository;
import com.example.demo.domain.recommender.Gorse;
import com.example.demo.domain.user.query.UserQueryService;
import io.gorse.gorse4j.Feedback;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Log4j2
public class EventQueryService extends AbstractQueryServiceImpl<Event> {
    private final Gorse client;

    private final UserQueryService userQueryService;

    @Autowired
    public EventQueryService(EventRepository repository, Gorse client, UserQueryService userQueryService) {
        super(repository);
        this.client = client;
        this.userQueryService = userQueryService;
    }

    public List<Event> getEvents(Optional<UUID> userId) {
        return repository
                .findAll()
                .stream()
                .filter(event -> userId.isEmpty() || event.getEventOwner().getId().equals(userId.get()))
                .toList();
    }

    public Event getEvent(UUID id, String email) throws IOException {
        log.info(String.format("Up-serting viewing-feedback of event(%s) by user(%s)", id.toString(), email));
        List<Feedback> feedbacks = List.of(
                new Feedback("viewEvent", userQueryService.findByEmail(email).getId().toString(), id.toString(),
                        LocalDateTime.now().toString())
        );
        client.insertFeedback(feedbacks);
        return repository
                .findById(id)
                .orElseThrow(() -> new EventNotFoundException("event with id: " + id + " not found"));
    }

    public double getPageCount(Integer pageLength, Principal principal) {
        if (pageLength == 0) {
            return 1;
        }

        UUID userId = userQueryService.findByEmail(principal.getName()).getId();

        return Math.ceil(((EventRepository) repository)
                .countByEventOwner_IdNotIn(List.of(userId)) / (float) pageLength);
    }
}
