package com.alexis.picsas.persistence.repository;

import com.alexis.picsas.persistence.entity.OrderEntity;
import com.alexis.picsas.persistence.entity.OrderItemEntity;
import com.alexis.picsas.persistence.entity.OrderItemEntityId;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface OrderItemRepository extends CrudRepository<OrderItemEntity, OrderItemEntityId> {
    //busca si el id order existe y con el item id pizza.
    Optional<OrderItemEntity> findByIdOrderAndIdPizza(Integer orderId, Integer pizzaId);
    List<OrderItemEntity> findByIdOrder(Integer orderId);

}
