package com.p2p.tutoring.service.auth;

import com.p2p.tutoring.model.User;
import com.p2p.tutoring.service.common.ServiceResult;

public interface AuthService {
    ServiceResult<Void> register(String name, String email, String password);

    ServiceResult<User> login(String email, String password);
}
