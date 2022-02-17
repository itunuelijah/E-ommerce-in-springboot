package com.phoenix.data.models;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;
    private String firstName;
    private String lastName;

    @Column (unique = true)
    private String email;

    @Column (length = 500)
    private String address;

    @OneToOne (cascade = CascadeType.PERSIST)
    private  final Cart myCart;
    public AppUser(){
        this.myCart = new Cart();
    }
}
