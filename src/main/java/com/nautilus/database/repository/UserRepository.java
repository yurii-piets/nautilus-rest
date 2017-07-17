package com.nautilus.database.repository;

import com.nautilus.database.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;;import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
