package com.alexis.picsas.service;

import com.alexis.picsas.persistence.entity.CustomerEntity;
import com.alexis.picsas.persistence.entity.Method;
import com.alexis.picsas.persistence.entity.OrderEntity;
import com.alexis.picsas.persistence.entity.Status;
import com.alexis.picsas.persistence.mapper.OrderMapper;
import com.alexis.picsas.persistence.projection.OrderSummary;
import com.alexis.picsas.persistence.repository.CustomerRepository;
import com.alexis.picsas.persistence.repository.OrderRepository;
import com.alexis.picsas.service.dto.OrderDto;
import com.alexis.picsas.service.dto.RandomOrderDto;
import com.alexis.picsas.service.exception.CustomerNotExists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final OrderMapper orderMapper;
    private static final Method DELIVERY = Method.D;
    private static final Method CARRYOUT = Method.C;
    private static final Method ON_SITE = Method.S;
    public OrderService(OrderRepository orderRepository, CustomerRepository customerRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.orderMapper = orderMapper;
    }
    public List<OrderEntity> getAll(){
        List<OrderEntity> orders = this.orderRepository.findAll();
        orders.forEach(o -> System.out.println(o.getCustomer()));
        return orders;
    }
    public List<OrderEntity> getTodayOrders(){
        LocalDateTime today = LocalDate.now().atTime(0,0);
        return this.orderRepository.findAllByCreatedDateAfter(today);
    }
    public List<OrderEntity> getOutsideOrders(){
        List<Method> methods = Arrays.asList(DELIVERY,CARRYOUT);
        return this.orderRepository.findAllByMethodIn(methods);
    }
    @Secured("ROLE_ADMIN")
    public List<OrderEntity> getCustomerOrders(String idCustomer){
        return this.orderRepository.findCustomer(idCustomer);
    }
    public OrderSummary getSummary(int orderId){
        return this.orderRepository.findSummary(orderId);
    }
    @Transactional
    public OrderDto createCart(){
        //traemos el authenticateusernametoken
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //sacamos el usuario, como en el jwt lo tenemos como user.
        User user = (User) authentication.getPrincipal();
        //De user sacamos el getUsername
        String username = user.getUsername();
        //Buscamos en el customer, el username
        CustomerEntity customer = customerRepository.findByUserUsername(username).orElseThrow(()-> new CustomerNotExists(username));
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
    @Transactional
    public boolean saveRandomOrder(RandomOrderDto randomOrderDto){
        return this.orderRepository.saveRandomOrder(randomOrderDto.getIdCustomer(),randomOrderDto.getMethod());
    }
}

