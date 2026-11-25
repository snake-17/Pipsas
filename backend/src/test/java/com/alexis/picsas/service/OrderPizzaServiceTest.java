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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.text.html.Option;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.COLLECTION;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class OrderPizzaServiceTest {
    @Mock
    private CurrentUserService currentUserService;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private PizzaRepository pizzaRepository;
    @Mock
    private OrderItemMapper orderItemMapper;


    @InjectMocks
    private OrderPizzaService orderPizzaService;
    //test de cart
    @Test
    void debeRetornarElIdOrder(){
        //preparando los datos
        CustomerEntity customer = new CustomerEntity();
        when(currentUserService.getCurrentCustomer()).thenReturn(customer);
        //simulamos que no existe el carrito
        when(orderRepository.findByCustomerAndStatus(customer, Status.CART)).thenReturn(Optional.empty());
        //simulamos el save
        OrderEntity saveOrder = new OrderEntity();
        saveOrder.setIdOrder(123);
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(saveOrder);
        //Especificamos que debe devolver el mapper
        OrderDto orderDto= new OrderDto();
        orderDto.setOrderId(123);
        when(orderMapper.toDto(saveOrder)).thenReturn(orderDto);
        OrderDto resultado = orderPizzaService.createCart();
        assertThat(resultado).isNotNull();
        assertThat(resultado.getOrderId()).isEqualTo(123);
        //especificamos que queremos capturar el OrderEntity que le pase al repository
        ArgumentCaptor<OrderEntity> orderCaptor = ArgumentCaptor.forClass(OrderEntity.class);
        //ahora tenemos el objeto que se guardo
        verify(orderRepository, times(1)).save(orderCaptor.capture());
        OrderEntity captureOrder = orderCaptor.getValue();
        assertThat(captureOrder.getCustomer()).isEqualTo(customer);
        assertThat(captureOrder.getStatus()).isEqualTo(Status.CART);
        assertThat(captureOrder.getTotal()).isEqualByComparingTo(BigDecimal.ZERO);
    }
    @Test
    void debeRetornarElCarritoExistente(){
        CustomerEntity customer = new CustomerEntity();
        OrderEntity order = new OrderEntity();
        order.setIdOrder(123);
        order.setCustomer(customer);
        order.setStatus(Status.CART);
        order.setTotal(BigDecimal.ZERO);

        OrderDto orderDto = new OrderDto();
        orderDto.setOrderId(123);

        when(currentUserService.getCurrentCustomer()).thenReturn(customer);
        when(orderRepository.findByCustomerAndStatus(customer,Status.CART)).thenReturn(Optional.of(order));
        when(orderMapper.toDto(order)).thenReturn(orderDto);

        //when
        OrderDto resultado = orderPizzaService.createCart();
        //then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getOrderId()).isEqualTo(123);
        //Si ya existe el carrito, createCart() NO debe guardar uno nuevo
        verify(orderRepository, never()).save(any(OrderEntity.class));
    }
    //test Delete
    @Test
    void debeBorrarElPedido(){
        CustomerEntity customer = new CustomerEntity();
        OrderEntity order = new OrderEntity();
        OrderItemEntity itemToDelete = new OrderItemEntity();
        OrderItemEntity remainingItem = new OrderItemEntity();
        customer.setIdCustomer("customer-123");
        order.setIdOrder(123);
        order.setStatus(Status.CART);
        order.setCustomer(customer);
        order.setTotal(BigDecimal.ZERO);
        //creamos el item que se va borrar
        itemToDelete.setIdPizza(1);
        //Creamos el item que se queda para simular el total
        remainingItem.setIdPizza(2);
        remainingItem.setPrice(new BigDecimal("15.0"));
        remainingItem.setQuantity(1);

        when(currentUserService.getCurrentCustomer()).thenReturn(customer);
        when(orderRepository.findByIdOrderAndStatusAndCustomerIdCustomer(123,Status.CART,"customer-123"))
                .thenReturn(Optional.of(order));
        when(orderItemRepository.findByIdOrderAndIdPizza(123,1))
                .thenReturn(Optional.of(itemToDelete));
        when(orderItemRepository.findByIdOrder(123))
                .thenReturn(List.of(remainingItem));

        //when
        orderPizzaService.deletePizzaService(123,1);

        //then
        verify(orderItemRepository).delete(itemToDelete);
        assertThat(order.getTotal()).isEqualByComparingTo(new BigDecimal("15.0"));
        verify(orderRepository).save(order);
    }
    //test checkout
    @Test
    void debeHacerCheckout(){
        CustomerEntity customer = new CustomerEntity();
        OrderEntity order = new OrderEntity();
        OrderItemEntity items = new OrderItemEntity();
        CheckoutDto checkoutDto = new CheckoutDto();
        PizzaEntity pizza = new PizzaEntity();
        pizza.setIdPizza(1);
        customer.setIdCustomer("customer-123");
        order.setIdOrder(123);
        items.setIdPizza(1);
        items.setQuantity(1);
        items.setIdOrder(123);
        items.setPrice(new BigDecimal("20.00"));
        checkoutDto.setAdditional_notes("this is a test");
        checkoutDto.setMethod(Method.D);

        when(currentUserService.getCurrentCustomer()).thenReturn(customer);
        when(orderRepository.findByIdOrderAndStatusAndCustomerIdCustomer(123,Status.CART,"customer-123"))
                .thenReturn(Optional.of(order));
        when(orderItemRepository.findByIdOrder(123)).thenReturn(List.of(items));
        when(pizzaRepository.findByIdPizzaAndAvailableTrue(1))
                .thenReturn(Optional.of(pizza));

        //when
        orderPizzaService.checkoutPizzaService(123,checkoutDto);
        //then
        assertThat(order.getStatus()).isEqualTo(Status.PENDING_PAYMENT);
        assertThat(order.getTotal()).isEqualByComparingTo(new BigDecimal("20.0"));
        assertThat(order.getMethod()).isEqualTo(Method.D);
        assertThat(order.getAdditionalNotes()).isEqualTo("this is a test");
        verify(orderRepository).save(order);
    }
    //pay
    @Test
    void debeHacerElPay(){
        CustomerEntity customer = new CustomerEntity();
        OrderEntity order = new OrderEntity();
        customer.setIdCustomer("customer-123");
        order.setIdOrder(123);
        order.setCustomer(customer);

        when(currentUserService.getCurrentCustomer()).thenReturn(customer);
        when(orderRepository.findByIdOrderAndStatusAndCustomerIdCustomer(123,Status.PENDING_PAYMENT,"customer-123"))
                .thenReturn(Optional.of(order));

        //when
        orderPizzaService.payPizzaService(123);
        //then
        assertThat(order.getStatus()).isEqualTo(Status.PAID);
        verify(orderRepository).save(order);
    }
    //test add pizza
    @Test
    void addPizzaItemNoExists(){

    CustomerEntity customer = new CustomerEntity();
    OrderEntity order = new OrderEntity();
    PizzaEntity pizza = new PizzaEntity();
    OrderItemEntity items = new OrderItemEntity();
    OrderItemDto dto = new OrderItemDto();
    OrderItemResponseDto responseDto = new OrderItemResponseDto();

    dto.setPizzaId(1);
    dto.setQuantity(1);
    customer.setIdCustomer("customer-123");
    //order.setIdOrder(123);
    order.setTotal(BigDecimal.ZERO);
    //pizza.setIdPizza(1);
    pizza.setPrice(new BigDecimal("20.0"));

    when(currentUserService.getCurrentCustomer())
            .thenReturn(customer);
    when(orderRepository.findByIdOrderAndStatusAndCustomerIdCustomer(
            123,
            Status.CART,
            "customer-123"))
            .thenReturn(Optional.of(order));
    when(pizzaRepository.findByIdPizzaAndAvailableTrue(1))
            .thenReturn(Optional.of(pizza));
    //items debe inicializar con idorder y idpizza?
    when(orderItemRepository.findByIdOrderAndIdPizza(
            123,
            1))
            .thenReturn(Optional.empty());
    //este when no se si va aqui

    when(orderItemRepository.save(any(OrderItemEntity.class)))
            .thenReturn(items);
    when(orderItemMapper.toDto(any(OrderItemEntity.class))).
            thenReturn(responseDto);
    //when
    orderPizzaService.addPizzaService(123,dto);
    //then
    ArgumentCaptor<OrderItemEntity> captor = ArgumentCaptor.forClass(OrderItemEntity.class);
    verify(orderItemRepository).save(captor.capture());
    OrderItemEntity savedItem = captor.getValue();
    assertThat(savedItem.getIdPizza()).isEqualTo(1);
    assertThat(savedItem.getQuantity()).isEqualTo(1);
    assertThat(savedItem.getPrice())
            .isEqualByComparingTo("20.0");
    assertThat(savedItem.getIdOrder()).isEqualTo(123);
    assertThat(order.getTotal()).isEqualByComparingTo("20.0");
    verify(orderRepository.save(order));
    }
    @Test
    public void addRepeatedPizza(){
        CustomerEntity customer = new CustomerEntity();
        OrderEntity order = new OrderEntity();
        OrderItemEntity existemItem = new OrderItemEntity();
        PizzaEntity pizza = new PizzaEntity();
        OrderItemDto dto = new OrderItemDto();
        OrderItemResponseDto responseDto = new OrderItemResponseDto();

        customer.setIdCustomer("customer-123");
        order.setTotal(BigDecimal.ZERO);
        pizza.setPrice(new BigDecimal("20.0"));
        dto.setQuantity(2);
        dto.setPizzaId(1);

        //simulamos que existe que existen 3 pizzas con id 1 y precio 20
        existemItem.setIdOrder(123);
        existemItem.setIdPizza(1);
        existemItem.setQuantity(3);
        existemItem.setPrice(new BigDecimal("20.0"));

        when(currentUserService.getCurrentCustomer()).thenReturn(customer);
        when(orderRepository.findByIdOrderAndStatusAndCustomerIdCustomer(
                123,
                Status.CART,
                "customer-123"))
                .thenReturn(Optional.of(order));
        when(pizzaRepository.findByIdPizzaAndAvailableTrue(1))
                .thenReturn(Optional.of(pizza));
        when(orderItemRepository.findByIdOrderAndIdPizza(
                123,
                1))
                .thenReturn(Optional.of(existemItem));
        when(orderItemRepository.save(any(OrderItemEntity.class)))
                .thenReturn(existemItem);
        when(orderItemMapper.toDto(any(OrderItemEntity.class))).thenReturn(responseDto);

        //When
        orderPizzaService.addPizzaService(123,dto);

        //then
        assertThat(order.getTotal()).isEqualByComparingTo("40.0");
        assertThat(existemItem.getQuantity()).isEqualTo(5);
        verify(orderItemRepository).save(existemItem);
        verify(orderRepository).save(order);
        verify(orderItemMapper).toDto(existemItem);

    }
    //exception
    @Test
    public void addPizzaService_whenOrderDoesNotExist_shouldThrowOrderIdNotExists(){
        CustomerEntity customer = new CustomerEntity();
        OrderItemDto dto = new OrderItemDto();

        customer.setIdCustomer("customer-123");

        when(currentUserService.getCurrentCustomer())
                .thenReturn(customer);
        when(orderRepository.findByIdOrderAndStatusAndCustomerIdCustomer(123,Status.CART,"customer-123"))
                .thenReturn(Optional.empty());


        assertThatThrownBy(() ->
                orderPizzaService.addPizzaService(123, dto)
        )
                .isInstanceOf(OrderIdNotExists.class);
        verifyNoInteractions(pizzaRepository, orderItemRepository);
    }
    @Test
    public void addPizzaService_whenPizzaDoesNotExists_shouldThrowPizzaNotExists(){
        CustomerEntity customer = new CustomerEntity();
        OrderEntity order = new OrderEntity();

        OrderItemDto dto = new OrderItemDto();
        dto.setPizzaId(1);
        customer.setIdCustomer("customer-123");

        when(currentUserService.getCurrentCustomer()).thenReturn(customer);
        when(orderRepository.findByIdOrderAndStatusAndCustomerIdCustomer(
                123,
                Status.CART,
                "customer-123"
        )).thenReturn(Optional.of(order));
        when(pizzaRepository.findByIdPizzaAndAvailableTrue(1))
                .thenReturn(Optional.empty());

        assertThatThrownBy( () ->
                orderPizzaService.addPizzaService(123,dto)
        )
                .isInstanceOf(PizzaNotExists.class);
        verifyNoInteractions(orderItemRepository);
        verify(orderRepository,never()).save(any());

    }
    //Test de actualizar el carrito
    @Test
    public void updatePizzaService_whenUpdatePizzaService_shouldReturnUpdatedCart(){
        CustomerEntity customer = new CustomerEntity();
        OrderEntity order = new OrderEntity();
        OrderItemDto dto = new OrderItemDto();
        OrderItemEntity items = new OrderItemEntity();
        OrderItemResponseDto responseDto = new OrderItemResponseDto();

        customer.setIdCustomer("customer-123");
        order.setTotal(BigDecimal.ZERO);
        items.setQuantity(1);
        items.setPrice(new BigDecimal("20.0"));

        dto.setPizzaId(1);
        dto.setQuantity(3);

        when(currentUserService.getCurrentCustomer())
                .thenReturn(customer);
        when(orderRepository.findByIdOrderAndStatusAndCustomerIdCustomer(
                123,
                Status.CART,
                "customer-123"
        )).thenReturn(Optional.of(order));
        when(orderItemRepository.findByIdOrderAndIdPizza(123,1)).thenReturn(Optional.of(items));
        //Esta raro, porque lo guardo de nuevo en items? no deberia crear uno nuevo?
        when(orderItemRepository.save(any(OrderItemEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderItemRepository.findByIdOrder(123)).thenReturn(List.of(items));
        when(orderRepository.save(any(OrderEntity.class)))
                .thenReturn(order);
        when(orderItemMapper.toDto(any(OrderItemEntity.class))).thenReturn(responseDto);

        //when
        OrderItemResponseDto currentResponse = orderPizzaService.updatePizzaService(123,dto);

        //then
        assertThat(currentResponse).isNotNull();
        assertThat(items.getQuantity()).isEqualTo(3);
        assertThat(order.getTotal()).isEqualTo("60.0");

        verify(orderItemRepository).save(items);
        verify(orderRepository).save(order);
    }
    //update, el carrito no existe
    @Test
    public void updatePizzaService_whenUpdatePizzaService_shouldThrowOrderIdNotExists(){
        CustomerEntity customer = new CustomerEntity();
        OrderItemDto dto = new OrderItemDto();
        customer.setIdCustomer("customer-123");

        when(currentUserService.getCurrentCustomer()).thenReturn(customer);
        when(orderRepository.findByIdOrderAndStatusAndCustomerIdCustomer(
                123,
                Status.CART,
                "customer-123"
        )).thenReturn(Optional.empty());
        assertThatThrownBy(()->
                orderPizzaService.updatePizzaService(123,dto)
        ).isInstanceOf(OrderIdNotExists.class);
        verify(orderRepository, never()).save(any());
        verifyNoInteractions(orderItemRepository);
    }
    //update, item no existe
    @Test
    public void updatePizzaService_whenUpdatePizzaService_ShouldThrowOrderItemNotExists(){
        CustomerEntity customer = new CustomerEntity();
        OrderEntity order = new OrderEntity();
        OrderItemDto dto = new OrderItemDto();

        customer.setIdCustomer("customer-123");
        dto.setPizzaId(1);
        dto.setQuantity(2);

        when(currentUserService.getCurrentCustomer()).thenReturn(customer);
        when(orderRepository.findByIdOrderAndStatusAndCustomerIdCustomer(
                123,
                Status.CART,
                "customer-123"
        )).thenReturn(Optional.of(order));
        when(orderItemRepository.findByIdOrderAndIdPizza(
                123,
                1
        )).thenReturn(Optional.empty());
        assertThatThrownBy(()->
                orderPizzaService.updatePizzaService(123,dto)
        ).isInstanceOf(OrderIdNotExists.class);
        verify(orderRepository, never()).save(any());
        verify(orderItemRepository, never()).save(any());

    }
    //checkoutPizzaService()
    @Test
    public void checkoutPizzaService_whenCartIsEmpty_ShouldThrowZeroItems(){
        CustomerEntity customer = new CustomerEntity();
        OrderEntity order = new OrderEntity();
        CheckoutDto dto = new CheckoutDto();

        customer.setIdCustomer("customer-123");
        order.setIdOrder(123);
        when(currentUserService.getCurrentCustomer()).thenReturn(customer);
        when(orderRepository.findByIdOrderAndStatusAndCustomerIdCustomer(
                123,
                Status.CART,
                "customer-123"
        )).thenReturn(
                Optional.of(order)
        );
        when(orderItemRepository.findByIdOrder(123)).thenReturn(Collections.emptyList());
        assertThatThrownBy( () ->
                orderPizzaService.checkoutPizzaService(123,dto)
        ).isInstanceOf(ZeroItems.class);
        verify(orderRepository,never()).save(any());
        verify(orderItemRepository,never()).save(any());
    }
//CHECKOUT. quantity zero
    @Test
    public void checkoutPizzaService_whenQuantityIsZero_shouldThrowZeroItems(){
        CustomerEntity customer = new CustomerEntity();
        OrderEntity order = new OrderEntity();
        OrderItemEntity items = new OrderItemEntity();
        CheckoutDto dto = new CheckoutDto();

        customer.setIdCustomer("customer-123");
        order.setIdOrder(123);
        items.setQuantity(-1);

        when(currentUserService.getCurrentCustomer()).thenReturn(customer);
        when(orderRepository.findByIdOrderAndStatusAndCustomerIdCustomer(
                123,
                Status.CART,
                "customer-123"
        )).thenReturn(Optional.of(order));
        when(orderItemRepository.findByIdOrder(123)).thenReturn(List.of(items));

        assertThatThrownBy( ()->
                orderPizzaService.checkoutPizzaService(123,dto)
        ).isInstanceOf(ZeroItems.class);
        verify(orderRepository,never()).save(any());
        verifyNoInteractions(pizzaRepository);
    }
    //CHECKOUT, pizza agotada
    @Test
    public void checkoutPizzaService_whenPizzaIsSoldOut_shouldThrowPizzaNotExists(){
        CustomerEntity customer = new CustomerEntity();
        OrderEntity order = new OrderEntity();
        OrderItemEntity items = new OrderItemEntity();
        CheckoutDto dto = new CheckoutDto();

        customer.setIdCustomer("customer-123");
        order.setIdOrder(123);
        items.setQuantity(2);
        items.setIdPizza(1);

        when(currentUserService.getCurrentCustomer()).thenReturn(customer);
        when(orderRepository.findByIdOrderAndStatusAndCustomerIdCustomer(
                123,
                Status.CART,
                "customer-123"
        )).thenReturn(Optional.of(order));
        when(orderItemRepository.findByIdOrder(123)).thenReturn(List.of(items));
        when(pizzaRepository.findByIdPizzaAndAvailableTrue(1)).thenReturn(Optional.empty());
        assertThatThrownBy(()->
                orderPizzaService.checkoutPizzaService(123,dto)
        ).isInstanceOf(PizzaNotExists.class);
        verify(orderRepository,never()).save(any());
        verify(currentUserService,times(1)).getCurrentCustomer();

    }
    //CHECKOUT, varias pizzas
    @Test
    public void checkoutPizzaService_whenThereAreSeveralPizzas_shouldReturnOk(){
        CustomerEntity customer = new CustomerEntity();
        OrderEntity order = new OrderEntity();
        OrderItemEntity itemOne = new OrderItemEntity();
        OrderItemEntity itemTwo = new OrderItemEntity();
        CheckoutDto dto = new CheckoutDto();
        PizzaEntity pizzaOne = new PizzaEntity();
        PizzaEntity pizzaTwo = new PizzaEntity();

        customer.setIdCustomer("customer-123");
        order.setIdOrder(123);
        order.setTotal(BigDecimal.ZERO);
        //item uno
        itemOne.setQuantity(2);
        itemOne.setIdPizza(1);
        itemOne.setPrice(new BigDecimal("20.0"));
        //itemDos
        itemTwo.setIdPizza(2);
        itemTwo.setQuantity(3);
        itemTwo.setPrice(new BigDecimal("15.0"));

        dto.setMethod(Method.D);
        dto.setAdditional_notes("test_1");

        when(currentUserService.getCurrentCustomer()).thenReturn(customer);
        when(orderRepository.findByIdOrderAndStatusAndCustomerIdCustomer(
                123,
                Status.CART,
                "customer-123"
        )).thenReturn(Optional.of(order));
        when(orderItemRepository.findByIdOrder(123)).thenReturn(List.of(itemOne,itemTwo));
        when(pizzaRepository.findByIdPizzaAndAvailableTrue(1)).thenReturn(Optional.of(pizzaOne));
        when(pizzaRepository.findByIdPizzaAndAvailableTrue(2)).thenReturn(Optional.of(pizzaTwo));
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(order);

        //when
        orderPizzaService.checkoutPizzaService(123,dto);
        //then
        assertThat(order.getTotal()).isEqualByComparingTo("85.0");
        verify(orderRepository,times(1)).save(order);


    }
    //pay pizza service exception.
    @Test
    public void payPizzaService_whenStatusIsDifferentToPending_shouldThrowOrderIdNotExists(){
        CustomerEntity customer = new CustomerEntity();

        customer.setIdCustomer("customer-123");

        when(currentUserService.getCurrentCustomer()).thenReturn(customer);
        when(orderRepository.findByIdOrderAndStatusAndCustomerIdCustomer(
                123,
                Status.PENDING_PAYMENT,
                "customer-123"
        )).thenReturn(Optional.empty());
        assertThatThrownBy(()->
                orderPizzaService.payPizzaService(123)
        ).isInstanceOf(OrderIdNotExists.class);
        verify(orderRepository,never()).save(any());
    }
}
