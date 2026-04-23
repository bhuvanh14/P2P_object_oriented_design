package com.p2p.tutoring.service.dashboard;

import com.p2p.tutoring.model.Slot;
import com.p2p.tutoring.model.User;

import java.util.List;

public record DashboardView(
        User user,
        List<Slot> myTutorSlots,
        List<Slot> myLearnerSlots,
        String subjects,
        String requests
) {
}
