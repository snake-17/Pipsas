package com.alexis.picsas.persistence.audit;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuditUsername implements AuditorAware<String> {


    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Validamos si no hay autenticación, si no está autenticado, o si es el usuario anónimo de Spring
        if (authentication == null || !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal())) {
            return Optional.empty();
        }

        // Como ya filtramos a los anónimos, aquí es seguro hacer el casting a UserDetails
        UserDetails user = (UserDetails) authentication.getPrincipal();
        return Optional.of(user.getUsername());
    }
}
