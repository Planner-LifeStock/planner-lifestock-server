package com.lifestockserver.lifestock.rank.service;

import com.lifestockserver.lifestock.rank.dto.UserAssetDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class RankService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ZSetOperations<String, Object> zSetOperations;

    @Autowired
    public RankService(RedisTemplate<String, Object> redisTemplate){
        this.redisTemplate = redisTemplate;
        this.zSetOperations = redisTemplate.opsForZSet();
    }

    private final String KEY = "user:asset:ranking";

    // 유저 자산 업데이트 (Sorted Set에 유저 ID와 자산 저장)
    public void updateUserAsset(Long userId, Long totalAssets){
        zSetOperations.add(KEY, String.valueOf(userId), totalAssets.doubleValue());

        //만료 시간 정의
        redisTemplate.expire(KEY, 1, TimeUnit.DAYS);
    }

    // 특정 유저의 순위 조회
    public Long getUserRank(Long userId){
        return zSetOperations.reverseRank(KEY, String.valueOf(userId));
    }

    public  List<UserAssetDto> getTopUsers(int count){
        Set<ZSetOperations.TypedTuple<Object>> topUsers = zSetOperations.reverseRangeWithScores(KEY, 0, count - 1);

        return topUsers.stream().map(tuple -> UserAssetDto.builder()
                .userId(Long.valueOf((String) tuple.getValue()))
                .totalAssets(tuple.getScore().longValue())
                .build()).collect(Collectors.toList());
    }

    public Double getUserTotalAsset(Long userID){
        return zSetOperations.score(KEY, userID);
    }

    /*
    public Page<ZSetOperations.TypedTuple<Object>> getTopUsersByPage(Pageable pageable){
        int start = (int)pageable.getOffset();
        int end = (start + pageable.getPageSize()) - 1;

        Set<ZSetOperations.TypedTuple<Object>> users = zSetOperations.reverseRangeWithScores(KEY, start, end);
        long total = zSetOperations.zCard(KEY);

        List<ZSetOperations.TypedTuple<Object>> userList = users.stream().collect(Collectors.toList());

        return new PageImpl<>(userList, pageable, total);
    }//페이지네이션
     */
    // 페이지네이션 (UserAssetDto로 반환)
    public Page<UserAssetDto> getTopUsersByPage(Pageable pageable){
        int start = (int)pageable.getOffset();
        int end = (start + pageable.getPageSize()) - 1;

        Set<ZSetOperations.TypedTuple<Object>> users = zSetOperations.reverseRangeWithScores(KEY, start, end);
        long total = zSetOperations.zCard(KEY);

        List<UserAssetDto> userList = users.stream()
                .map(tuple -> UserAssetDto.builder()
                        .userId(Long.valueOf((String) tuple.getValue()))
                        .totalAssets(tuple.getScore().longValue())
                        .build())
                .collect(Collectors.toList());

        return new PageImpl<>(userList, pageable, total);
    }


    //현재 유저 기준으로 앞뒤 n명의 유저 반환
    public Set<ZSetOperations.TypedTuple<Object>> getSurroundingUsers(Long userId, int n) {
        Long rank = getUserRank(userId);
        if(rank == null){
            return Set.of();
        }

        int start = Math.max(0, rank.intValue() - n);
        int end = rank.intValue() + n;

        return zSetOperations.reverseRangeWithScores(KEY, start, end);
    }
}
