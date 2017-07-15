package com.nautilus.database.def;

import com.nautilus.constants.Status;
import com.nautilus.dto.user.login.LoginDTO;
import com.nautilus.dto.user.register.RegisterUserDTO;

public interface UserService {

    Status registerUser(RegisterUserDTO user);
    Status updateUser(RegisterUserDTO user);
    Status checkCredential(LoginDTO loginDTO);
}
