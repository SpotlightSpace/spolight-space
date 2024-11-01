package com.spotlightspace.core.event.domain;


import com.spotlightspace.core.event.dto.request.CreateEventRequestDto;
import com.spotlightspace.core.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Document(indexName = "events")
@Getter
public class EventElastic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Field(name = "event_id", type = FieldType.Long)
    private Long id;
    @Field(type = FieldType.Text)
    private String title;
    @Field(type = FieldType.Text)
    private String content;
    @Field(type = FieldType.Text)
    private String location;
    @Field(name = "start_at", type = FieldType.Date)
    private LocalDateTime startAt;
    @Field(name = "end_at", type = FieldType.Date)
    private LocalDateTime endAt;
    @Field(name = "max_people", type = FieldType.Integer)
    private int maxPeople;
    @Field(type = FieldType.Integer)
    private int price;
    @Field(type = FieldType.Text)
    private EventCategory category;
    @Field(type = FieldType.Date)
    private LocalDateTime recruitmentStartAt;
    @Field(type = FieldType.Date)
    private LocalDateTime recruitmentFinishAt;
    @Field(type = FieldType.Boolean)
    private boolean isDeleted = false;
    @Field(type = FieldType.Boolean)
    private boolean isCalculated = false;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private EventElastic(CreateEventRequestDto createEventRequestDto, User user) {
        this.title = createEventRequestDto.getTitle();
        this.content = createEventRequestDto.getContent();
        this.location = createEventRequestDto.getLocation();
        this.startAt = createEventRequestDto.getStartAt();
        this.endAt = createEventRequestDto.getEndAt();
        this.maxPeople = createEventRequestDto.getMaxPeople();
        this.price = createEventRequestDto.getPrice();
        this.category = createEventRequestDto.getCategory();
        this.recruitmentStartAt = createEventRequestDto.getRecruitmentStartAt();
        this.recruitmentFinishAt = createEventRequestDto.getRecruitmentFinishAt();
        this.user = user;
    }

    public static EventElastic of(CreateEventRequestDto createEventRequestDto, User user) {
        return new EventElastic(createEventRequestDto, user);
    }
}
