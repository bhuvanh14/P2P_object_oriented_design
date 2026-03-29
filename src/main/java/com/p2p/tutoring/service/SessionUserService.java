package com.p2p.tutoring.service;

import com.p2p.tutoring.model.User;
import com.p2p.tutoring.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SessionUserService {

    private static final String SESSION_USER_ID = "userId";

    private final UserRepository userRepository;

    public SessionUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void login(HttpSession session, User user) {
        session.setAttribute(SESSION_USER_ID, user.getId());
    }

    public void logout(HttpSession session) {
        session.invalidate();
    }

    public Optional<User> getCurrentUser(HttpSession session) {
        Object rawId = session.getAttribute(SESSION_USER_ID);
        if (!(rawId instanceof Long userId)) {
            return Optional.empty();
        }
        return userRepository.findById(userId);
    }
}
