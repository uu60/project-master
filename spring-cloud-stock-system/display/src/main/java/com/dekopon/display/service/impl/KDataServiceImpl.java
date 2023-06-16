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
import lombok.AllArgsConstructor;
import lombok.Data;
import org.redisson.Redisson;
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
    public List<KDataEntity> getMonthPeriodDailyData(String code) {

        // 先查询redis
        String data = redisTemplate.opsForValue().get(RedisConfiguration.K_DATA_DAILY_PREFIX + code);
        List<KDataEntity> kDataEntities;
        // 有则直接返回
        if (StringUtils.hasText(data)) {
            kDataEntities = gson.fromJson(data, new TypeToken<List<KDataEntity>>() {
            }.getType());
        } else {
            // 没有再查询数据库 30天或者当天数据
            kDataEntities = kDataMapper.selectList(new LambdaQueryWrapper<KDataEntity>().eq(KDataEntity::getCode,
                    code).eq(KDataEntity::getDaily, KDataEntity.Const.DAILY_DAILY).orderByDesc(KDataEntity::getTime).last("limit 30"));

            // 如果有值，写入redis并返回
            // TODO: 防止缓存击穿
            if (!kDataEntities.isEmpty()) {
                long current = System.currentTimeMillis();
                redisTemplate.opsForValue().set(RedisConfiguration.K_DATA_DAILY_PREFIX + code,
                        gson.toJson(kDataEntities), nextHalfHourMillis(current) - current, TimeUnit.MILLISECONDS);
            }
            // TODO: 附加预测数据
        }
        long lastStamp = 0;
        // 如果数据库没有数据 or 数据过期了，需要让python去查询
        if (kDataEntities.isEmpty() || (lastStamp =
                nextDayMillis(kDataEntities.get(kDataEntities.size() - 1).getTime().getTime())) < System.currentTimeMillis()) {
            long finalLastStamp = lastStamp;
            @AllArgsConstructor
            @Data
            class TempTO {
                String code;
                long last;
                int daily;
            }
            pool.execute(() -> {
                rabbitTemplate.convertAndSend("", RabbitConfiguration.K_DATA_QUERY_QUEUE_NAME,
                        R.ok().setData(new TempTO(code, finalLastStamp / 1000, 1)));
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

    private long nextDayMillis(long timeStamp) {
        return (timeStamp / (24 * 60 * 60 * 1000) + 1) * (24 * 60 * 60 * 1000);
    }
}
