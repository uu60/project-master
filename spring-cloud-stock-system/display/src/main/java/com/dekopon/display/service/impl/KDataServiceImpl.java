package com.dekopon.display.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dekopon.display.config.RabbitConfiguration;
import com.dekopon.display.config.RedisConfiguration;
import com.dekopon.display.dao.KDataMapper;
import com.dekopon.display.entity.KDataEntity;
import com.dekopon.display.service.KDataService;
import com.dekopon.common.pojo.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
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
@Slf4j
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
    @Autowired
    RedissonClient redisson;

    public RLock kDataQueryLock;

    @PostConstruct
    private void setKDataQueryLock() {
        kDataQueryLock = redisson.getLock("k_data_query_lock");
    }

    @Override
    public List<KDataEntity> getMonthPeriodDailyData(String code) {
        // 先查询redis
        String data = redisTemplate.opsForValue().get(RedisConfiguration.K_DATA_DAILY_PREFIX + code);
        // 有则直接返回
        if (StringUtils.hasText(data)) {
            List<KDataEntity> kDataEntities = gson.fromJson(data, new TypeToken<List<KDataEntity>>() {
            }.getType());
            try {
                if (kDataQueryLock.tryLock(-1, 30, TimeUnit.MINUTES)) {
                    checkExpiredAndNotifyPython(code, kDataEntities);
                }
            } catch (InterruptedException ignored) {
            }
            return kDataEntities;
        } else {
            // 没有获取到锁说明已经有线程去数据库查询了，先直接返回等待
            try {
                if (kDataQueryLock.tryLock(-1, 30, TimeUnit.MINUTES)) {
                    return queryDatabaseAndDecideIfUpdate(code);
                }
            } catch (InterruptedException ignore) {
            }
            // TODO: 附加预测数据
            return null;
        }
    }

    private List<KDataEntity> queryDatabaseAndDecideIfUpdate(String code) {
        // 没有再查询数据库 30天或者当天数据
        List<KDataEntity> kDataEntities =
                kDataMapper.selectList(new LambdaQueryWrapper<KDataEntity>().eq(KDataEntity::getCode, code).eq(KDataEntity::getDaily, KDataEntity.Const.DAILY_DAILY).orderByDesc(KDataEntity::getTime).last("limit 30"));
        Collections.reverse(kDataEntities);
        // 如果数据库没有数据，需要让python去查询
        if (kDataEntities.isEmpty()) {
            notifyPythonByRabbitAsync(code, 0);
        } else { // 数据库有值
            long current = System.currentTimeMillis();
            redisTemplate.opsForValue().set(RedisConfiguration.K_DATA_DAILY_PREFIX + code,
                    gson.toJson(kDataEntities), nextDayMillis(current) - current, TimeUnit.MILLISECONDS);
            // 数据过期了
            checkExpiredAndNotifyPython(code, kDataEntities);
        }
        return kDataEntities;
    }

    private void checkExpiredAndNotifyPython(String code, List<KDataEntity> kDataEntities) {
        long stamp = kDataEntities.get(kDataEntities.size() - 1).getTime().getTime();
        if (nextDayMillis(stamp) < System.currentTimeMillis()) {
            notifyPythonByRabbitAsync(code, stamp / 1000);
        } else {
            kDataQueryLock.forceUnlock();
        }
    }

    private void notifyPythonByRabbitAsync(String code, long last) {
        pool.execute(() -> {
            @AllArgsConstructor
            @Data
            class TempTO {
                String code;
                long last;
                int daily;
            }
            // python去查询所有数据
            rabbitTemplate.convertAndSend("", RabbitConfiguration.K_DATA_QUERY_QUEUE_NAME,
                    R.ok().setData(new TempTO(code, last, 1)));
        });
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
