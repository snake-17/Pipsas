package com.alexis.picsas.persistence.mapper;

import com.alexis.picsas.persistence.entity.UserEntity;
import com.alexis.picsas.service.dto.RegisterDto;
import com.alexis.picsas.service.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(UserEntity user);


    UserEntity toUserEntity(RegisterDto registerDto);
}
