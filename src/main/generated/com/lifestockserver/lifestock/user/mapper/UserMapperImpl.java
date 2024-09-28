package com.lifestockserver.lifestock.user.mapper;

import com.lifestockserver.lifestock.user.domain.User;
import com.lifestockserver.lifestock.user.dto.UserCreateDto;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-09-28T15:29:36+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Amazon.com Inc.)"
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
    public UserCreateDto toDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserCreateDto userCreateDto = new UserCreateDto();

        userCreateDto.setUsername( user.getUsername() );
        userCreateDto.setPassword( user.getPassword() );
        userCreateDto.setRealName( user.getRealName() );
        userCreateDto.setDisplayName( user.getDisplayName() );
        userCreateDto.setEmail( user.getEmail() );
        userCreateDto.setPhoneNumber( user.getPhoneNumber() );

        return userCreateDto;
    }
}
