package com.p2p.tutoring.service.dashboard;

import com.p2p.tutoring.model.Slot;
import com.p2p.tutoring.model.User;
import com.p2p.tutoring.repository.SlotRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final SlotRepository slotRepository;

    public DashboardServiceImpl(SlotRepository slotRepository) {
        this.slotRepository = slotRepository;
    }

    @Override
    public DashboardView buildDashboard(User currentUser) {
        List<Slot> myTutorSlots = slotRepository.findByTutorOrderBySlotDateDescStartTimeDesc(currentUser);
        List<Slot> myLearnerSlots = slotRepository.findByLearnerOrderBySlotDateDescStartTimeDesc(currentUser);

        String subjects = currentUser.getSubjects() == null ? "Not set" : currentUser.getSubjects();
        String requests = "No request workflow is wired yet.";

        return new DashboardView(currentUser, myTutorSlots, myLearnerSlots, subjects, requests);
    }
}
