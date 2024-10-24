package com.spotlightspace.core.event.service;

import com.spotlightspace.common.annotation.AuthUser;
import com.spotlightspace.common.entity.TableRole;
import com.spotlightspace.common.exception.ApplicationException;
import com.spotlightspace.core.attachment.service.AttachmentService;
import com.spotlightspace.core.event.domain.Event;
import com.spotlightspace.core.event.dto.AddEventRequestDto;
import com.spotlightspace.core.event.dto.AddEventResponseDto;
import com.spotlightspace.core.event.dto.UpdateEventRequestDto;
import com.spotlightspace.core.event.dto.UpdateEventResponseDto;
import com.spotlightspace.core.event.repository.EventRepository;
import com.spotlightspace.core.user.domain.User;
import com.spotlightspace.core.user.domain.UserRole;
import com.spotlightspace.core.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static com.spotlightspace.common.exception.ErrorCode.*;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final AttachmentService attachmentService;

    @Transactional
    public AddEventResponseDto addEvent(AddEventRequestDto requestDto, AuthUser authUser, List<MultipartFile> files) throws IOException {
        // 유저 확인
        User user = checkUserExist(authUser.getUserId());
        // 유저 권한 확인
        validateUserRole(user.getRole());
        // 프로필 이미지가 있다면 저장 로직
        Event event = eventRepository.save(Event.of(requestDto, user));
        if (!files.isEmpty()) {
            attachmentService.addAttachmentList(files, event.getId(), TableRole.EVENT);
        }
        return AddEventResponseDto.from(event);
    }

    @Transactional
    public UpdateEventResponseDto updateEvent(UpdateEventRequestDto requestDto, AuthUser authUser, Long id) {
        // 이벤트 존재 유무 검사
        Event event = checkEventExist(id);
        // 이벤트를 작성한 아티스트인가 검사
        checkEventAndUser(event, authUser);

        // 수정 로직
        if (requestDto.getTitle() != null) {
            event.changeTitle(requestDto.getTitle());
        }
        if (requestDto.getContent() != null) {
            event.changeContent(requestDto.getContent());
        }
        if (requestDto.getStartAt() != null) {
            event.changeStartAt(requestDto.getStartAt());
        }
        if (requestDto.getEndAt() != null) {
            event.changeEndAt(requestDto.getEndAt());
        }
        if (requestDto.getMaxPeople() != null) {
            // 변경하려는 maxPeople값이 이미 결제한 사람 언더일 때 exception 처리 해야함
            event.changeMaxPeople(requestDto.getMaxPeople());
        }
        if (requestDto.getPrice() != null) {
            event.changePrice(requestDto.getPrice());
        }
        if (requestDto.getCategory() != null) {
            event.changeCategory(requestDto.getCategory());
        }
        if (requestDto.getRecruitmentStartAt() != null) {
            event.changeRecruitmentStartAt(requestDto.getRecruitmentStartAt());
        }
        if (requestDto.getRecruitmentFinishAt() != null) {
            event.changeRecruitmentFinishAt(requestDto.getRecruitmentFinishAt());
        }
        eventRepository.save(event);
        return UpdateEventResponseDto.from(event);
    }

    @Transactional
    public void deleteEvent(Long id, AuthUser authUser) {
        // 이벤트 존재 유무 검사
        Event event = checkEventExist(id);
        // 이벤트를 작성한 아티스트인가 검사
        checkEventAndUser(event, authUser);
        // 삭제 진행 시 결제한 사람 (포인트, 쿠폰)환불처리 + 관련 이미지 삭제
        attachmentService.deleteAttachmentWithOtherTable(event.getId(), TableRole.EVENT);
        event.deleteEvent();
    }

    // 유저 존재 확인
    private User checkUserExist(Long id) {
        return userRepository.findByIdOrElseThrow(id);
    }

    // 유저 권한 확인
    private void validateUserRole(UserRole role) {
        if (role != UserRole.ROLE_ARTIST) {
            throw new ApplicationException(USER_NOT_ARTIST);
        }
    }

    // 이벤트 존재 확인
    private Event checkEventExist(Long id) {
        return eventRepository.findByIdOrElseThrow(id);
    }

    // 이벤트를 작성한 사람인가 확인
    private void checkEventAndUser(Event event, AuthUser authUser) {
        if(!event.getUser().getId().equals(authUser.getUserId())) {
            throw new ApplicationException(USER_NOT_ACCESS_EVENT);
        }
    }
}
