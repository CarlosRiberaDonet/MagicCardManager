package com.magic.investor_api.user.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="user")
@Data // Genera getters, setters, toString, equals y hashCode automáticamente
@NoArgsConstructor // Constructor vacío necesario para Hibernate
@AllArgsConstructor // Constructor con todos los campos
public class User {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column (name = "password", nullable = false)
    private String password;

    @Column (name="role", nullable = false)
    private String role;
}