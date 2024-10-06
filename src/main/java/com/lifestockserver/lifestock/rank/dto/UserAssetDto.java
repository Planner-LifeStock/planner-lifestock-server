package com.lifestockserver.lifestock.rank.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserAssetDto {
    private Long userId;
    private double totalAssets;
}
