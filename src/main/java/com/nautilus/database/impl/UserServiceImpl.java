package com.nautilus.database.impl;

import com.nautilus.constants.Status;
import com.nautilus.database.def.UserService;
import com.nautilus.dto.user.login.LoginDTO;
import com.nautilus.dto.user.register.RegisterUserDTO;
import org.springframework.stereotype.Component;

@Component
public class UserServiceImpl implements UserService {
    @Override
    public Status registerUser(RegisterUserDTO user) {
        return Status.ACCEPTED;
    }

    @Override
    public Status updateUser(RegisterUserDTO user) {
        return Status.UPDATED;
    }

    @Override
    public Status checkCredential(LoginDTO loginDTO) {
        return Status.ACCEPTED;
    }
}
