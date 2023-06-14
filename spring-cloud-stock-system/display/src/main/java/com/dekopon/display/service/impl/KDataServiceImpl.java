package com.dekopon.display.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dekopon.display.configuration.RabbitConfiguration;
import com.dekopon.display.configuration.RedisConfiguration;
import com.dekopon.display.dao.KDataMapper;
import com.dekopon.display.entity.KDataEntity;
import com.dekopon.display.service.KDataService;
import com.dekopon.pojo.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author dekopon
 * @since 2023/6/13 19:14
 */
@Service
public class KDataServiceImpl implements KDataService {
    @Autowired
    KDataMapper kDataMapper;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    Gson gson;
    @Autowired
    ThreadPoolExecutor pool;

    @Override
    public List<KDataEntity> getExistedData(String code) {
        // 先查询redis
        String data = redisTemplate.opsForValue().get(RedisConfiguration.K_DATA_PREFIX + code);
        // 有则直接返回
        if (StringUtils.hasText(data)) {
            return gson.fromJson(data, new TypeToken<List<KDataEntity>>() {
            }.getType());
        }
        // 没有再查询数据库
        List<KDataEntity> kDataEntities =
                kDataMapper.selectList(new LambdaQueryWrapper<KDataEntity>().eq(KDataEntity::getCode, code).orderByDesc(KDataEntity::getTime).last("limit 24"));

        // 如果有值，写入redis并返回
        // TODO: 防止缓存击穿
        if (!kDataEntities.isEmpty()) {
            long current = System.currentTimeMillis();
            redisTemplate.opsForValue().set(RedisConfiguration.K_DATA_PREFIX + code, gson.toJson(kDataEntities),
                    nextHalfHourMillis(current) - current, TimeUnit.MILLISECONDS);
        }
        // 否则异步交给python取获取股票数据
        if (kDataEntities.isEmpty() || nextHalfHourMillis(kDataEntities.get(kDataEntities.size() - 1).getTime().getTime()) < System.currentTimeMillis()) {
            pool.execute(() -> {
                rabbitTemplate.convertAndSend("", RabbitConfiguration.K_DATA_QUERY_QUEUE_NAME, R.other(10000));
            });
        }
        Collections.reverse(kDataEntities);

        return kDataEntities;
    }

    @Override
    public KDataEntity getLatestKData(String code) {
        // 获取加载过的最新的记录
        return kDataMapper.selectOne(new LambdaQueryWrapper<KDataEntity>().eq(KDataEntity::getCode, code).orderByDesc(KDataEntity::getTime).last("limit 1"));
    }

    private long nextHalfHourMillis(long timeStamp) {
        return (timeStamp / (30 * 60 * 1000) + 1) * (30 * 60 * 1000);
    }
}
