package com.lifestockserver.lifestock.rank.controller;

import com.lifestockserver.lifestock.rank.dto.UserAssetDto;
import com.lifestockserver.lifestock.rank.service.RankService;
import com.lifestockserver.lifestock.user.domain.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/ranking")
public class RankController {

    private final RankService rankService;

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
    @GetMapping("/rank")
    public Long getUserRank(@AuthenticationPrincipal CustomUserDetails userDetails){
        return rankService.getUserRank(userDetails.getUserId());
    }

    //상위 N명의 유저 조회 API
    @GetMapping("/top")
    public Set<?> getTopUsers(@RequestParam(value = "size", defaultValue = "30") int size){
        return rankService.getTopUsers(size);
    }

    @GetMapping() //상위 N명 반환(페이지네이션)
    public Page<?> getRankingPage(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "30") int size
    ){
        Pageable pageable = PageRequest.of(page, size);
        return rankService.getTopUsersByPage(pageable);
    }

    @GetMapping("/surrounding")
    public Set<?> getSurroundingUsers(@AuthenticationPrincipal CustomUserDetails userDetails,
                                      @RequestParam(value = "n", defaultValue = "5") int n) {
        return rankService.getSurroundingUsers(userDetails.getUserId(), n);
    }

}
