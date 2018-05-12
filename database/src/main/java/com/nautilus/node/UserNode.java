package com.nautilus.node;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nautilus.algorithm.MD5;
import com.nautilus.dto.constants.Authorities;
import com.nautilus.dto.user.RegisterUserDto;
import com.nautilus.dto.user.UserInfoDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@NodeEntity
@ToString(exclude = "cars")
@EqualsAndHashCode(exclude = "cars")
public class UserNode implements UserDetails {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String surname;

    private String phoneNumber;

    private String email;

    private String password;

    @JsonIgnore
    private boolean enabled;

    @JsonIgnore
    private boolean locked;

    @JsonIgnore
    private boolean expired;

    @JsonIgnore
    private boolean credentialsExpired;

    @JsonIgnore
    private List<GrantedAuthority> authorities;

    @Relationship(type = "OWNS")
    @JsonIgnore
    private Set<CarNode> cars;

    public UserNode(RegisterUserDto registerUserDto){
        this.name = registerUserDto.getName();
        this.surname = registerUserDto.getSurname();
        this.phoneNumber = registerUserDto.getPhoneNumber();
        this.email = registerUserDto.getEmail();
        this.password = new MD5().encode(registerUserDto.getPassword());
        this.authorities = List.of(Authorities.USER);
        this.enabled = true;
        this.locked = false;
        this.expired = false;
        this.credentialsExpired = false;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !expired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !credentialsExpired;
    }

    public UserInfoDto toUserInfoDto(){
        return UserInfoDto.builder()
                .email(email)
                .phoneNumber(phoneNumber)
                .name(name)
                .surname(surname)
                .build();
    }
}
