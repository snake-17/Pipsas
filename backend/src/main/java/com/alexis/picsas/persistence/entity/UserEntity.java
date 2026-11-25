package com.alexis.picsas.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
public class UserEntity {

    @Id // Esta es la única llave primaria
    @Column(nullable = false, length = 20)
    private String username; // Funciona como ID y es String a la vez

    @Column(nullable = false, length = 200)
    private String password;

    @Column(length = 50)
    private String email;

    @Column(nullable = false, columnDefinition = "TINYINT")
    private Boolean locked;

    @Column(nullable = false, columnDefinition = "TINYINT")
    private Boolean disabled; // Cambié esto a Boolean porque es TINYINT en la BD
    @OneToMany(mappedBy = "user")
    private List<UserRoleEntity> roles;
}