package com.alexis.picsas.persistence.repository;

import com.alexis.picsas.persistence.entity.UserRoleEntity;
import com.alexis.picsas.persistence.entity.UserRoleId;
import org.springframework.data.repository.CrudRepository;

public interface UserRoleRepository extends CrudRepository<UserRoleEntity, UserRoleId> {
    void deleteByUsername(String username);
}
