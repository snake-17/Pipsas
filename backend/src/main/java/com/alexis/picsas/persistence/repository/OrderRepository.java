package com.alexis.picsas.persistence.repository;

import com.alexis.picsas.persistence.entity.CustomerEntity;
import com.alexis.picsas.persistence.entity.Method;
import com.alexis.picsas.persistence.entity.OrderEntity;
import com.alexis.picsas.persistence.entity.Status;
import com.alexis.picsas.persistence.projection.OrderSummary;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends ListCrudRepository<OrderEntity,Integer> {
    Optional<OrderEntity> findByCustomerAndStatus(CustomerEntity customer, Status status);
    //Busca por id y el status del carrito
    Optional<OrderEntity> findByIdOrderAndStatus(Integer orderId,Status status);
    //buscamos por orderId el status sea cart y el id customer
    Optional<OrderEntity> findByIdOrderAndStatusAndCustomerIdCustomer(Integer idOrder,Status status, String isCustomer);
    @Transactional
    @Modifying
    @Query("UPDATE OrderEntity o SET o.status = :newStatus "+
            "WHERE o.status = :currentStatus And o.modifiedDate <= :limitTime")
    int expiredOldCarts(
            @Param("currentStatus") Status currentStatus,
            @Param("newStatus") Status newStatus,
            @Param("limitTime") LocalDateTime limitTime);
    List<OrderEntity> findAllByCreatedDateAfter(LocalDateTime date);
    List<OrderEntity> findAllByMethodIn(List<Method>method);
    @Query(value = "SELECT * FROM pizza_order WHERE id_customer = :id", nativeQuery = true)
    List <OrderEntity> findCustomer(@Param("id") String idCustomer);
    @Query(value = "SELECT po.id_order AS idOrder, " +
            "cu.name AS customerName, " +
            "po.date AS orderDate," +
            "po.total AS orderTotal, " +
            "GROUP_CONCAT(pi.name) AS pizzaNames " +
            "FROM pizza_order po " +
            "INNER JOIN customer cu ON po.id_customer = cu.id_customer " +
            "INNER JOIN order_item oi ON po.id_order = oi.id_order " +
            "INNER JOIN pizza pi ON oi.id_pizza = pi.id_pizza " +
            "WHERE po.id_order = :orderId " +
            "GROUP BY po.id_order," +
            "cu.name," +
            "po.date," +
            "po.total",nativeQuery = true)
    OrderSummary findSummary(@Param("orderId")int orderId);
    @Procedure(value = "take_random_pizza_order",outputParameterName = "order_taken")
    boolean saveRandomOrder(@Param("idCustomer") String idCustomer,@Param("method") String method);
}
