package com.macrobalance.cart.entity;

import com.macrobalance.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "carts")
public class Cart extends BaseEntity {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "guest_id")
    private String guestId;

    @Column(nullable = false)
    private String status = "ACTIVE";
}
