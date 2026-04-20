package com.p2p.tutoring.service.slot;

import com.p2p.tutoring.model.Slot;
import com.p2p.tutoring.model.User;
import com.p2p.tutoring.repository.SlotRepository;
import com.p2p.tutoring.service.common.ServiceResult;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;

@Service
public class SlotServiceImpl implements SlotService {

    private final SlotRepository slotRepository;
    private final SlotFactory slotFactory;

    public SlotServiceImpl(SlotRepository slotRepository, SlotFactory slotFactory) {
        this.slotRepository = slotRepository;
        this.slotFactory = slotFactory;
    }

    @Override
    public ServiceResult<Void> addSlot(User tutor, String date, String startTime, String endTime, String subject) {
        try {
            LocalDate slotDate = LocalDate.parse(date.trim());
            LocalTime start = LocalTime.parse(startTime.trim());
            LocalTime end = LocalTime.parse(endTime.trim());

            if (!end.isAfter(start)) {
                return ServiceResult.failure("End time must be after start time.");
            }

            LocalDateTime startDateTime = LocalDateTime.of(slotDate, start);
            if (!startDateTime.isAfter(LocalDateTime.now())) {
                return ServiceResult.failure("Slot must be scheduled in the future.");
            }

            List<Slot> sameDaySlots = slotRepository.findByTutorAndSlotDateOrderByStartTimeAsc(tutor, slotDate);
            boolean overlaps = sameDaySlots.stream()
                    .anyMatch(existing -> start.isBefore(existing.getEndTime()) && end.isAfter(existing.getStartTime()));

            if (overlaps) {
                return ServiceResult.failure("This slot overlaps an existing slot on the same date.");
            }

            Slot slot = Objects.requireNonNull(slotFactory.createTutorSlot(tutor, slotDate, start, end, subject));
            slotRepository.save(slot);
            return ServiceResult.success();
        } catch (DateTimeParseException | NullPointerException ex) {
            return ServiceResult.failure("Invalid date/time format. Use YYYY-MM-DD and HH:MM.");
        }
    }

    @Override
    public List<Slot> findAvailableFutureSlots() {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        return slotRepository
                .findByBookedFalseAndSlotDateGreaterThanEqualOrderBySlotDateAscStartTimeAsc(today)
                .stream()
                .filter(slot -> LocalDateTime.of(slot.getSlotDate(), slot.getStartTime()).isAfter(now))
                .toList();
    }
}
