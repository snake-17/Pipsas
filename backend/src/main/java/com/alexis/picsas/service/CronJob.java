package com.alexis.picsas.service;

import com.alexis.picsas.persistence.entity.Status;
import com.alexis.picsas.persistence.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;

@Service
public class CronJob {
    private final OrderRepository orderRepository;

    public CronJob(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Scheduled(cron = "0 * * * * *")
    public void expireCart(){
    LocalDateTime limitTime = LocalDateTime.now().minusMinutes(15);
    int cancelled = orderRepository.expiredOldCarts(Status.CART,Status.EXPIRED,limitTime);
        System.out.println("it has been cancelled "+cancelled+" carts");
    }
}
