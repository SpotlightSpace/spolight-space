package com.spotlightspace.core.admin.dto.responsedto;


import com.spotlightspace.core.event.domain.Event;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class AdminEventResponseDto {
    private Long id;
    private String title;
    private String content;
    private String location;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private int maxPeople;
    private int price;
    private String category;
    private LocalDateTime recruitmentStartAt;
    private LocalDateTime recruitmentFinishAt;
    private Boolean isDeleted;

    // Event 엔티티에서 데이터를 가져오는 정적 팩토리 메서드
    public static AdminEventResponseDto from(Event event) {
        return new AdminEventResponseDto(
                event.getId(),
                event.getTitle(),
                event.getContent(),
                event.getLocation(),
                event.getStartAt(),
                event.getEndAt(),
                event.getMaxPeople(),
                event.getPrice(),
                event.getCategory().name(),
                event.getRecruitmentStartAt(),
                event.getRecruitmentFinishAt(),
                event.getIsDeleted()
        );
    }
}
