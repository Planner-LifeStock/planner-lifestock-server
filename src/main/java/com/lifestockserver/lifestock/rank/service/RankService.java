package com.lifestockserver.lifestock.rank.service;

import com.lifestockserver.lifestock.rank.dto.UserAssetDto;
import com.lifestockserver.lifestock.user.repository.UserRepository;
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
    private final UserRepository userRepository;

    @Autowired
    public RankService(RedisTemplate<String, Object> redisTemplate, UserRepository userRepository){
        this.redisTemplate = redisTemplate;
        this.zSetOperations = redisTemplate.opsForZSet();
        this.userRepository = userRepository;
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
        Long rank = zSetOperations.reverseRank(KEY, String.valueOf(userId));
        if(rank == null){
            return -1L;
        }
        return rank;
    }

    public List<UserAssetDto> getTopUsers(int count) {
        Set<ZSetOperations.TypedTuple<Object>> topUsers = zSetOperations.reverseRangeWithScores(KEY, 0, count - 1);

        return topUsers.stream().map(tuple -> {
            Long userId = Long.valueOf((String) tuple.getValue());
            Long totalAssets = tuple.getScore().longValue();

            // userId를 통해 UserRepository에서 realName 조회
            String realName = userRepository.findById(userId)
                    .map(user -> user.getRealName())
                    .orElse("Unknown"); // 실명이 없을 경우 기본값 설정

            return UserAssetDto.builder()
                    .userId(userId)
                    .totalAssets(totalAssets)
                    .userRealName(realName) // 실명 추가
                    .build();
        }).collect(Collectors.toList());
    }


    public Double getUserTotalAsset(Long userID){
        return zSetOperations.score(KEY, userID);
    }

    public Page<UserAssetDto> getTopUsersByPage(Pageable pageable) {
        int start = (int)pageable.getOffset();
        int end = (start + pageable.getPageSize()) - 1;

        Set<ZSetOperations.TypedTuple<Object>> users = zSetOperations.reverseRangeWithScores(KEY, start, end);
        long total = zSetOperations.zCard(KEY);

        List<UserAssetDto> userList = users.stream()
                .map(tuple -> {
                    Long userId = Long.valueOf((String) tuple.getValue());
                    Long totalAssets = tuple.getScore().longValue();
                    // userId를 사용해 실명 조회
                    String realName = userRepository.findById(userId)
                            .map(user -> user.getRealName())
                            .orElse("Unknown"); // 실명이 없을 경우 기본값 설정
                    return UserAssetDto.builder()
                            .userId(userId)
                            .totalAssets(totalAssets)
                            .userRealName(realName) // 실명 추가
                            .build();
                })
                .collect(Collectors.toList());

        return new PageImpl<>(userList, pageable, total);
    }

    public List<UserAssetDto> getSurroundingUsers(Long userId, int n) {
        Long rank = getUserRank(userId);
        if (rank == null) {
            return List.of();
        }

        int start = Math.max(0, rank.intValue() - n);
        int end = rank.intValue() + n;

        Set<ZSetOperations.TypedTuple<Object>> surroundingUsers = zSetOperations.reverseRangeWithScores(KEY, start, end);

        return surroundingUsers.stream()
                .map(tuple -> {
                    Long surroundingUserId = Long.valueOf((String) tuple.getValue());
                    Long totalAssets = tuple.getScore().longValue();

                    // userId를 통해 UserRepository에서 realName 조회
                    String realName = userRepository.findById(surroundingUserId)
                            .map(user -> user.getRealName())
                            .orElse("Unknown"); // 실명이 없을 경우 기본값 설정

                    return UserAssetDto.builder()
                            .userId(surroundingUserId)
                            .totalAssets(totalAssets)
                            .userRealName(realName) // 실명 추가
                            .build();
                })
                .collect(Collectors.toList());
    }

}
