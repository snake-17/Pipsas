package com.alexis.picsas.service;

import com.alexis.picsas.persistence.entity.CustomerEntity;
import com.alexis.picsas.persistence.repository.CustomerRepository;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }
    public CustomerEntity findByPhone(String phone){
        return this.customerRepository.findByPhoneNumber(phone);
    }
}
