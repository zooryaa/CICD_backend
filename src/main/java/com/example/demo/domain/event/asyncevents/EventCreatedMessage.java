package com.example.demo.domain.event.asyncevents;

import com.example.demo.domain.event.Event;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;


public class EventCreatedMessage extends ApplicationEvent {
    private final Event event;
    private final LocalDateTime eventCreationTime;


    public EventCreatedMessage(Object source, Event event, LocalDateTime eventCreationTime) {
        super(source);
        this.event = event;
        this.eventCreationTime = eventCreationTime;
    }

    public Event getEvent() {
        return event;
    }

    public LocalDateTime getEventCreationTime() {
        return eventCreationTime;
    }
}
