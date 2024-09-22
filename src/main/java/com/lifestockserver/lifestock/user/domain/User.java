package com.lifestockserver.lifestock.user.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import com.lifestockserver.lifestock.common.domain.Base;

@Entity
@Table(name = "user")
@Getter
@Setter
@ToString(exclude = "password")
public class User extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String realName;

    @Column(nullable = true)
    private String displayName;  // Service에서 랜덤 생성하는 코드 필요

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;

}