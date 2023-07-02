package com.dekopon.display.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dekopon.common.exception.RException;
import com.dekopon.display.config.RedisConfiguration;
import com.dekopon.display.dao.CollectionMapper;
import com.dekopon.display.dao.KDataMapper;
import com.dekopon.display.entity.CollectionEntity;
import com.dekopon.display.entity.KDataEntity;
import com.dekopon.display.service.CollectionService;
import com.dekopon.display.vo.collection.CollectionListItemVO;
import com.google.gson.Gson;
import jakarta.annotation.PostConstruct;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * @author dekopon
 * @since 2023/7/2 11:38
 */
@Service
public class CollectionServiceImpl implements CollectionService {

    @Autowired
    CollectionMapper collectionMapper;
    @Autowired
    KDataMapper kDataMapper;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    Gson gson;
    @Autowired
    RedissonClient redissonClient;

    @Override
    public void collect(String username, String code) {
        // 查询是否已经添加过
        CollectionEntity collectionEntity =
                collectionMapper.selectOne(new LambdaQueryWrapper<CollectionEntity>().eq(CollectionEntity::getUsername, username).eq(CollectionEntity::getStockCode, code));
        if (collectionEntity != null) {
            throw new RException(1, "已经收藏过了。");
        }
        // 未添加过查询是否查询过
        List<KDataEntity> kDataEntities =
                kDataMapper.selectList(new LambdaQueryWrapper<KDataEntity>().eq(KDataEntity::getCode, code).last(
                        "limit 1"));
        if (kDataEntities == null || kDataEntities.isEmpty()) {
            throw new RException(2, "还没有查询过或者股票不存在，先去查询。");
        }
        // 添加
        CollectionEntity collectionEntity1 = new CollectionEntity();
        collectionEntity1.setTime(new Date());
        collectionEntity1.setUsername(username);
        collectionEntity1.setStockCode(code);
        collectionMapper.insert(collectionEntity1);
    }

    @Override
    public void delete(String username, String code) {
        collectionMapper.delete(new LambdaQueryWrapper<CollectionEntity>().eq(CollectionEntity::getUsername,
                username).eq(CollectionEntity::getStockCode, code));
    }

    @Override
    public List<CollectionListItemVO> list(String username) {
        List<CollectionListItemVO> vos =
                collectionMapper.selectList(new LambdaQueryWrapper<CollectionEntity>().eq(CollectionEntity::getUsername, username)).stream().sorted((o1, o2) -> o2.getTime().compareTo(o1.getTime())).map(entity -> {
                    CollectionListItemVO collectionListItemVO = new CollectionListItemVO();
                    collectionListItemVO.setCode(entity.getStockCode());
                    collectionListItemVO.setCollectTime(entity.getTime());
                    return collectionListItemVO;
                }).toList();

        // 收集code字符串集合
        List<String> codes = vos.stream().map(CollectionListItemVO::getCode).toList();

        // 构建code - price
        Map<String, KDataEntity> latestClosePriceMap = new HashMap<>();
        codes.forEach(code -> {
            KDataEntity kDataEntity;
            for (;;) {
                String latestEntityStr =
                        stringRedisTemplate.opsForValue().get(RedisConfiguration.K_DATA_LATEST_PREFIX + code);
                if (StringUtils.hasText(latestEntityStr)) {
                    kDataEntity = gson.fromJson(latestEntityStr, KDataEntity.class);
                    break;
                } else {
                    RLock latestKDataQueryLock = redissonClient.getLock("latest_k_data_query_lock_" + code);
                    // redis中没有需要去查库
                    // 加锁查库
                    try {
                        if (!latestKDataQueryLock.tryLock(-1, 30, TimeUnit.MINUTES)) {
                            // 获取锁失败，已经有查库线程
                            continue;
                        }
                    } catch (InterruptedException ignore) {

                    }
                    // 获取到锁了
                    try {
                        kDataEntity =
                                kDataMapper.selectOne(new LambdaQueryWrapper<KDataEntity>().eq(KDataEntity::getCode, code).orderByDesc(KDataEntity::getTime).last("limit 1"));
                        if (kDataEntity != null) {
                            latestClosePriceMap.put(code, kDataEntity);
                            stringRedisTemplate.opsForValue().set(RedisConfiguration.K_DATA_LATEST_PREFIX + code, gson.toJson(kDataEntity));
                        }
                        break;
                    } finally {
                        latestKDataQueryLock.unlock();
                    }
                }
            }
            latestClosePriceMap.put(code, kDataEntity);
        });

        // 整合到vos
        vos.forEach(vo -> {
            KDataEntity entity = latestClosePriceMap.get(vo.getCode());
            vo.setOpenPrice(entity.getOpen());
            vo.setClosePrice(entity.getClose());
            vo.setDataTime(entity.getTime());
        });

        return vos;
    }
}
