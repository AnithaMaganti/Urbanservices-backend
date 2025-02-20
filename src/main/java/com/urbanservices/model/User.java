package com.urbanservices.model;


import com.urbanservices.enums.Role;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

//    @Column(unique = true, nullable = false)
//    private String mobileNo;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 20)
    private Role role; // Example: USER, SERVICE_PROVIDER, ADMIN
}
