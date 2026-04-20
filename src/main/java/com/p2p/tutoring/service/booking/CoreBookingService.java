package com.p2p.tutoring.service.booking;

import com.p2p.tutoring.model.Slot;
import com.p2p.tutoring.model.User;
import com.p2p.tutoring.repository.SlotRepository;
import com.p2p.tutoring.service.booking.rule.BookingRule;
import com.p2p.tutoring.service.common.ServiceResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service("coreBookingService")
public class CoreBookingService implements BookingService {

    private final SlotRepository slotRepository;
    private final List<BookingRule> bookingRules;

    public CoreBookingService(SlotRepository slotRepository, List<BookingRule> bookingRules) {
        this.slotRepository = slotRepository;
        this.bookingRules = bookingRules;
    }

    @Override
    public ServiceResult<Slot> getBookableSlot(Long slotId, User learner) {
        Long requiredSlotId = Objects.requireNonNull(slotId);
        Optional<Slot> maybeSlot = slotRepository.findById(requiredSlotId);
        if (maybeSlot.isEmpty()) {
            return ServiceResult.failure("Slot not found.");
        }

        Slot slot = maybeSlot.get();
        ServiceResult<Void> validation = validate(slot, learner);
        if (!validation.isSuccess()) {
            return ServiceResult.failure(validation.getMessage());
        }

        return ServiceResult.success(slot);
    }

    @Override
    public ServiceResult<Void> bookSlot(Long slotId, User learner) {
        Long requiredSlotId = Objects.requireNonNull(slotId);
        Optional<Slot> maybeSlot = slotRepository.findById(requiredSlotId);
        if (maybeSlot.isEmpty()) {
            return ServiceResult.failure("Slot not found.");
        }

        Slot slot = maybeSlot.get();
        ServiceResult<Void> validation = validate(slot, learner);
        if (!validation.isSuccess()) {
            return validation;
        }

        slot.setBooked(true);
        slot.setLearner(learner);
        slotRepository.save(slot);
        return ServiceResult.success();
    }

    private ServiceResult<Void> validate(Slot slot, User learner) {
        LocalDateTime now = LocalDateTime.now();
        for (BookingRule rule : bookingRules) {
            Optional<String> message = rule.validate(slot, learner, now);
            if (message.isPresent()) {
                return ServiceResult.failure(message.get());
            }
        }
        return ServiceResult.success();
    }
}
