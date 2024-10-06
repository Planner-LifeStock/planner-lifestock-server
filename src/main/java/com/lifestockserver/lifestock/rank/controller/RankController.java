package com.lifestockserver.lifestock.rank.controller;

import com.lifestockserver.lifestock.rank.dto.UserAssetDto;
import com.lifestockserver.lifestock.rank.service.RankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/ranking")
public class RankController {

    private final RankService rankService;

    @Autowired
    public RankController(RankService rankService) {
        this.rankService = rankService;
    }

    //유저 자산 업데이트 API
    @PostMapping("/update")
    public void updateUserAsset(@RequestBody UserAssetDto userAssetDto){
        //System.out.println("Received request to update user asset : userID = " + userId + ", totalAssets = " + totalAssets);
        rankService.updateUserAsset(userAssetDto.getUserId(), userAssetDto.getTotalAssets());
    }

    //유저 순위 조회 API
    @GetMapping("/rank/{userId}")
    public Long getUserRank(@PathVariable Long userId){
        return rankService.getUserRank(userId);
    }

    //상위 N명의 유저 조회 API
    @GetMapping("/top/{count}")
    public Set<?> getTopUsers(@PathVariable int count){
        return rankService.getTopUsers(count);
    }
}