package com.alexis.picsas.persistence.mapper;

import com.alexis.picsas.persistence.entity.CustomerEntity;
import com.alexis.picsas.service.dto.CustomerDto;
import com.alexis.picsas.service.dto.RegisterDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerEntity toCutomerEntity(RegisterDto registerDto);

}
