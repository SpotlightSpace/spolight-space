package com.spotlightspace.core.admin.repository;


import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spotlightspace.core.admin.dto.responsedto.AdminCouponResponseDto;
import com.spotlightspace.core.admin.dto.responsedto.AdminEventResponseDto;
import com.spotlightspace.core.admin.dto.responsedto.AdminReviewResponseDto;
import com.spotlightspace.core.admin.dto.responsedto.AdminUserResponseDto;
import com.spotlightspace.core.coupon.domain.Coupon;
import com.spotlightspace.core.coupon.domain.QCoupon;
import com.spotlightspace.core.event.domain.Event;
import com.spotlightspace.core.event.domain.QEvent;
import com.spotlightspace.core.review.domain.QReview;
import com.spotlightspace.core.review.domain.Review;
import com.spotlightspace.core.user.domain.QUser;
import com.spotlightspace.core.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.spotlightspace.core.coupon.domain.QCoupon.coupon;
import static com.spotlightspace.core.event.domain.QEvent.event;
import static com.spotlightspace.core.review.domain.QReview.review;
import static com.spotlightspace.core.user.domain.QUser.user;

@Repository
@RequiredArgsConstructor
public class AdminQueryRepositoryImpl implements AdminQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<AdminUserResponseDto> getAdminUsers(String keyword, Pageable pageable) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
        pageable.getSort().forEach(order -> {
            PathBuilder<?> entityPath = new PathBuilder<>(user.getType(), user.getMetadata());
            orderSpecifiers.add(new OrderSpecifier(
                    order.isAscending() ? Order.ASC : Order.DESC,
                    entityPath.get(order.getProperty())
            ));
        });

        // 쿼리 실행 및 페이징 적용
        List<Tuple> tuples = queryFactory
                .select(user.id, user.email, user.nickname, user.phoneNumber, user.role.stringValue(), user.isDeleted)
                .from(user)
                .where(keywordContainsForUser(keyword))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]))
                .fetch();

        // Tuple 데이터를 DTO로 매핑
        List<AdminUserResponseDto> results = tuples.stream()
                .map(tuple -> AdminUserResponseDto.of(
                        tuple.get(user.id),
                        tuple.get(user.email),
                        tuple.get(user.nickname),
                        tuple.get(user.phoneNumber),
                        tuple.get(user.role.stringValue()),
                        tuple.get(user.isDeleted)))
                .collect(Collectors.toList());

        long totalCount = queryFactory
                .select(Wildcard.count)
                .from(user)
                .where(keywordContainsForUser(keyword))
                .fetchOne();

        return new PageImpl<>(results, pageable, totalCount);
    }

    @Override
    public Page<AdminEventResponseDto> getAdminEvents(String keyword, Pageable pageable) {
        // 정렬을 위한 OrderSpecifier 목록 생성
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
        pageable.getSort().forEach(order -> {
            PathBuilder<?> entityPath = new PathBuilder<>(event.getType(), event.getMetadata());
            orderSpecifiers.add(new OrderSpecifier(
                    order.isAscending() ? Order.ASC : Order.DESC,
                    entityPath.get(order.getProperty())
            ));
        });

        // 쿼리 실행 및 페이징 적용
        List<Tuple> tuples = queryFactory
                .select(event.id, event.title, event.content, event.location, event.startAt, event.endAt,
                        event.maxPeople, event.price, event.category.stringValue(), event.recruitmentStartAt,
                        event.recruitmentFinishAt, event.isDeleted)
                .from(event)
                .where(keywordContainsForEvent(keyword)) // 검색어 조건 추가
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orderSpecifiers.toArray(new OrderSpecifier[0])) // 정렬 조건 적용
                .fetch();

        // Tuple 데이터를 DTO로 매핑
        List<AdminEventResponseDto> results = tuples.stream()
                .map(tuple -> AdminEventResponseDto.of(
                        tuple.get(event.id),
                        tuple.get(event.title),
                        tuple.get(event.content),
                        tuple.get(event.location),
                        tuple.get(event.startAt),
                        tuple.get(event.endAt),
                        tuple.get(event.maxPeople),
                        tuple.get(event.price),
                        tuple.get(event.category.stringValue()),
                        tuple.get(event.recruitmentStartAt),
                        tuple.get(event.recruitmentFinishAt),
                        tuple.get(event.isDeleted)))
                .collect(Collectors.toList());

        long totalCount = queryFactory
                .select(Wildcard.count)
                .from(event)
                .where(keywordContainsForEvent(keyword))
                .fetchOne();

        return new PageImpl<>(results, pageable, totalCount);
    }


    @Override
    public Page<AdminReviewResponseDto> getAdminReviews(String keyword, Pageable pageable) {
        // 정렬을 위한 OrderSpecifier 목록 생성
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
        pageable.getSort().forEach(order -> {
            PathBuilder<?> entityPath = new PathBuilder<>(review.getType(), review.getMetadata());
            orderSpecifiers.add(new OrderSpecifier(
                    order.isAscending() ? Order.ASC : Order.DESC,
                    entityPath.get(order.getProperty())
            ));
        });

        // 쿼리 실행 및 페이징 적용
        List<Tuple> tuples = queryFactory
                .select(review.id, review.event.title, review.user.nickname, review.contents, review.rating, review.isDeleted)
                .from(review)
                .where(keywordContainsForReview(keyword)) // 검색어 조건 추가
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orderSpecifiers.toArray(new OrderSpecifier[0])) // 정렬 조건 적용
                .fetch();

        // Tuple 데이터를 DTO로 매핑
        List<AdminReviewResponseDto> results = tuples.stream()
                .map(tuple -> AdminReviewResponseDto.of(
                        tuple.get(review.id),
                        tuple.get(review.event.title),
                        tuple.get(review.user.nickname),
                        tuple.get(review.contents),
                        tuple.get(review.rating),
                        tuple.get(review.isDeleted)))
                .collect(Collectors.toList());

        long totalCount = queryFactory
                .select(Wildcard.count)
                .from(review)
                .where(keywordContainsForReview(keyword)) // 검색어 조건 추가
                .fetchOne();

        return new PageImpl<>(results, pageable, totalCount);
    }

    @Override
    public Page<AdminCouponResponseDto> getAdminCoupons(String keyword, Pageable pageable) {
        QCoupon coupon = QCoupon.coupon;

        // 정렬을 위한 OrderSpecifier 목록 생성
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
        pageable.getSort().forEach(order -> {
            PathBuilder<?> entityPath = new PathBuilder<>(coupon.getType(), coupon.getMetadata());
            orderSpecifiers.add(new OrderSpecifier(
                    order.isAscending() ? Order.ASC : Order.DESC,
                    entityPath.get(order.getProperty())
            ));
        });

        // 쿼리 실행 및 페이징 적용
        List<Tuple> tuples = queryFactory
                .select(
                        coupon.id,
                        coupon.discountAmount,
                        coupon.expiredAt,
                        coupon.code,
                        coupon.isUsed
                )
                .from(coupon)
                .where(keywordContainsForCoupon(keyword)) // 검색어 조건 추가
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orderSpecifiers.toArray(new OrderSpecifier[0])) // 정렬 조건 적용
                .fetch();

        // Tuple 데이터를 DTO로 매핑
        List<AdminCouponResponseDto> results = tuples.stream()
                .map(tuple -> AdminCouponResponseDto.of(
                        tuple.get(coupon.id),
                        tuple.get(coupon.discountAmount),
                        tuple.get(coupon.expiredAt),
                        tuple.get(coupon.code),
                        tuple.get(coupon.isUsed)
                ))
                .collect(Collectors.toList());

        long totalCount = queryFactory
                .select(Wildcard.count)
                .from(coupon)
                .where(keywordContainsForCoupon(keyword)) // 검색어 조건 추가
                .fetchOne();

        return new PageImpl<>(results, pageable, totalCount);
    }


    // 유저 검색용 키워드 조건 추가 메서드
    private BooleanExpression keywordContainsForUser(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return null;
        }
        return user.nickname.startsWith(keyword)
                .or(user.email.startsWith(keyword))
                .or(user.phoneNumber.startsWith(keyword))
                .or(user.role.stringValue().startsWith(keyword));
    }

    // 이벤트 검색용 키워드 조건 추가 메서드
    private BooleanExpression keywordContainsForEvent(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return null;
        }
        return event.title.startsWith(keyword)
                .or(event.content.startsWith(keyword))
                .or(event.location.startsWith(keyword));
    }

    // 리뷰 검색용 키워드 조건 추가 메서드
    private BooleanExpression keywordContainsForReview(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return null;
        }
        return review.contents.startsWith(keyword)
                .or(review.user.nickname.startsWith(keyword));
    }

    private BooleanExpression keywordContainsForCoupon(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return null;
        }
        return coupon.code.startsWith(keyword)
                .or(coupon.discountAmount.stringValue().startsWith(keyword));
    }

    @Override
    public Optional<User> findUserById(Long id) {
        User user = queryFactory
                .selectFrom(QUser.user)
                .where(QUser.user.id.eq(id))
                .fetchOne();
        return Optional.ofNullable(user);
    }

    @Override
    public Optional<Event> findEventById(Long id) {
        Event event = queryFactory
                .selectFrom(QEvent.event)
                .where(QEvent.event.id.eq(id))
                .fetchOne();
        return Optional.ofNullable(event);
    }

    @Override
    public Optional<Review> findReviewById(Long id) {
        Review review = queryFactory
                .selectFrom(QReview.review)
                .where(QReview.review.id.eq(id))
                .fetchOne();
        return Optional.ofNullable(review);
    }

    @Override
    public void saveCoupon(Coupon coupon) {
        // 쿠폰 저장 로직 구현 (JPA 사용)
    }

    @Override
    public Optional<Coupon> findCouponById(Long id) {
        Coupon coupon = queryFactory.selectFrom(QCoupon.coupon)
                .where(QCoupon.coupon.id.eq(id).and(QCoupon.coupon.isUsed.isFalse()))
                .fetchOne();
        return Optional.ofNullable(coupon);
    }

    @Override
    public List<Tuple> findCoupons(String keyword, Pageable pageable) {
        BooleanExpression keywordCondition = keyword != null && !keyword.isEmpty() ? QCoupon.coupon.code.containsIgnoreCase(keyword) : null;

        return queryFactory.select(
                        QCoupon.coupon.id,
                        QCoupon.coupon.discountAmount,
                        QCoupon.coupon.expiredAt,
                        QCoupon.coupon.code,
                        QCoupon.coupon.isUsed
                )
                .from(QCoupon.coupon)
                .where(keywordCondition, QCoupon.coupon.isUsed.isFalse())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public long countCoupons(String keyword) {
        BooleanExpression keywordCondition = keyword != null && !keyword.isEmpty() ? QCoupon.coupon.code.containsIgnoreCase(keyword) : null;

        return queryFactory.select(Wildcard.count)
                .from(QCoupon.coupon)
                .where(keywordCondition, QCoupon.coupon.isUsed.isFalse())
                .fetchOne();
    }


}

