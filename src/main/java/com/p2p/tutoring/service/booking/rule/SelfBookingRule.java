package com.p2p.tutoring.service.booking.rule;

import com.p2p.tutoring.model.Slot;
import com.p2p.tutoring.model.User;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@Order(3)
public class SelfBookingRule implements BookingRule {

    @Override
    public Optional<String> validate(Slot slot, User learner, LocalDateTime now) {
        if (slot.getTutor().getId().equals(learner.getId())) {
            return Optional.of("Tutors cannot book their own slots.");
        }
        return Optional.empty();
    }
}
