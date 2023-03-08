package com.example.demo.domain.eventuser.command;

import com.example.demo.core.adapter.LocalDateTimeAdapter;
import com.example.demo.core.exception.NotCheckedException;
import com.example.demo.core.generic.StatusOr;
import com.example.demo.domain.event.dto.EventDTO;
import com.example.demo.domain.event.dto.EventMapper;
import com.example.demo.domain.eventuser.EventUser;
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

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Validated
@RestController
@Log4j2
@RequestMapping("/eventUser")
public class EventUserCommandController {
    private final EventUserCommandService eventUserCommandService;
    private final EventMapper eventMapper;

    @Autowired
    public EventUserCommandController(EventUserCommandService eventUserCommandService, EventMapper eventMapper) {
        this.eventUserCommandService = eventUserCommandService;
        this.eventMapper = eventMapper;
    }

    /**
     * This method is used to create many event users. Each userId in the userIds will get enrolled in the event (provided via eventId)
     * @param eventId For which event should the users be enrolled
     * @param userIds Which users should be enrolled in the event.
     * @return The event into which the users have been enrolled.
     * @throws IOException If the connection to the recommendation engine fails, this exception get's thrown.
     */
    @PostMapping("/{eventId}")
    @Operation(summary="Create many EventUser")
    public ResponseEntity<EventDTO> signManyUserUpForEvent(@PathVariable("eventId") UUID eventId,
                                                           @RequestBody UUID[] userIds) throws IOException {
        log.info(String.format("Creating many enrollments in event(%s)", eventId.toString()));

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(eventMapper.toDTO(eventUserCommandService.createManyEnrollmentsForEvent(eventId, userIds)));
    }

    /**
     * This endpoint signes a specified user
     * @param userId
     * @param eventId
     * @return
     * @throws NotCheckedException
     * @throws IOException
     */
    @PostMapping
    @Operation(summary = "Create EventUser")
    @PreAuthorize("hasAuthority('USER_MODIFY') || @userPermissionEvaluator.isUser(authentication.principal.user, #userId)" +
            "|| @userPermissionEvaluator.isEventOwner(authentication.principal.user, #eventId)")
    public ResponseEntity<String> signUserUpForEvent(@RequestParam("user_id") UUID userId,
                                                     @RequestParam("event_id") UUID eventId) throws NotCheckedException, IOException {
        log.info(String.format("Enrolling user: %s in event %s", userId.toString(), eventId.toString()));
        StatusOr<EventUser> eventRegistration = eventUserCommandService.registerUserForEvent(userId, eventId, true);

        if (!eventRegistration.isOkAndPresent()) {
            return ResponseEntity
                    .status(eventRegistration.getStatus())
                    .body(eventUserCommandService.getMessageForEventRegistration(eventRegistration.getStatus()));
        }

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();

        return ResponseEntity
                .status(eventRegistration.getStatus())
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .body(gson.toJson(eventRegistration.getItem()));
    }

    @DeleteMapping
    @Operation(summary = "Delete EventUser")
    @PreAuthorize("hasRole('ADMIN') || (@userPermissionEvaluator.isUser(authentication.principal.user, #userId) ||" +
            "@userPermissionEvaluator.isEventOwner(authentication.principal.user, #eventId))")
    public ResponseEntity<String> deleteUserFromEvent(@RequestParam("user_id") UUID userId,
                                                      @RequestParam("event_id") UUID eventId) {
        log.info(String.format("De-enrolling user: %s and event: %s", userId.toString(), eventId.toString()));

        HttpStatus status = eventUserCommandService.deleteUserFromEvent(userId, eventId);

        return ResponseEntity.status(status)
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .body(eventUserCommandService.getMessageForUserEventDeletion(status));
    }
}
