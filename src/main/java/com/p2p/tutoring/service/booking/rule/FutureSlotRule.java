package com.p2p.tutoring.service.booking.rule;

import com.p2p.tutoring.model.Slot;
import com.p2p.tutoring.model.User;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@Order(2)
public class FutureSlotRule implements BookingRule {

    @Override
    public Optional<String> validate(Slot slot, User learner, LocalDateTime now) {
        LocalDateTime slotStart = LocalDateTime.of(slot.getSlotDate(), slot.getStartTime());
        if (!slotStart.isAfter(now)) {
            return Optional.of("Only future slots can be booked.");
        }
        return Optional.empty();
    }
}
