package com.alexis.picsas.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "customer")
@Getter
@Setter
@NoArgsConstructor
public class CustomerEntity {
    @Id
    @Column(name = "id_customer", nullable = false, length = 36)
    private String idCustomer;
    @Column(length = 60,nullable = false)
    private String name;
    @Column(length = 100)
    private String address;
    @Column(name = "phone_number",length = 20)
    private String phoneNumber;
    @OneToOne
    @JoinColumn(name = "user_username",referencedColumnName = "username")
    @JsonIgnore
    private UserEntity user;
    @OneToMany(mappedBy = "customer")
    private List<OrderEntity> orders;
}
