package com.spotlightspace.core.point.service;

import com.spotlightspace.common.annotation.AuthUser;
import com.spotlightspace.common.exception.ApplicationException;
import com.spotlightspace.common.exception.ErrorCode;
import com.spotlightspace.core.data.UserTestData;
import com.spotlightspace.core.point.domain.Point;
import com.spotlightspace.core.point.dto.CreatePointRequestDto;
import com.spotlightspace.core.point.dto.CreatePointResponseDto;
import com.spotlightspace.core.point.dto.GetPointResponseDto;
import com.spotlightspace.core.point.repository.PointRepository;
import com.spotlightspace.core.user.domain.User;
import com.spotlightspace.core.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static com.spotlightspace.common.exception.ErrorCode.USER_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class PointServiceTest {

    @Mock
    private PointRepository pointRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private PointService pointService;

    @Test
    @DisplayName("포인트가 없을 때 새로운 데이터를 가지고 생성한다.")
    public void createNewPoint() {
        // given
        AuthUser authUser = UserTestData.testAuthUser();
        User user = UserTestData.testUser();
        CreatePointRequestDto requestDto = new CreatePointRequestDto(10000);
        int amount = (int) (requestDto.getPrice() * 0.005);

        Point point = Point.of(amount, user);

        given(userRepository.findByIdOrElseThrow(authUser.getUserId())).willReturn(user);
        given(pointRepository.findByUser(user)).willReturn(Optional.empty());
        given(pointRepository.save(any(Point.class))).willReturn(point);

        // when
        CreatePointResponseDto responseDto = pointService.createPoint(requestDto, authUser);

        // then
        assertNotNull(responseDto);
        assertEquals(responseDto.getAmount(), amount);
    }

    @Test
    @DisplayName("포인트가 있을 때 기존 데이터에 포인트를 추가한다.")
    public void addPointsWhenPointExists() {
        // given
        AuthUser authUser = UserTestData.testAuthUser();
        User user = UserTestData.testUser();

        int initPoint = 100;
        int addedPoint = 50; // 추가할 포인트
        Point existingPoint = Point.of(initPoint, user);

        existingPoint.addPoint(addedPoint);

        CreatePointRequestDto requestDto = new CreatePointRequestDto(addedPoint);

        given(userRepository.findByIdOrElseThrow(authUser.getUserId())).willReturn(user);
        given(pointRepository.findByUser(user)).willReturn(Optional.of(existingPoint));
        given(pointRepository.save(existingPoint)).willReturn(existingPoint);

        // when
        CreatePointResponseDto responseDto = pointService.createPoint(requestDto, authUser);

        // then
        assertNotNull(responseDto);
        assertEquals(responseDto.getAmount(), initPoint + addedPoint);
    }

    @Test
    @DisplayName("유저의 포인트 정보를 조회한다.")
    public void getPointReturnsUserPoints() {
        // given
        AuthUser authUser = UserTestData.testAuthUser();
        User user = UserTestData.testUser();
        ReflectionTestUtils.setField(user, "id", authUser.getUserId());

        int existPoint = 100;
        Point existingPoint = Point.of(existPoint, user);

        given(userRepository.findByIdOrElseThrow(authUser.getUserId())).willReturn(user);
        given(pointRepository.findByUserOrElseThrow(user)).willReturn(existingPoint);

        // when
        GetPointResponseDto responseDto = pointService.getPoint(authUser);

        // then
        assertNotNull(responseDto);
        assertEquals(existPoint, responseDto.getAmount());
    }

    @Test
    @DisplayName("유저가 존재하지 않을 경우 예외를 발생시킨다.")
    public void getPointThrowsExceptionWhenUserNotFound() {
        // given
        AuthUser authUser = UserTestData.testAuthUser();

        given(userRepository.findByIdOrElseThrow(authUser.getUserId()))
                .willThrow(new ApplicationException(USER_NOT_FOUND));

        // when
        ApplicationException exception = assertThrows(ApplicationException.class, () -> pointService.getPoint(authUser));

        // then
        assertEquals("존재하지 않는 유저입니다.", exception.getMessage());
    }
}
