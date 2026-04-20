package com.p2p.tutoring.service.slot;

import com.p2p.tutoring.model.Slot;
import com.p2p.tutoring.model.User;

import java.time.LocalDate;
import java.time.LocalTime;

public interface SlotFactory {
    Slot createTutorSlot(User tutor, LocalDate slotDate, LocalTime startTime, LocalTime endTime, String subject);
}
