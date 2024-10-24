package com.spotlightspace.core.event.repository;

import com.spotlightspace.common.exception.ApplicationException;
import com.spotlightspace.core.event.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import static com.spotlightspace.common.exception.ErrorCode.EVENT_NOT_FOUND;

public interface EventRepository extends JpaRepository<Event, Long>, EventQueryRepository {

    Optional<Event> findByIdAndUserId(Long id, Long userId);

    default Event findByIdOrElseThrow(long id) {
        return findById(id)
                .orElseThrow(() -> new ApplicationException(EVENT_NOT_FOUND));
    }

    default Event findByIdAndUserIdOrElseThrow(Long id, Long userId) {
        return findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ApplicationException(EVENT_NOT_FOUND));
    }
}
