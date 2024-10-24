package com.spotlightspace.common.exception;

import static org.springframework.http.HttpStatus.*;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    USER_NOT_FOUND(NOT_FOUND, "존재하지 않는 유저입니다."),
    INVALID_PASSWORD_OR_EMAIL(NOT_FOUND, "이메일 또는 패스워드가 일치하지 않습니다"),
    EMAIL_DUPLICATED(CONFLICT, "이미 존재하는 이메일입니다."),
    FORBIDDEN_USER(FORBIDDEN, "권한이 없습니다."),

    ADMIN_NOT_FOUND(NOT_FOUND, "존재하지 않는 관리자입니다."),

    EVENT_NOT_FOUND(NOT_FOUND, "존재하지 않는 이벤트입니다."),

    ATTACHMENT_NOT_FOUND(NOT_FOUND, "존재하지 않는 첨부파일입니다."),

    TICKET_NOT_FOUND(NOT_FOUND, "존재하지 않는 티켓입니다."),

    POINT_NOT_FOUND(NOT_FOUND, "존재하지 않는 포인트입니다."),

    COUPON_NOT_FOUND(NOT_FOUND, "존재하지 않는 쿠폰입니다."),

    REVIEW_NOT_FOUND(NOT_FOUND, "존재하지 않는 리뷰입니다."),

    WRONG_CATEGORY_NAME(BAD_REQUEST, "유효하지 않는 카테고리 입니다."),

    USER_NOT_ARTIST(FORBIDDEN,"일반 유저는 해당 작업을 수행할 수 없습니다."),

    TABLE_NOT_FOUND(NOT_FOUND, "테이블이 존재하지 않습니다."),

    USER_NOT_ACCESS_EVENT(FORBIDDEN, "해당 사용자가 등록한 이벤트가 아닙니다.");


    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
