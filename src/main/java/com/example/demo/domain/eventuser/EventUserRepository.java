package com.example.demo.domain.eventuser;

import com.example.demo.core.generic.AbstractRepository;
import com.example.demo.domain.event.Event;
import com.example.demo.domain.user.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Repository
public interface EventUserRepository extends AbstractRepository<EventUser> {
    long deleteByUserInAndEventIn(Collection<User> users, Collection<Event> events);

    @Transactional
    @Modifying
    @Query("delete from EventUser e where e.user = ?1 and e.event = ?2")
    int deleteByUserAndEvent(User user, Event event);

    List<EventUser> findAllByEvent(Event event, PageRequest pageReqest);

    boolean existsByUserAndEvent(User user, Event event);

    List<EventUser> findAllByEvent(Event event);

    void deleteByEvent(Event event);
    void deleteByUser(User user);
}
