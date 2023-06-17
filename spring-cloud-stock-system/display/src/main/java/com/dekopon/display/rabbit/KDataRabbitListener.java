package com.dekopon.display.rabbit;

import com.dekopon.display.config.RabbitConfiguration;
import com.dekopon.display.config.RedisConfiguration;
import com.dekopon.display.dao.KDataMapper;
import com.dekopon.display.entity.KDataEntity;
import com.dekopon.display.service.KDataService;
import com.dekopon.display.service.impl.KDataServiceImpl;
import com.dekopon.pojo.R;
import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author dekopon
 * @since 2023/6/14 01:37
 */
@Component
public class KDataRabbitListener {

    @Autowired
    KDataMapper kDataMapper;
    @Autowired
    KDataService kDataService;
    @Autowired
    Gson gson;
    @Autowired
    StringRedisTemplate redisTemplate;

    @SuppressWarnings("all")
    @RabbitListener(queues = RabbitConfiguration.K_DATA_RESULT_QUEUE_NAME)
    public void saveToDB(Message message,
                         R r,
                         Channel channel) {
        try {
            Integer code = r.getCode();
            // 有新数据
            if (code == 0) {
                Map<String, Object> data = (Map<String, Object>) (r.getData());
                String dataCode = (String) data.get("code");
                Integer dataDaily = (Integer) data.get("daily");
                List<Map<String, Object>> dataData = (List<Map<String, Object>>) data.get("data");

                List<KDataEntity> records = dataData.stream().map(map -> {
                    KDataEntity entity = new KDataEntity();
                    entity.setTime(transformDate((String) map.get("Time")));
                    entity.setClose(new BigDecimal((String) map.get("Close")));
                    entity.setLow(new BigDecimal((String) map.get("Low")));
                    entity.setOpen(new BigDecimal((String) map.get("Open")));
                    entity.setHigh(new BigDecimal((String) map.get("High")));
                    entity.setDaily(dataDaily);
                    entity.setCode(dataCode);
                    entity.setDividends(new BigDecimal((String) map.get("Dividends")));
                    Object volume = map.get("Volume");
                    volume = volume instanceof Integer ? ((Integer) volume).longValue() : volume;
                    entity.setVolume((Long) volume);
                    entity.setStockSplits(new BigDecimal((String) map.get("Stock Splits")));

                    return entity;
                }).toList();

                records.forEach(e -> kDataMapper.insert(e));
                redisTemplate.opsForValue().getAndDelete(RedisConfiguration.K_DATA_DAILY_PREFIX + dataCode);
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            }
        } catch (Exception noack) {
            noack.printStackTrace();
            while (true) {
                try {
                    channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
                    break;
                } catch (Exception ignoreAndRetry) {

                }
            }
        } finally {
            ((KDataServiceImpl) kDataService).kDataQueryLock.forceUnlock();
        }
    }

    private Date transformDate(String timeNY) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssxxx");
        OffsetDateTime offsetDateTime = OffsetDateTime.parse(timeNY, formatter);
        return Date.from(offsetDateTime.toInstant());
    }
}
