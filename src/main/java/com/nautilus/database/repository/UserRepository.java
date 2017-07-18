package com.nautilus.database.repository;

import com.nautilus.database.domain.UserConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserConfig, Long> {

    /*@Query
    void update(UserConfig user);

    @Query
    Object findUserByEmail(String email);

    @Query
    String findPasswordByEmail(String email);*/
}
