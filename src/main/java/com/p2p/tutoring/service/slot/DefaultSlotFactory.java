package com.p2p.tutoring.service.slot;

import com.p2p.tutoring.model.Slot;
import com.p2p.tutoring.model.User;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class DefaultSlotFactory implements SlotFactory {

    @Override
    public Slot createTutorSlot(User tutor, LocalDate slotDate, LocalTime startTime, LocalTime endTime, String subject) {
        Slot slot = new Slot();
        slot.setTutor(tutor);
        slot.setSlotDate(slotDate);
        slot.setStartTime(startTime);
        slot.setEndTime(endTime);
        slot.setSubject(subject == null ? "" : subject.trim());
        slot.setBooked(false);
        return slot;
    }
}
