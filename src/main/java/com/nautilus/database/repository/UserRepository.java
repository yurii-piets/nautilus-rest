package com.nautilus.database.repository;

import com.nautilus.database.domain.UserConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;;

@Repository
public interface UserRepository extends JpaRepository<UserConfig, Long> {
    void update(UserConfig user);

    Object findUserByEmail(String email);

    String findPasswordByEmail(String email);
}
