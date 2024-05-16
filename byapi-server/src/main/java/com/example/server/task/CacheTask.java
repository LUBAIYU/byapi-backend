package com.example.server.task;

import com.example.common.constant.CommonConsts;
import com.example.common.enums.ErrorCode;
import com.example.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Set;

/**
 * 删除过期随机数定时任务
 *
 * @author by
 */
@Component
@Slf4j
public class CacheTask {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 每一分钟检查一次缓存，将过期随机数删除，指定过期时间为10分钟
     */
    @Scheduled(cron = "0 * * * * ? ")
    public void deleteExpireCache() {
        log.info("task begin ...");
        //判断key是否存在
        if (Boolean.FALSE.equals(redisTemplate.hasKey(CommonConsts.NONCE_KEY))) {
            return;
        }
        //遍历zSet中的每一个随机数，判断是否过期
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        Set<ZSetOperations.TypedTuple<Object>> typedTuples = zSetOperations.rangeWithScores(CommonConsts.NONCE_KEY, 0, -1);
        if (CollectionUtils.isEmpty(typedTuples)) {
            return;
        }
        for (ZSetOperations.TypedTuple<Object> tuple : typedTuples) {
            Object value = tuple.getValue();
            Double score = tuple.getScore();
            long currentTimeMillis = System.currentTimeMillis();
            //当时间间隔超过10分钟进行删除
            if (score != null && currentTimeMillis / 1000.0 - score.longValue() > 10 * 60) {
                try {
                    zSetOperations.remove(CommonConsts.NONCE_KEY, value);
                    log.info("del expire nonce:{}", value);
                } catch (Exception e) {
                    log.error("redis delete expire cache error", e);
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, CommonConsts.CACHE_DEL_ERROR);
                }
            }
        }
        log.info("task end ...");
    }
}
