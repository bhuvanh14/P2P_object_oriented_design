package com.p2p.tutoring.service.profile;

import com.p2p.tutoring.model.User;
import com.p2p.tutoring.service.common.ServiceResult;

public interface TutorProfileService {
    ServiceResult<Void> becomeTutor(User currentUser, String subjects, String contact);
}
