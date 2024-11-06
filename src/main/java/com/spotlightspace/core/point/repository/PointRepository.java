package com.spotlightspace.core.point.repository;

import static com.spotlightspace.common.exception.ErrorCode.POINT_NOT_FOUND;

import com.spotlightspace.common.exception.ApplicationException;
import com.spotlightspace.core.point.domain.Point;
import com.spotlightspace.core.user.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PointRepository extends JpaRepository<Point, Long> {

    Optional<Point> findByUser(User user);

    @Query("select p from Point p join fetch p.user where p.user in :users")
    List<Point> findPointsByUserIn(@Param("users") List<User> users);

    default Point findByUserOrElseThrow(User user) {
        return findByUser(user)
                .orElseThrow(() -> new ApplicationException(POINT_NOT_FOUND));
    }
}
