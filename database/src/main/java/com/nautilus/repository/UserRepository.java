package com.nautilus.repository;

import com.nautilus.domain.UserNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface UserRepository extends Neo4jRepository<UserNode, Long> {

    UserNode findUserNodeByEmail(String email);

    UserNode findUserNodeByPhoneNumber(String phoneNumber);
}
