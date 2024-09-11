package com.lifestockserver.lifestock.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class UserCreateDto {

    private final Long id;
    private final String username;
    private final String realName;
    private final String displayName;
    private final String email;
    private final String phoneNumber;

}