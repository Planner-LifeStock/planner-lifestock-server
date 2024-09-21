package com.lifestockserver.lifestock.user.mapper;

import com.lifestockserver.lifestock.user.domain.User;
import com.lifestockserver.lifestock.user.dto.UserCreateDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // Dto -> Entity
    @Mapping(target = "id", ignore = true)  // id는 자동 생성되므로 무시
    User toEntity(UserCreateDto userCreateDto);

    // Entity -> Dto
    UserCreateDto toDto(User user);
}
