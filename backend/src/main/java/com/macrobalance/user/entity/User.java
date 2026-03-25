package com.macrobalance.user.entity;

import com.macrobalance.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User extends BaseEntity {

    @Column(nullable = false, length = 150)
    private String name;

    @Column(unique = true, nullable = false, length = 255)
    private String email;

    @Column(unique = true, length = 20)
    private String phone;

    @Column(nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @Column(name = "is_email_verified")
    private boolean isEmailVerified = false;

    @Column(name = "is_phone_verified")
    private boolean isPhoneVerified = false;

}
