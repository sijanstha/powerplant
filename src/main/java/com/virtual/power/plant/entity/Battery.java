package com.virtual.power.plant.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "tbl_battery", indexes = {
        @Index(name = "idx_post_code", columnList = "post_code")
})
@Getter
@Setter
@ToString
public class Battery {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "post_code", nullable = false)
    private String postcode;

    @Column(name = "capacity", nullable = false)
    private int capacity;
}
