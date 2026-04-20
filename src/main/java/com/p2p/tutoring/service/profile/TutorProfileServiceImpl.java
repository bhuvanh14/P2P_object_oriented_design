package com.p2p.tutoring.service.profile;

import com.p2p.tutoring.model.User;
import com.p2p.tutoring.repository.UserRepository;
import com.p2p.tutoring.service.common.ServiceResult;
import org.springframework.stereotype.Service;

@Service
public class TutorProfileServiceImpl implements TutorProfileService {

    private final UserRepository userRepository;

    public TutorProfileServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public ServiceResult<Void> becomeTutor(User currentUser, String subjects, String contact) {
        String normalizedSubjects = subjects == null ? "" : subjects.trim();
        String normalizedContact = contact == null ? "" : contact.trim();

        if (normalizedSubjects.isBlank() || normalizedContact.isBlank()) {
            return ServiceResult.failure("Subjects and contact details are required.");
        }

        currentUser.setSubjects(normalizedSubjects);
        currentUser.setContact(normalizedContact);
        currentUser.setRole("tutor");
        userRepository.save(currentUser);

        return ServiceResult.success();
    }
}
