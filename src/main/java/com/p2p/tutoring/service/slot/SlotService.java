package com.p2p.tutoring.service.slot;

import com.p2p.tutoring.model.Slot;
import com.p2p.tutoring.model.User;
import com.p2p.tutoring.service.common.ServiceResult;

import java.util.List;

public interface SlotService {
    ServiceResult<Void> addSlot(User tutor, String date, String startTime, String endTime, String subject);

    List<Slot> findAvailableFutureSlots();
}
