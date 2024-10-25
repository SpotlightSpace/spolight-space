package com.spotlightspace.core.payment.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ApprovePaymentResponseDto {

    /**
     * 요청 고유 번호 - 승인/취소가 구분된 결제번호
     */
    @NotBlank(message = "요청 고유 번호는 필수 값입니다.")
    private String aid;

    /**
     * 결제 고유 번호 - 승인/취소가 동일한 결제번호
     */
    @NotBlank(message = "결제 고유 번호는 필수 값입니다.")
    private String tid;

    /**
     * 가맹점 코드
     */
    @NotBlank(message = "가맹점 코드는 필수 값입니다.")
    private String cid;

    /**
     * 정기 결제용 ID, 정기 결제 CID로 단건 결제 요청 시 발급
     */
    private String sid;

    /**
     * 가맹점 주문번호
     */
    @NotBlank(message = "가맹점 주문번호는 필수 값입니다.")
    private final String partnerOrderId;

    /**
     * 가맹점 회원 id
     */
    @NotBlank(message = "가맹점 회원 id는 필수 값입니다.")
    private final String partnerUserId;

    /**
     * 결제 수단, CARD 또는 MONEY 중 하나
     */
    @NotBlank(message = "결제 수단은 필수 값입니다.")
    private final String paymentMethodType;

    /**
     * 결제 금액 정보
     */
    @NotNull(message = "결제 금액 정보는 필수 값입니다.")
    private final Amount amount;

    /**
     * 결제 상세 정보, 결제 수단이 카드일 경우만 포함
     */
    private final CardInfo cardInfo;

    /**
     * 상품 이름
     */
    @NotBlank(message = "상품 이름은 필수 값입니다.")
    private final String itemName;

    /**
     * 상품 코드
     */
    private final String itemCode;

    /**
     * 상품 수량
     */
    @NotNull(message = "상품 수량은 필수 값입니다.")
    @Positive(message = "상품 수량은 양수입니다.")
    private final Integer quantity;

    /**
     * 결제 준비 요청 시각
     */
    @NotBlank(message = "결제 준비 요청 시각은 필수 값입니다.")
    private final String createdAt;

    /**
     * 결제 승인 시각
     */
    @NotBlank(message = "결제 승인 시각은 필수 값입니다.")
    private final String approvedAt;

    /**
     * 결제 승인 요청에 대해 저장한 값, 요청 시 전달된 내용
     */
    private final String payload;

    @Getter
    @RequiredArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Amount {

        /**
         * 전체 결제 금액
         */
        @NotNull(message = "총 결제 금액은 필수 값입니다.")
        @PositiveOrZero(message = "총 결제 금액은 0이상이여야 합니다.")
        private final Integer total;

        /**
         * 비과세 금액
         */
        @NotNull(message = "비과세 금액 필수 값입니다.")
        @Positive(message = "비과세 금액은 0이상이여야 합니다.")
        private final Integer taxFree;

        /**
         * 부가세 금액
         */
        @NotNull(message = "부가세 금액은 필수 값입니다.")
        @Positive(message = "부가게 금액은 0이상이여야 합니다.")
        private final Integer tax;

        /**
         * 사용한 포인트 금액
         */
        @NotNull(message = "사용한 포인트는 필수 값입니다.")
        @Positive(message = "사용한 포인트는 0이상이여야 합니다.")
        private final Integer point;

        /**
         * 할인 금액
         */
        @NotNull(message = "할인 금액은 필수 값입니다.")
        @Positive(message = "할인 금액은 0이상이여야 합니다.")
        private final Integer discount;

        /**
         * 컵 보증금
         */
        @NotNull(message = "컵 보증금은 필수 값입니다.")
        @Positive(message = "컵 보증금은 0이상이여야 합니다.")
        private final Integer greenDeposit;
    }

    @Getter
    @RequiredArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class CardInfo {

        /**
         * 카카오페이 매입사명
         */
        @NotBlank(message = "카카오페이 매입사명은 필수 값입니다.")
        private final String kakaopayPurchaseCorp; // 카카오페이 매입사명

        /**
         * 카카오페이 매입사 코드
         */
        @NotBlank(message = "카카오페이 매입사 코드는 필수 값입니다.")
        private final String kakaopayPurchaseCorpCode; // 카카오페이 매입사 코드

        /**
         * 카카오페이 발급사명
         */
        @NotBlank(message = "카카오페이 발급사명은 필수 값입니다.")
        private final String kakaopayIssuerCorp; // 카카오페이 발급사명

        /**
         * 카카오페이 발급사 코드
         */
        @NotBlank(message = "카카오페이 발급사 코드는 필수 값입니다.")
        private final String kakaopayIssuerCorpCode; // 카카오페이 발급사 코드

        /**
         * 카드 BIN
         */
        @NotBlank(message = "카드 BIN은 필수 값입니다.")
        private final String bin;

        /**
         * 카드 타입
         */
        @NotBlank(message = "카드 타입은 필수 값입니다.")
        private final String cardType;

        /**
         * 할부 개월 수
         */
        @NotBlank(message = "할부 개월 수는 필수 값입니다.")
        private final String installMonth;

        /**
         * 카드사 승인번호
         */
        @NotBlank(message = "카드사 승인번호는 필수 값입니다.")
        private final String approvedId;

        /**
         * 카드사 가맹점 번호
         */
        @NotBlank(message = "카드사 가맹점 번호는 필수 값입니다.")
        private final String cardMid;

        /**
         * 무이자할부 여부(Y/N)
         */
        @NotBlank(message = "무이자할부 여부는 필수 값입니다.")
        private final String interestFreeInstall;

        /**
         * 할부 유형(24.02.01일부터 제공) - CARD_INSTALLMENT: 업종 무이자 - SHARE_INSTALLMENT: 분담 무이자
         */
        @NotBlank(message = "할부 유형은 필수 값입니다.")
        private final String installmentType;

        /**
         * 카드 상품 코드
         */
        @NotBlank(message = "카드 상품 코드는 필수 값입니다.")
        private final String cardItemCode;
    }
}
