package com.nautilus.repository;

import com.nautilus.domain.UserConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserConfig, Long> {
    UserConfig findUserConfigByEmail(String email);
    UserConfig findUserConfigByPhoneNumber(String phoneNumber);
}
