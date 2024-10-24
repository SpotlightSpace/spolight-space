package com.spotlightspace.core.event.dto;

import com.spotlightspace.core.event.domain.Event;
import com.spotlightspace.core.event.domain.EventCategory;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UpdateEventResponseDto {
    private Long id;
    private String title;
    private String content;
    private String location;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private int maxPeople;
    private int price;
    private EventCategory category;
    private LocalDateTime recruitmentStartAt;
    private LocalDateTime recruitmentFinishAt;
    private UpdateEventResponseDto(Event event) {
        this.id = event.getId();
        this.title = event.getTitle();
        this.content = event.getContent();
        this.location = event.getLocation();
        this.startAt = event.getStartAt();
        this.endAt = event.getEndAt();
        this.maxPeople = event.getMaxPeople();
        this.price = event.getPrice();
        this.category = event.getCategory();
        this.recruitmentStartAt = event.getRecruitmentStartAt();
        this.recruitmentFinishAt = event.getRecruitmentFinishAt();
    }
    public static UpdateEventResponseDto from(Event event) {
        return new UpdateEventResponseDto(event);
    }
}
