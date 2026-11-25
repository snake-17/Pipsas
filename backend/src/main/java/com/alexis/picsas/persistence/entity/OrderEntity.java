package com.alexis.picsas.persistence.entity;

import com.alexis.picsas.persistence.audit.AuditPizzaListener;
import com.alexis.picsas.persistence.audit.AuditableEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="pizza_order")
@EntityListeners({
    AuditingEntityListener.class})
@Getter
@Setter
@NoArgsConstructor
public class OrderEntity extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_order", nullable = false)
    private Integer idOrder;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;
    @Column(nullable = false,columnDefinition = "DECIMAL(6,2)")
    private BigDecimal total;
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "CHAR(1)")
    private Method method;
    @Column(name="additional_notes", length = 200)
    private String additionalNotes;
    @OneToMany(mappedBy = "order")
    private List<OrderItemEntity> items;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "id_customer",
            referencedColumnName = "id_customer",
            nullable = false)
    @JsonIgnore
    private CustomerEntity customer;
}
