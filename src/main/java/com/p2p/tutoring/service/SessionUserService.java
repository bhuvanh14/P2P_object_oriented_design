package com.p2p.tutoring.service;

import com.p2p.tutoring.model.User;
import com.p2p.tutoring.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SessionUserService {

    private static final String SESSION_USER_ID = "userId";
    private static final String SESSION_USER_ROLE = "userRole";

    private final UserRepository userRepository;

    public SessionUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void login(HttpSession session, User user) {
        session.setAttribute(SESSION_USER_ID, user.getId());
        session.setAttribute(SESSION_USER_ROLE, user.getRole());
    }

    public void logout(HttpSession session) {
        session.invalidate();
    }

    public void updateRole(HttpSession session, String role) {
        session.setAttribute(SESSION_USER_ROLE, role);
    }

    public Optional<User> getCurrentUser(HttpSession session) {
        Object rawId = session.getAttribute(SESSION_USER_ID);
        if (!(rawId instanceof Long userId)) {
            return Optional.empty();
        }
        Optional<User> user = userRepository.findById(userId);
        user.ifPresent(u -> {
            Object rawRole = session.getAttribute(SESSION_USER_ROLE);
            String role = u.getRole();
            if (role != null && (rawRole == null || !role.equalsIgnoreCase(rawRole.toString()))) {
                session.setAttribute(SESSION_USER_ROLE, role);
            }
        });
        return user;
    }
}
