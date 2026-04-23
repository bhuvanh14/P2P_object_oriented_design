package com.p2p.tutoring.service.booking;

import com.p2p.tutoring.model.Slot;
import com.p2p.tutoring.model.User;
import com.p2p.tutoring.service.common.ServiceResult;

public interface BookingService {
    ServiceResult<Slot> getBookableSlot(Long slotId, User learner);

    ServiceResult<Void> bookSlot(Long slotId, User learner);
}
