package com.spotlightspace.core.payment.service;

import static com.spotlightspace.common.exception.ErrorCode.*;
import static com.spotlightspace.common.exception.ErrorCode.COUPON_ALREADY_USED;
import static com.spotlightspace.common.exception.ErrorCode.EVENT_TICKET_OUT_OF_STOCK;
import static com.spotlightspace.common.exception.ErrorCode.NOT_ENOUGH_POINT_AMOUNT;
import static com.spotlightspace.common.exception.ErrorCode.NOT_IN_EVENT_RECRUITMENT_PERIOD;
import static com.spotlightspace.common.exception.ErrorCode.POINT_AMOUNT_CANNOT_BE_NEGATIVE;

import com.spotlightspace.common.exception.ApplicationException;
import com.spotlightspace.core.event.domain.Event;
import com.spotlightspace.core.event.repository.EventRepository;
import com.spotlightspace.core.eventticketstock.domain.EventTicketStock;
import com.spotlightspace.core.eventticketstock.repository.EventTicketStockRepository;
import com.spotlightspace.core.payment.client.KakaopayApi;
import com.spotlightspace.core.payment.domain.Payment;
import com.spotlightspace.core.payment.dto.response.ApprovePaymentResponseDto;
import com.spotlightspace.core.payment.dto.response.CancelPaymentResponseDto;
import com.spotlightspace.core.payment.dto.response.ReadyPaymentResponseDto;
import com.spotlightspace.core.payment.repository.PaymentRepository;
import com.spotlightspace.core.point.domain.Point;
import com.spotlightspace.core.point.repository.PointRepository;
import com.spotlightspace.core.point.service.PointService;
import com.spotlightspace.core.pointhistory.service.PointHistoryService;
import com.spotlightspace.core.ticket.service.TicketService;
import com.spotlightspace.core.user.domain.User;
import com.spotlightspace.core.user.repository.UserRepository;
import com.spotlightspace.core.usercoupon.domain.UserCoupon;
import com.spotlightspace.core.usercoupon.repository.UserCouponRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService {

    private final KakaopayApi kakaopayApi;
    private final TicketService ticketService;
    private final PointService pointService;
    private final PointHistoryService pointHistoryService;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final UserCouponRepository userCouponRepository;
    private final PointRepository pointRepository;
    private final EventTicketStockRepository eventTicketStockRepository;

    @Value("${payment.kakao.cid}")
    private String cid;

    public ReadyPaymentResponseDto readyPayment(long userId, long eventId, Long couponId, Integer pointAmount) {
        User user = userRepository.findByIdOrElseThrow(userId);
        Event event = eventRepository.findByIdOrElseThrow(eventId);
        EventTicketStock eventTicketStock = eventTicketStockRepository.findByEventOrElseThrow(event);

        validateRecruitmentPeriod(event);
        validateEventTicketStock(eventTicketStock);

        Point point = null;
        UserCoupon userCoupon = null;
        int discountedPrice = event.getPrice();
        if (doesCouponIdExist(couponId)) {
            userCoupon = userCouponRepository.findByCouponIdAndUserIdOrElseThrow(couponId, user.getId());
            validateUserCoupon(userCoupon);
            discountedPrice -= userCoupon.getDiscountAmount();
        }
        if (doesPointAmountExist(pointAmount)) {
            point = pointRepository.findByUserOrElseThrow(user);
            validatePoint(point, pointAmount);
            point.deduct(pointAmount);
            discountedPrice -= pointAmount;
        }

        Payment payment = Payment.create(cid, event, user, event.getPrice(), discountedPrice, userCoupon, point);
        paymentRepository.save(payment);

        if (isPointUsed(point)) {
            pointHistoryService.createPointHistory(payment, point, pointAmount);
        }
        eventTicketStock.decreaseStock();

        ReadyPaymentResponseDto responseDto = kakaopayApi.readyPayment(
                cid,
                payment.getPartnerOrderId(),
                user.getId(),
                event.getTitle(),
                event.getId(),
                payment.getDiscountedAmount()
        );

        payment.setTid(responseDto.getTid());

        return responseDto;
    }

    public ApprovePaymentResponseDto approvePayment(String pgToken, String tid) {
        Payment payment = paymentRepository.findByTidOrElseThrow(tid);
        ApprovePaymentResponseDto responseDto = kakaopayApi.approvePayment(pgToken, payment);

        ticketService.createTicket(payment.getUser(), payment.getEvent(), payment.getOriginalAmount());
        payment.approve();

        return responseDto;
    }

    public CancelPaymentResponseDto cancelPayment(String tid, int cancelAmount, int cancelTaxFreeAmount) {
        Payment payment = paymentRepository.findByTidOrElseThrow(tid);
        Event event = payment.getEvent();
        if (event.isFinishedRecruitment(LocalDateTime.now())) {
            throw new ApplicationException(CANCELLATION_PERIOD_EXPIRED);
        }

        CancelPaymentResponseDto responseDto = kakaopayApi.cancelPayment(cid, tid, cancelAmount, cancelTaxFreeAmount);

        payment.cancel();
        if (payment.isPointUsed()) {
            pointService.cancelPointUsage(payment.getPoint());
        }

        EventTicketStock eventTicketStock = eventTicketStockRepository.findByEventOrElseThrow(payment.getEvent());
        eventTicketStock.increaseStock();

        return responseDto;
    }

    public void failPayment(String tid) {
        Payment payment = paymentRepository.findByTidOrElseThrow(tid);
        payment.fail();
    }

    private boolean doesCouponIdExist(Long couponId) {
        return couponId != null;
    }

    private boolean doesPointAmountExist(Integer pointAmount) {
        return pointAmount != null;
    }

    private void validateRecruitmentPeriod(Event event) {
        if (event.isNotRecruitmentPeriod()) {
            throw new ApplicationException(NOT_IN_EVENT_RECRUITMENT_PERIOD);
        }
    }

    private void validateEventTicketStock(EventTicketStock eventTicketStock) {
        if (eventTicketStock.isOutOfStock()) {
            throw new ApplicationException(EVENT_TICKET_OUT_OF_STOCK);
        }
    }

    private void validateUserCoupon(UserCoupon userCoupon) {
        if (userCoupon.isUsed()) {
            throw new ApplicationException(COUPON_ALREADY_USED);
        }
    }

    private void validatePoint(Point point, int pointAmount) {
        if (pointAmount < 0) {
            throw new ApplicationException(POINT_AMOUNT_CANNOT_BE_NEGATIVE);
        }
        if (point.cannotDeduct(pointAmount)) {
            throw new ApplicationException(NOT_ENOUGH_POINT_AMOUNT);
        }
    }

    private boolean isPointUsed(Point point) {
        return point != null;
    }
}
