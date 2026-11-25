package com.alexis.picsas.service;

import com.alexis.picsas.persistence.entity.CustomerEntity;
import com.alexis.picsas.persistence.repository.CustomerRepository;
import com.alexis.picsas.service.exception.CustomerNotExists;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentUserService {
    private final CustomerRepository customerRepository;

    public CustomerEntity getCurrentCustomer(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return customerRepository
                .findByUserUsername(user.getUsername())
                .orElseThrow(()-> new CustomerNotExists(user.getUsername()));
    }
}
