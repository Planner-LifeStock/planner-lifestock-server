package com.lifestockserver.lifestock.user.mapper;

import com.lifestockserver.lifestock.user.domain.User;
import com.lifestockserver.lifestock.user.dto.UserCreateDto;
import com.lifestockserver.lifestock.user.dto.UserResponseDto;
import com.lifestockserver.lifestock.user.dto.UserUpdateDto;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import com.lifestockserver.lifestock.user.domain.UserStatus;
import com.lifestockserver.lifestock.user.domain.UserRole;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-11-12T01:49:05+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.11 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User toEntity(UserCreateDto userCreateDto) {
        if ( userCreateDto == null ) {
            return null;
        }

        User.UserBuilder user = User.builder()
            .username(userCreateDto.getUsername())
            .password(userCreateDto.getPassword())
            .realName(userCreateDto.getRealName())
            .displayName(userCreateDto.getDisplayName())
            .email(userCreateDto.getEmail())
            .phoneNumber(userCreateDto.getPhoneNumber());

        return user.build();
    }

    @Override
    public UserResponseDto toDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserResponseDto.UserResponseDtoBuilder userResponseDto = UserResponseDto.builder()
            .status(user.getStatus())
            .id(user.getId())
            .username(user.getUsername())
            .realName(user.getRealName())
            .email(user.getEmail())
            .displayName(user.getDisplayName())
            .phoneNumber(user.getPhoneNumber())
            .role(user.getRole())
            .asset(user.getAsset());

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
