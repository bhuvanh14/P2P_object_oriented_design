package com.p2p.tutoring.service.booking.rule;

import com.p2p.tutoring.model.Slot;
import com.p2p.tutoring.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

public interface BookingRule {
    Optional<String> validate(Slot slot, User learner, LocalDateTime now);
}
