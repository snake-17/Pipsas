package com.alexis.picsas.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "order_item")
@IdClass(OrderItemEntityId.class)
@Getter
@Setter
@NoArgsConstructor
public class OrderItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_item", nullable = false)
    private Integer idItem;
    @Id
    @Column(name = "id_order",nullable = false)
    private Integer idOrder;
    @Column(name = "id_pizza", nullable = false)
    private Integer idPizza;
    @Column(nullable = false)
    private Integer quantity;
    @Column(nullable = false,columnDefinition = "Decimal(5,2)")
    private BigDecimal price;
    @ManyToOne
    @JoinColumn(name = "id_order",referencedColumnName = "id_order",insertable = false,updatable = false)
    @JsonIgnore
    private OrderEntity order;
    @ManyToOne
    @JoinColumn(name = "id_pizza", referencedColumnName = "id_pizza",insertable = false,updatable = false)
    private PizzaEntity pizza;
}
