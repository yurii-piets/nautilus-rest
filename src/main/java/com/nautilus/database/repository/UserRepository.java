package com.nautilus.database.repository;

import com.nautilus.database.domain.UserConfig;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserConfig, Long> {

    UserConfig findUserConfigByEmail(String email);

//    void update(UserConfig user);

//    @Query
//    Object findUserByEmail(String email);
//
//    @Query
//    String findPasswordByEmail(String email);
}
