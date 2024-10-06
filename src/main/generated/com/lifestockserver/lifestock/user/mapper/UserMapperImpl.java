package com.lifestockserver.lifestock.user.mapper;

import com.lifestockserver.lifestock.user.domain.User;
import com.lifestockserver.lifestock.user.dto.UserCreateDto;
import com.lifestockserver.lifestock.user.dto.UserResponseDto;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-10-07T02:49:39+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Eclipse Adoptium)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User toEntity(UserCreateDto userCreateDto) {
        if ( userCreateDto == null ) {
            return null;
        }

        User user = new User();

        user.setUsername( userCreateDto.getUsername() );
        user.setPassword( userCreateDto.getPassword() );
        user.setRealName( userCreateDto.getRealName() );
        user.setDisplayName( userCreateDto.getDisplayName() );
        user.setEmail( userCreateDto.getEmail() );
        user.setPhoneNumber( userCreateDto.getPhoneNumber() );

        return user;
    }

    @Override
    public UserResponseDto toDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserResponseDto userResponseDto = new UserResponseDto();

        userResponseDto.setStatus( user.getStatus() );
        userResponseDto.setId( user.getId() );
        userResponseDto.setUsername( user.getUsername() );
        userResponseDto.setRealName( user.getRealName() );
        userResponseDto.setEmail( user.getEmail() );
        userResponseDto.setDisplayName( user.getDisplayName() );
        userResponseDto.setPhoneNumber( user.getPhoneNumber() );
        userResponseDto.setRole( user.getRole() );

        return userResponseDto;
    }
}
