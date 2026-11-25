package com.alexis.picsas.service;

import com.alexis.picsas.persistence.entity.*;
import com.alexis.picsas.persistence.repository.CustomerRepository;
import com.alexis.picsas.persistence.repository.UserRepository;
import com.alexis.picsas.persistence.repository.UserRoleRepository;
import com.alexis.picsas.service.exception.CustomerNotExists;
import com.alexis.picsas.service.exception.UserAlreadyExists;
import com.alexis.picsas.service.exception.UserNotExist;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ConfigService {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final CustomerRepository customerRepository;

    public ConfigService(
            UserRepository userRepository,
            UserRoleRepository userRoleRepository,
            CustomerRepository customerRepository) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.customerRepository = customerRepository;
    }
    @Transactional
    public void toEmployeeService(String username){
        UserEntity user = userRepository.findById(username).orElseThrow(
                ()-> new CustomerNotExists(username)
                );
        userRoleRepository.deleteByUsername(username);
        UserRoleEntity userRoleEntity = new UserRoleEntity();
        userRoleEntity.setUsername(username);
        userRoleEntity.setRole(Role.EMPLOYEE);
        userRoleEntity.setGrantedDate(LocalDateTime.now());
        userRoleEntity.setUser(user);
        userRoleRepository.save(userRoleEntity);
        CustomerEntity customer = customerRepository.findByUserUsername(username).orElseThrow(()->new UserNotExist(username));
        customerRepository.delete(customer);
    }
}
