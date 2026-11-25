package com.alexis.picsas.service;

import com.alexis.picsas.persistence.entity.CustomerEntity;
import com.alexis.picsas.persistence.entity.Role;
import com.alexis.picsas.persistence.entity.UserEntity;
import com.alexis.picsas.persistence.entity.UserRoleEntity;
import com.alexis.picsas.persistence.mapper.CustomerMapper;
import com.alexis.picsas.persistence.mapper.UserMapper;
import com.alexis.picsas.persistence.repository.CustomerRepository;
import com.alexis.picsas.persistence.repository.UserRepository;
import com.alexis.picsas.persistence.repository.UserRoleRepository;
import com.alexis.picsas.service.exception.UserAlreadyExists;
import com.alexis.picsas.service.dto.LoginDto;
import com.alexis.picsas.service.dto.RegisterDto;
import com.alexis.picsas.web.config.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final CustomerMapper customerMapper;
    private final UserRoleRepository userRoleRepository;

    public AuthService(
            UserRepository userRepository,
            CustomerRepository customerRepository,
            AuthenticationManager authenticationManager,
            JwtUtils jwtUtils,
            PasswordEncoder passwordEncoder,
            UserMapper userMapper,
            CustomerMapper customerMapper,
            UserRoleRepository userRoleRepository ) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.customerMapper = customerMapper;
        this.userRoleRepository = userRoleRepository;

    }
    public String login(LoginDto loginDto){
        UsernamePasswordAuthenticationToken login = new UsernamePasswordAuthenticationToken(
                loginDto.getUsername(),
                loginDto.getPassword()
        );
        Authentication authentication = authenticationManager.authenticate(login);
        return jwtUtils.create(loginDto.getUsername());
    }
    @Transactional
    public void registerService(RegisterDto registerDto){
        if(userRepository.findById(registerDto.getUsername()).isPresent()){
            throw new UserAlreadyExists(registerDto.getUsername());
        }
        UserEntity user = userMapper.toUserEntity(registerDto);
        user.setPassword(
                passwordEncoder.encode(registerDto.getPassword()));
        user.setLocked(false);
        user.setDisabled(false);
        userRepository.save(user);

        CustomerEntity customer = customerMapper.toCutomerEntity(registerDto);
        customer.setIdCustomer(UUID.randomUUID().toString());
        customer.setUser(user);
        customerRepository.save(customer);

        UserRoleEntity userRole = new UserRoleEntity();
        userRole.setUsername(registerDto.getUsername());
        userRole.setRole(Role.CUSTOMER);
        userRole.setGrantedDate(LocalDateTime.now());
        userRoleRepository.save(userRole);

    }
}
