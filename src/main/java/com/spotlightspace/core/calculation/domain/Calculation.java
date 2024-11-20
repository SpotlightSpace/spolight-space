package com.spotlightspace.core.calculation.domain;

import com.spotlightspace.common.entity.Timestamped;
import com.spotlightspace.core.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "calculations")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Calculation extends Timestamped {

    @Id
    @GeneratedValue
    @Column(name = "calculation_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private long calculationAmount;

    private Calculation(User user, long calculationAmount) {
        this.user = user;
        this.calculationAmount = calculationAmount;
    }

    public static Calculation create(User user, long calculationAmount) {
        return new Calculation(user, calculationAmount);
    }
}
