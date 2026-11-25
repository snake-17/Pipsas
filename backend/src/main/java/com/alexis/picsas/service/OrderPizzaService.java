package com.alexis.picsas.service;

import com.alexis.picsas.persistence.entity.*;
import com.alexis.picsas.persistence.mapper.OrderItemMapper;
import com.alexis.picsas.persistence.mapper.OrderMapper;

import com.alexis.picsas.persistence.repository.OrderItemRepository;
import com.alexis.picsas.persistence.repository.OrderRepository;
import com.alexis.picsas.persistence.repository.PizzaRepository;
import com.alexis.picsas.service.dto.CheckoutDto;
import com.alexis.picsas.service.dto.OrderDto;

import com.alexis.picsas.service.dto.OrderItemDto;
import com.alexis.picsas.service.dto.OrderItemResponseDto;

import com.alexis.picsas.service.exception.OrderIdNotExists;
import com.alexis.picsas.service.exception.PizzaNotExists;
import com.alexis.picsas.service.exception.ZeroItems;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class OrderPizzaService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final PizzaRepository pizzaRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;
    private final CurrentUserService currentUserService;
    public OrderPizzaService(OrderRepository orderRepository,
                             OrderMapper orderMapper,
                             PizzaRepository pizzaRepository,
                             OrderItemRepository orderItemRepository,
                             OrderItemMapper orderItemMapper,
                             CurrentUserService currentUserService) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.pizzaRepository = pizzaRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderItemMapper = orderItemMapper;
        this.currentUserService = currentUserService;
    }
    @Transactional
    public OrderDto createCart(){
        CustomerEntity customer =
                currentUserService.getCurrentCustomer();
        Optional <OrderEntity> findOpenCart = orderRepository.findByCustomerAndStatus(customer,Status.CART);
        if(findOpenCart.isPresent()){
            OrderEntity orderEntity = findOpenCart.get();
            return orderMapper.toDto(orderEntity);
        }
        else{
            //Crear el orderEntity que inicializa el cart.
            OrderEntity order = new OrderEntity();
            //Defino con que se va guardar el objeto
            order.setCustomer(customer);
            order.setStatus(Status.CART);
            order.setTotal(BigDecimal.ZERO);
            //guardar en BD
            OrderEntity saveOrder = orderRepository.save(order);
            return orderMapper.toDto(saveOrder);
        }

    }
    @Transactional
    public OrderItemResponseDto addPizzaService(Integer orderid, OrderItemDto itemDto){
        Integer pizzaId = itemDto.getPizzaId();
        Integer quantity = itemDto.getQuantity();
        CustomerEntity customer =
                currentUserService.getCurrentCustomer();
        OrderEntity cart = orderRepository.findByIdOrderAndStatusAndCustomerIdCustomer(orderid,Status.CART,customer.getIdCustomer())
                .orElseThrow(()->new OrderIdNotExists("El pedido no exist/no hay carrito"));
        PizzaEntity pizza = pizzaRepository
                .findByIdPizzaAndAvailableTrue(pizzaId)
                .orElseThrow(()-> new PizzaNotExists("pizza no encontrada o agotada"));
        BigDecimal price = pizza.getPrice();
        //No tengo almace, por lo que no actualizo inventario.
        Optional<OrderItemEntity> existingItem = orderItemRepository
                .findByIdOrderAndIdPizza(orderid,pizzaId);
        OrderItemEntity saved = new OrderItemEntity();
        if(existingItem.isPresent()){
            OrderItemEntity orderItem = existingItem.get();
            Integer currentQuantity = orderItem.getQuantity();
            Integer sum = currentQuantity+quantity;
            orderItem.setQuantity(sum);
            saved = orderItemRepository.save(orderItem);

        }else{
            OrderItemEntity orderItem = new OrderItemEntity();
            orderItem.setIdOrder(orderid);
            orderItem.setIdPizza(pizzaId);
            orderItem.setPrice(price);
            orderItem.setQuantity(quantity);
            saved = orderItemRepository.save(orderItem);
        }
        //operacion que multiplica precio por cantidad
        BigDecimal subtotal = price.multiply(BigDecimal.valueOf(quantity));
        //Traemos el total de la tabla y le sumamos
        cart.setTotal(cart.getTotal().add(subtotal));
        orderRepository.save(cart);
    return orderItemMapper.toDto(saved);
    }
    @Transactional
    public OrderItemResponseDto updatePizzaService(Integer orderId, OrderItemDto itemDto){
        Integer pizzaId = itemDto.getPizzaId();
        Integer quantity = itemDto.getQuantity();
        //busco si el pedido y el status es el adecuado
        CustomerEntity customer =
                currentUserService.getCurrentCustomer();
        OrderEntity cart = orderRepository.findByIdOrderAndStatusAndCustomerIdCustomer(orderId,Status.CART,customer.getIdCustomer())
                .orElseThrow(()->new OrderIdNotExists("El pedido no exist/no hay carrito"));
        //busco si existe el pedido, relacionado con la pizza.
        OrderItemEntity existingItem = orderItemRepository
                .findByIdOrderAndIdPizza(orderId,pizzaId).orElseThrow(()->new OrderIdNotExists("Pedido o pizza no encontrada"));
        //se, si lo encontro o si no existe.
        //Como existe modifico el quantity y el valor en total
        //primero, modifica el quantity;
        existingItem.setQuantity(quantity);
        OrderItemEntity orderItemEntity = this.orderItemRepository.save(existingItem);
        //recalcular el carrito
        List<OrderItemEntity> items = this.orderItemRepository.findByIdOrder(orderId);
        BigDecimal total = items.stream()
                .map((item)->item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO,BigDecimal::add);

        //Agregamos el total, a el objeto cart que ya trae la entidad o el objeto que tiene el order_pizza
        cart.setTotal(total);
        this.orderRepository.save(cart);
        return orderItemMapper.toDto(orderItemEntity);
    }
    @Transactional
    public void deletePizzaService(Integer orderId,Integer pizzaId) {
        CustomerEntity customer =
                currentUserService.getCurrentCustomer();
        OrderEntity cart = orderRepository.findByIdOrderAndStatusAndCustomerIdCustomer(orderId, Status.CART,customer.getIdCustomer())
                .orElseThrow(() -> new OrderIdNotExists("El pedido no exist/no hay carrito"));
        OrderItemEntity existingItem = orderItemRepository
                .findByIdOrderAndIdPizza(orderId, pizzaId)
                .orElseThrow(() -> new OrderIdNotExists("el item/pizza dentro del pedido no encontrado"));
        orderItemRepository.delete(existingItem);
        //recalcular el carrito
        List<OrderItemEntity> items = this.orderItemRepository.findByIdOrder(orderId);
        BigDecimal total = items.stream()
                .map((item)->item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO,BigDecimal::add);

        //Agregamos el total, a el objeto cart que ya trae la entidad o el objeto que tiene el order_pizza
        cart.setTotal(total);
        this.orderRepository.save(cart);
    }
    @Transactional
    public void checkoutPizzaService(Integer orderId, CheckoutDto checkoutDto){
        CustomerEntity customer =
                currentUserService.getCurrentCustomer();
        OrderEntity cart =orderRepository
                .findByIdOrderAndStatusAndCustomerIdCustomer
                        (
                                orderId,
                                Status.CART,
                                customer.getIdCustomer()
                        ).
                orElseThrow(()->new OrderIdNotExists("Order with your id customer is not found"));
        List <OrderItemEntity> items = orderItemRepository.findByIdOrder(cart.getIdOrder());
        if(items.isEmpty()){
            throw new ZeroItems("You have zero items on your cart.");
        }
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItemEntity item : items){
            if(item.getQuantity() <= 0){
                throw new ZeroItems("You have zero items on your cart.");
            }
            this.pizzaRepository
                    .findByIdPizzaAndAvailableTrue(item.getIdPizza())
                    .orElseThrow(()-> new PizzaNotExists("pizza no encontrada o agotada"));
            BigDecimal subtotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            total = total.add(subtotal);
        }
        cart.setAdditionalNotes(checkoutDto.getAdditional_notes());
        cart.setMethod(checkoutDto.getMethod());
        cart.setTotal(total);
        cart.setStatus(Status.PENDING_PAYMENT);
        orderRepository.save(cart);
    }
    @Transactional
    public void payPizzaService(Integer orderId){
        CustomerEntity customer =
                currentUserService.getCurrentCustomer();
        OrderEntity order = this.orderRepository.findByIdOrderAndStatusAndCustomerIdCustomer(
                orderId,
                Status.PENDING_PAYMENT,
                customer.getIdCustomer()
        ).orElseThrow(()->new OrderIdNotExists("Not Exist an order with pending payment"));
        //Simula el pago
        order.setStatus(Status.PAID);

        this.orderRepository.save(order);

    }
}

