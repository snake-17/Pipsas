package com.alexis.picsas.persistence.repository;

import com.alexis.picsas.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends CrudRepository<UserEntity,String> {

        @Query("""
        SELECT u
        FROM UserEntity u
        LEFT JOIN FETCH u.roles
        WHERE u.username = :username
    """)
        Optional<UserEntity> findByUsernameWithRoles(@Param("username") String username);

}
