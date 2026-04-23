package com.p2p.tutoring.service.booking.rule;

import com.p2p.tutoring.model.Slot;
import com.p2p.tutoring.model.User;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@Order(1)
public class AlreadyBookedRule implements BookingRule {

    @Override
    public Optional<String> validate(Slot slot, User learner, LocalDateTime now) {
        if (slot.isBooked()) {
            return Optional.of("This slot is already booked.");
        }
        return Optional.empty();
    }
}
