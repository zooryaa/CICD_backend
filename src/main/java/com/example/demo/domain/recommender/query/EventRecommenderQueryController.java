package com.example.demo.domain.recommender.query;

import com.example.demo.domain.recommender.EventRecommendation;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@Log4j2
@RequestMapping("/recommendation")
public class EventRecommenderQueryController {
    private final EventRecommenderQueryService eventRecommenderQueryService;

    @Autowired
    public EventRecommenderQueryController(EventRecommenderQueryService eventRecommenderQueryService) {
        this.eventRecommenderQueryService = eventRecommenderQueryService;
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get Recommendations for User")
//    @PreAuthorize("@userPermissionEvaluator.isUser(authentication.principal.user, #userId)")
    public ResponseEntity<List<EventRecommendation>> getRecommendationsForUser(@PathVariable("userId") String userId,
                                                                               @RequestParam("page") int page,
                                                                               @RequestParam("pageLength") int pageLength) throws IOException {
        log.info(String.format("Getting paged recommendations for user(%s) with page (%d) and pageLength (%d)",
                userId, page, pageLength));
        return ResponseEntity.ok().body(
                eventRecommenderQueryService.getRecommendationForUser(userId, page, pageLength)
        );
    }
}
