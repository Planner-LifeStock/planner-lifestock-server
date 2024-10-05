package com.lifestockserver.lifestock.rank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class RankService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ZSetOperations<String, Object> zSetOperations;

    @Autowired
    public RankService(RedisTemplate<String, Object> redisTemplate, ZSetOperations<String, Object> zSetOperations){
        this.redisTemplate = redisTemplate;
        this.zSetOperations = redisTemplate.opsForZSet();
    }


    private final String KEY = "user:asset:ranking";

    // 유저 자산 업데이트 (Sorted Set에 유저 ID와 자산 저장)
    public void updateUserAsset(Long userId, double totalAssets){
        zSetOperations.add(KEY, userId, totalAssets);
    }

    // 특정 유저의 순위 조회
    public Long getUserRank(Long userId){
        return zSetOperations.reverseRank(KEY, userId);
    }

    // 상위 N명의 유저 리스트 조회
    public Set<ZSetOperations.TypedTuple<Object>> getTopUsers(int count){
        return zSetOperations.reverseRangeByScoreWithScores(KEY, 0, count - 1);
    }

    public Double getUSerTotalAsset(String userID){
        return zSetOperations.score(KEY, userID);
    }
}
