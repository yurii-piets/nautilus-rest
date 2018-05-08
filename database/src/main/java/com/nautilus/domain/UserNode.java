package com.nautilus.domain;

import com.nautilus.constants.Authorities;
import lombok.Data;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Set;

@Data
@NodeEntity
public class UserNode {

    private Long id;

    private String name;

    private String surname;

    private String phoneNumber;

    private String email;

    private String password;

    private Boolean enabled;

    private Authorities authorities;

    @Relationship(type = "OWNS")
    private Set<CarNode> cars;

}
