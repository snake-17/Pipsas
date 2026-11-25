package com.alexis.picsas.persistence.repository;

import com.alexis.picsas.persistence.entity.CustomerEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CustomerRepository extends ListCrudRepository<CustomerEntity,String> {
    Optional<CustomerEntity> findByUserUsername(String username);
    @Query(value="SELECT c FROM CustomerEntity c WHERE c.phoneNumber=:phone")
    CustomerEntity findByPhoneNumber(@Param("phone") String phone);

}
