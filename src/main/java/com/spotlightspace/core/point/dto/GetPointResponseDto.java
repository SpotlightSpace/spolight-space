package com.spotlightspace.core.point.dto;

import com.spotlightspace.core.point.domain.Point;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetPointResponseDto {

    private Long id;
    private int amount;
    private String nickname;
    private GetPointResponseDto(Point point) {
        this.id = point.getId();
        this.amount = point.getAmount();
        this.nickname = point.getUser().getNickname();
    }
    public static GetPointResponseDto from(Point point) {
        return new GetPointResponseDto(point);
    }
}
