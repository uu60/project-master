package com.dekopon.display.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dekopon.display.configuration.RabbitConfiguration;
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

    final static String REDIS_PREFIX = "k_data::";

    @Override
    public List<KDataEntity> getExistedData(String code) {
        // 先查询redis
        String data = redisTemplate.opsForValue().get(REDIS_PREFIX + code);
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
            redisTemplate.opsForValue().set(REDIS_PREFIX + code, gson.toJson(kDataEntities), millisToNextHour(),
                    TimeUnit.MILLISECONDS);
        }
        // 否则异步交给python取获取股票数据
        else {
            pool.execute(() -> {
                rabbitTemplate.convertAndSend("", RabbitConfiguration.K_DATA_QUERY_QUEUE_NAME, R.other(10000));
            });
        }
        return kDataEntities;
    }

    private long millisToNextHour() {
        long currentStamp = System.currentTimeMillis();
        long nextStamp = (currentStamp / (60 * 60 * 1000) + 1) * (60 * 60 * 1000);
        return nextStamp - currentStamp;
    }
}
