package com.example.springpipeline.entities;

import jakarta.persistence.*;
import lombok.Data;

//use lombok.Data;
//Assign to table Customers

@Data
@Entity
@Table(name = "customers")
public class Customer {
    @Id
    //SERIAL PRIMARY KEY
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;
    @Column(nullable = false)
    private  String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column(nullable = false)
    private String email;
    private java.sql.Timestamp createdAt;
}
