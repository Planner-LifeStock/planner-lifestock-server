package com.lifestockserver.lifestock.user.mapper;

import com.lifestockserver.lifestock.user.domain.User;
import com.lifestockserver.lifestock.user.dto.UserCreateDto;
import com.lifestockserver.lifestock.user.dto.UserResponseDto;
import com.lifestockserver.lifestock.user.dto.UserUpdateDto;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-11-13T23:06:24+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Eclipse Adoptium)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User toEntity(UserCreateDto userCreateDto) {
        if ( userCreateDto == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.username( userCreateDto.getUsername() );
        user.password( userCreateDto.getPassword() );
        user.realName( userCreateDto.getRealName() );
        user.displayName( userCreateDto.getDisplayName() );
        user.email( userCreateDto.getEmail() );
        user.phoneNumber( userCreateDto.getPhoneNumber() );

        return user.build();
    }

    @Override
    public UserResponseDto toDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserResponseDto.UserResponseDtoBuilder userResponseDto = UserResponseDto.builder();

        userResponseDto.status( user.getStatus() );
        userResponseDto.id( user.getId() );
        userResponseDto.username( user.getUsername() );
        userResponseDto.realName( user.getRealName() );
        userResponseDto.email( user.getEmail() );
        userResponseDto.displayName( user.getDisplayName() );
        userResponseDto.phoneNumber( user.getPhoneNumber() );
        userResponseDto.role( user.getRole() );
        userResponseDto.asset( user.getAsset() );

        return userResponseDto.build();
    }

    @Override
    public void updateEntityFromDto(UserUpdateDto userUpdateDto, User user) {
        if ( userUpdateDto == null ) {
            return;
        }

        user.setRealName( userUpdateDto.getRealName() );
        user.setDisplayName( userUpdateDto.getDisplayName() );
        user.setEmail( userUpdateDto.getEmail() );
        user.setPhoneNumber( userUpdateDto.getPhoneNumber() );
    }
}
