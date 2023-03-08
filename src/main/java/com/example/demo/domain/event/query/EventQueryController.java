package com.example.demo.domain.event.query;

import com.example.demo.domain.event.dto.EventDTO;
import com.example.demo.domain.event.dto.EventMapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@Log4j2
@RequestMapping("/event")
public class EventQueryController {
    private final EventQueryService eventQueryService;
    private final EventMapper eventMapper;


    @Autowired
    public EventQueryController(EventQueryService eventQueryService, EventMapper eventMapper) {
        this.eventQueryService = eventQueryService;
        this.eventMapper = eventMapper;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN_READ') || (@userPermissionEvaluator.isUser(authentication.principal.user, #userId))")
    @Operation(summary = "Get all events")
    public ResponseEntity<List<EventDTO>> getEvents(@RequestParam(value = "user_id", required = false) Optional<UUID> userId) {
        userId.ifPresentOrElse(uuid -> log.info("Getting all events for user: " + uuid),
                () -> log.info("Getting all events"));

        return ResponseEntity.ok().body(eventMapper.toDTOs(eventQueryService.getEvents(userId)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get event by id")
    public ResponseEntity<EventDTO> getEvent(@PathVariable UUID id, Principal principal) throws IOException {
        log.info("Load event: " + id.toString());
        return ResponseEntity.ok().body(eventMapper.toDTO(eventQueryService.getEvent(id, principal.getName())));
    }

    @GetMapping("/pageCount/{pageLength}")
    @Operation(summary = "Get number of pages for page length")
    public ResponseEntity<Double> getPageCount(@PathVariable Integer pageLength, Principal principal) {
        return ResponseEntity.ok().body(eventQueryService.getPageCount(pageLength, principal));
    }
}
