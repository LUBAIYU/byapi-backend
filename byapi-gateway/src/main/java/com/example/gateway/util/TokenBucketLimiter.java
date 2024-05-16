package com.example.gateway.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 令牌桶算法实现类
 *
 * @author by
 */
public class TokenBucketLimiter {
    /**
     * 上一次获取令牌的时间
     */
    public long lastTime = System.currentTimeMillis();
    /**
     * 令牌桶的容量
     */
    public int capacity = 4;
    /**
     * 生成令牌的速率（每秒2个）
     */
    public int rate = 2;
    /**
     * 当前桶里的令牌数量
     */
    public AtomicInteger tokens = new AtomicInteger(0);

    /**
     * 令牌桶算法实现限流，默认每次请求消耗一个令牌
     *
     * @return 是否限流
     */

    public synchronized boolean isLimited() {
        //获取当前时间
        long currentTime = System.currentTimeMillis();
        //计算时间间隔（单位为ms）
        long gap = currentTime - lastTime;
        //计算在这段时间内生成的令牌
        int generateCount = (int) (gap / 1000 * rate);
        //计算可能的令牌总数
        int allTokensCount = tokens.get() + generateCount;
        //设置令牌桶里的令牌数量，这里要取总数量与桶容量之间的最小值
        tokens.set(Math.min(capacity, allTokensCount));
        //开始获取令牌
        if (tokens.get() < 1) {
            //当前桶里令牌数量小于1个，进行限流
            return true;
        }
        //令牌数量足够，领取令牌，重新计算上一次获取令牌的时间
        tokens.decrementAndGet();
        lastTime = currentTime;
        return false;
    }
}
