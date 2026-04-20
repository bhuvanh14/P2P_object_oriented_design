package com.p2p.tutoring.service.auth;

import com.p2p.tutoring.model.User;
import com.p2p.tutoring.repository.UserRepository;
import com.p2p.tutoring.service.common.ServiceResult;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final UserRepository userRepository;

    public AuthServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public ServiceResult<Void> register(String name, String email, String password) {
        String normalizedName = name == null ? "" : name.trim();
        String normalizedEmail = email == null ? "" : email.trim().toLowerCase();
        String normalizedPassword = password == null ? "" : password.trim();

        if (normalizedName.isBlank() || normalizedEmail.isBlank() || normalizedPassword.isBlank()) {
            return ServiceResult.failure("Name, email, and password are required.");
        }

        if (!EMAIL_PATTERN.matcher(normalizedEmail).matches()) {
            return ServiceResult.failure("Invalid email format.");
        }

        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            return ServiceResult.failure("Email already exists.");
        }

        User user = new User();
        user.setName(normalizedName);
        user.setEmail(normalizedEmail);
        user.setPassword(normalizedPassword);

        userRepository.save(user);
        return ServiceResult.success();
    }

    @Override
    public ServiceResult<User> login(String email, String password) {
        String normalizedEmail = email == null ? "" : email.trim().toLowerCase();
        String normalizedPassword = password == null ? "" : password.trim();

        return userRepository.findByEmailIgnoreCase(normalizedEmail)
                .filter(user -> user.getPassword().equals(normalizedPassword))
                .map(ServiceResult::success)
                .orElseGet(() -> ServiceResult.failure("Invalid email or password."));
    }
}
