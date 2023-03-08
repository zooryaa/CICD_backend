package com.example.demo.domain.event;

import com.example.demo.core.generic.AbstractRepository;
import com.example.demo.domain.user.User;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.UUID;

@Repository
public interface EventRepository extends AbstractRepository<Event> {
    void deleteByEventOwner(User eventOwner);
    long countByEventOwner_IdNotIn(Collection<UUID> ids);

}
