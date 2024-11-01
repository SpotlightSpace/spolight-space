package com.spotlightspace.core.event.domain;

import com.spotlightspace.common.entity.Timestamped;
import com.spotlightspace.core.event.dto.request.CreateEventRequestDto;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Document(indexName = "events")
public class EventElastic extends Timestamped {

    @Id
    @Field(type = FieldType.Long, name = "event_id")
    private Long id;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Text)
    private String content;

    @Field(type = FieldType.Text)
    private String location;

    // 시작 일시
    @Field(type = FieldType.Date, name = "start_at")
    private LocalDateTime startAt;

    // 종료 일시
    @Field(type = FieldType.Date, name = "end_at")
    private LocalDateTime endAt;

    @Field(type = FieldType.Integer, name = "max_people")
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

    private EventElastic(CreateEventRequestDto createEventRequestDto) {
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
    }

    public static EventElastic of(CreateEventRequestDto createEventRequestDto) {
        return new EventElastic(createEventRequestDto);
    }
}
