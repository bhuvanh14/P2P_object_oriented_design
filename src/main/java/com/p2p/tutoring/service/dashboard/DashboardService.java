package com.p2p.tutoring.service.dashboard;

import com.p2p.tutoring.model.User;

public interface DashboardService {
    DashboardView buildDashboard(User currentUser);
}
