package com.example.demo.domain.eventuser;

import com.example.demo.core.generic.AbstractEntity;
import com.example.demo.domain.event.Event;
import com.example.demo.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventUser extends AbstractEntity {
    @ManyToOne
    private User user;
    @ManyToOne
    private Event event;
}
