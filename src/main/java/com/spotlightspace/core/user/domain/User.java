package com.spotlightspace.core.user.domain;

import com.spotlightspace.core.auth.dto.SignupUserRequestDto;
import com.spotlightspace.core.user.dto.request.UpdateUserRequestDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Entity
@Table(name = "users")
@Getter
@Service
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @NotNull
    private String email;

    @NotNull
    private String nickname;

    @NotNull
    private LocalDate birth;

    @Column(unique = true)
    @NotNull
    private String phoneNumber;

    @Column
    @NotNull
    private String password;

    @NotNull
    @Enumerated(EnumType.STRING)
    private UserRole role;

    private boolean isDeleted = false;

    private User(String email, String nickname, String password, UserRole role, LocalDate birth, String phoneNumber) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.role = role;
        this.birth = birth;
        this.phoneNumber = phoneNumber;
    }

    public static User of(String encryptPassword, SignupUserRequestDto signupUserRequestDto) {
        return new User(
                signupUserRequestDto.getEmail(),
                signupUserRequestDto.getNickname(),
                encryptPassword,
                UserRole.from(signupUserRequestDto.getRole()),
                LocalDate.parse(signupUserRequestDto.getBirth(), DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                signupUserRequestDto.getPhoneNumber()
        );
    }

    public void update(String encryptPassword, UpdateUserRequestDto updateUserRequestDto) {
        this.nickname = updateUserRequestDto.getNickname();
        this.birth = LocalDate.parse(updateUserRequestDto.getBirth(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.phoneNumber = updateUserRequestDto.getPhoneNumber();
        this.password = encryptPassword;}
}
