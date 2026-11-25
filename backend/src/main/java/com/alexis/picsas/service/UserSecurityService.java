package com.alexis.picsas.service;

import com.alexis.picsas.persistence.entity.UserEntity;
import com.alexis.picsas.persistence.entity.UserRoleEntity;
import com.alexis.picsas.persistence.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserSecurityService implements UserDetailsService {
    private final UserRepository userRepository;

    public UserSecurityService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        UserEntity userEntity = this.userRepository.findByUsernameWithRoles(username).orElseThrow(()->new UsernameNotFoundException("User "+username+" not found"));
        String [] roles = userEntity.getRoles().stream().map(userRole -> userRole.getRole().name()).toArray(String[]::new);
        return User.builder()
                .username(userEntity.getUsername())
                .password(userEntity.getPassword())
                .authorities(this.grantedAuthorities(roles))
                .accountLocked(userEntity.getLocked())
                .disabled(userEntity.getDisabled())
                .build();
    }
    private String [] getAuthorities(String role){
        List<String> authorities = new ArrayList<>();
        // Regla 1: Lectura del catálogo interno
        if ("ADMIN".equals(role) || "EMPLOYEE".equals(role)) {
            authorities.add("pizza:read");
        }
        // Regla 2: Generar órdenes aleatorias
        if ("ADMIN".equals(role) || "CUSTOMER".equals(role)) {
            authorities.add("random_order");
        }
        // Regla 3: Acciones exclusivas de gestión y administración
        if ("ADMIN".equals(role)) {
            authorities.add("pizza:create");
            authorities.add("pizza:update");
            authorities.add("pizza:update_price");
            authorities.add("pizza:delete");
        }
        return authorities.toArray(new String[0]);
    }
    private List<GrantedAuthority> grantedAuthorities(String [] roles){
        List<GrantedAuthority> authorities = new ArrayList<>(roles.length);
        for(String role: roles){
            authorities.add(new SimpleGrantedAuthority("ROLE_"+role));
            for(String authority: this.getAuthorities(role)){
                authorities.add(new SimpleGrantedAuthority(authority));
            }
        }
        return authorities;
    }
}
