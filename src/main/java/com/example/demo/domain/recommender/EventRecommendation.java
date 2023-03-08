package com.example.demo.domain.recommender;


import com.example.demo.domain.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventRecommendation {
    private String eventName;
    private boolean isCurrentUserEnrolled;
    private String imageUrl;

    private UUID eventId;


    public EventRecommendation(boolean isCurrentUserEnrolled, Event event, UUID userId) {
        this.isCurrentUserEnrolled = isCurrentUserEnrolled;
        eventName = event.getEventName();
        imageUrl = "https://hips.hearstapps.com/hmg-prod/images/wolf-dog-breeds-siberian-husky-1570411330.jpg? er,top&resize=1200:*";
        eventId = event.getId();
    }
}
