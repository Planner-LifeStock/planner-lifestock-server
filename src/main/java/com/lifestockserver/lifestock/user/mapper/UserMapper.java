package com.lifestockserver.lifestock.user.mapper;

import com.lifestockserver.lifestock.user.domain.User;
import com.lifestockserver.lifestock.user.dto.UserCreateDto;
import com.lifestockserver.lifestock.user.dto.UserResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // Dto -> Entity
    @Mapping(target = "id", ignore = true)  // id는 자동 생성되므로 무시
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "asset", ignore = true)
    @Mapping(target = "companies", ignore = true)
    User toEntity(UserCreateDto userCreateDto);

    // Entity -> Dto
    UserResponseDto toDto(User user);
}
