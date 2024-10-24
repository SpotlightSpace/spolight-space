package com.spotlightspace.core.review.dto;

import com.spotlightspace.core.user.domain.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
public class ReviewRequestDto {

    @NotNull
    private Long eventId;

    private User nickname;

    @NotNull
    @Range(min = 1, max = 5)
    private Integer rating;     // 별점 (1~5점)

    @Size(min = 10 , max = 200) //최소 10글자 최대 200글자
    @NotBlank
    private String contents; // 리뷰 내용
}
