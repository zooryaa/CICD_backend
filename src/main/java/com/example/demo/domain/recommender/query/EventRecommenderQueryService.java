package com.example.demo.domain.recommender.query;


import com.example.demo.core.redis.JedisPoolWrapper;
import com.example.demo.domain.event.Event;
import com.example.demo.domain.event.EventRepository;
import com.example.demo.domain.eventuser.EventUserRepository;
import com.example.demo.domain.recommender.EventRecommendation;
import com.example.demo.domain.recommender.Gorse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Log4j2
public class EventRecommenderQueryService {
    private final EventRepository eventRepository;

    private final EventUserRepository eventUserRepository;

    private final Gorse gorse;

    private final JedisPoolWrapper jedisPool;

    @Value("${recommender.maxNumberOfEventsRemovalAttemps}")
    private int maxNumberOfEventsRemovalAttemps;

    @Autowired
    public EventRecommenderQueryService(EventRepository eventRepository, EventUserRepository eventUserRepository, Gorse gorse, JedisPoolWrapper jedisPool) {
        this.eventRepository = eventRepository;
        this.eventUserRepository = eventUserRepository;
        this.gorse = gorse;
        this.jedisPool = jedisPool;
    }

    private boolean isCurrentUserEnrolledInEvent(String userId, Event event) {
        return !eventUserRepository.findAllByEvent(event)
                .stream()
                .filter(eventuser -> eventuser.getUser().getId().toString().equals(userId))
                .toList().isEmpty();
    }

    private List<Event> getEventsFromRecommendations(List<String> recommendations) throws IOException {
        List<Event> events = new ArrayList<>();

        for (String recommendation : recommendations) {
            Optional<Event> recommendationEvent = eventRepository.findById(UUID.fromString(recommendation));

            if (recommendationEvent.isPresent()) {
                events.add(recommendationEvent.get());
            } else {
                log.error("Got recommended an inexisting item: " + recommendation);
                gorse.deleteItem(recommendation);
            }
        }

        return events;
    }

    private List<Event> getUserOwnedEvents(List<Event> allEvents, String userId) {
        return allEvents
                .stream()
                .filter(event -> event.getEventOwner().getUserId().toString().equals(userId))
                .toList();
    }

    private boolean replaceUserOwnedEvents(List<Event> allEvents, String userId, int page, int pageLength, int numberOfUserEventsRemovalAttemps) throws IOException {
        List<Event> userOwnedEvents = getUserOwnedEvents(allEvents, userId);
        log.info("Found " + userOwnedEvents.size() + " events created by current user");
        allEvents.removeAll(userOwnedEvents);

        if (userOwnedEvents.isEmpty()) {
            return true;
        }

        if (numberOfUserEventsRemovalAttemps >= maxNumberOfEventsRemovalAttemps) {
            return false;
        }

        int currentUserOffset = 0;
        try (Jedis jedis = jedisPool.getJedisPool().getResource()) {
            currentUserOffset = Integer.parseInt(jedis.get("user_recs_" + userId));
            log.info("Successfully fetched number of self events");
        } catch (RuntimeException ignore) {
            log.info("Miss on redis cache with userid");
        }
        try (Jedis jedis = jedisPool.getJedisPool().getResource()) {
            jedis.set("user_recs_" + userId, String.valueOf(userOwnedEvents.size() + currentUserOffset));
        }

        List<String> potentiallyCleanedRecs = gorse.getRecommend(userId, page, pageLength, userOwnedEvents.size() + currentUserOffset);

        allEvents.addAll(
                getEventsFromRecommendations(potentiallyCleanedRecs)
        );

        return replaceUserOwnedEvents(allEvents, userId, page, pageLength, ++numberOfUserEventsRemovalAttemps);
    }

    public List<EventRecommendation> getRecommendationForUser(String userId, int page, int pageLength) throws IOException {
        List<String> recommendations = gorse.getRecommend(userId, pageLength, page);
        log.info("Recommmmendations raw, unfiltered. Length: " + recommendations.size());

        List<Event> events = getEventsFromRecommendations(recommendations);
        boolean wasAbleToReplaceAllUsersEvent = replaceUserOwnedEvents(events, userId, page, pageLength, 0);

        if (wasAbleToReplaceAllUsersEvent) {
            log.info("Successfully got the recommendations.");
        } else {
            log.warn("Unable to replace all user-owned events with configured max retries. " +
                    "Consider either increasing the limit or check the distribution of creators <-> # of events owned");
        }

        return events
                .stream()
                .map(event -> new EventRecommendation(
                        isCurrentUserEnrolledInEvent(userId, event),
                        event, UUID.fromString(userId)))
                .distinct()
                .toList();
    }
}
