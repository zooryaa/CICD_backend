package com.example.demo.domain.eventuser.query;

import com.example.demo.core.adapter.LocalDateTimeAdapter;
import com.example.demo.domain.event.Event;
import com.example.demo.domain.event.dto.EventDTO;
import com.example.demo.domain.event.dto.EventMapper;
import com.example.demo.domain.user.User;
import com.example.demo.domain.user.dto.UserMapper;
import com.example.demo.domain.user.query.UserQueryService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Validated
@Log4j2
@RestController
@RequestMapping("/eventUser")
public class EventUserQueryController {
    private final EventUserQueryService eventUserQueryService;
    private final UserQueryService userQueryService;
    private final EventMapper eventMapper;
    private final UserMapper userMapper;

    @Autowired
    public EventUserQueryController(EventUserQueryService eventUserQueryService, UserQueryService userQueryService, EventMapper eventMapper, UserMapper userMapper) {
        this.eventUserQueryService = eventUserQueryService;
        this.userQueryService = userQueryService;
        this.eventMapper = eventMapper;
        this.userMapper = userMapper;
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get Events for User")
    @PreAuthorize("@userPermissionEvaluator.isUser(authentication.principal.user, #userId) || hasAuthority('ADMIN_READ')")
    public ResponseEntity<List<EventDTO>> getAllEventsOfUser(@PathVariable("userId") UUID userId,
                                                             @RequestParam(value = "event_start", required = false) Optional<Integer> eventStart) {
        log.info(String.format("Getting all events for user(%s)", userId.toString()));
        List<Event> eventsOfUser = eventUserQueryService.getAllEventsOfUser(userId, eventStart);

        return ResponseEntity.ok()
                .body(eventMapper.toDTOs(eventsOfUser));
    }

    @GetMapping("/event/{eventId}")
    @Operation(summary = "Get all participants in an event")
    @PreAuthorize("@userPermissionEvaluator.isEventOwner(authentication.principal.user, #eventId) || hasAuthority('ADMIN_READ')")
    public ResponseEntity<String> getAllParticipantsInEvent(@PathVariable("eventId") UUID eventId,
                                                            @RequestParam("page") int page,
                                                            @RequestParam("pageLength") int pageLength,
                                                            Principal requester) {
        User user = userQueryService.findByEmail(requester.getName());
        log.info(String.format("Getting all participants for event(%s). Request started by userId(%s)", eventId.toString(), user.getUserId().toString()));

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .body(gson.toJson(userMapper.toDTOs(eventUserQueryService.getAllParticipantsOfEvent(eventId, page, pageLength))));
    }
}

