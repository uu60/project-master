package com.dekopon.display.rabbit;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dekopon.display.configuration.RabbitConfiguration;
import com.dekopon.display.dao.KDataMapper;
import com.dekopon.display.entity.KDataEntity;
import com.dekopon.pojo.R;
import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author dekopon
 * @since 2023/6/14 01:37
 */
@Component
public class KDataListener {

    @Autowired
    KDataMapper kDataMapper;
    @Autowired
    Gson gson;

    @RabbitListener(queues = RabbitConfiguration.K_DATA_RESULT_QUEUE_NAME)
    public void saveToDB(Message message,
                         R content,
                         Channel channel) {
        String code = content.getMsg();
        // 获取加载过的最新的记录
        KDataEntity kDataEntity = kDataMapper.selectOne(new LambdaQueryWrapper<KDataEntity>().eq(KDataEntity::getCode
                , code).orderByDesc(KDataEntity::getTime).last("limit 1"));

        List<KDataEntity> records = ((List<Map<String, Object>>) content.getData()).stream().map(map -> {
            KDataEntity entity = new KDataEntity();
            entity.setTime(transformDate((String) map.get("Time")));
            entity.setClose(BigDecimal.valueOf((Double) map.get("Close")));
            entity.setLow(BigDecimal.valueOf((Double) map.get("Low")));
            entity.setOpen(BigDecimal.valueOf((Double) map.get("Open")));
            entity.setHigh(BigDecimal.valueOf((Double) map.get("High")));
            entity.setCode(code);
            entity.setDividends(BigDecimal.valueOf((Double) map.get("Dividends")));
            entity.setVolume((int) Math.round((Double) map.get("Volume")));
            entity.setStockSplits(BigDecimal.valueOf((Double) map.get("Stock Splits")));
            entity.setHistorical(true);
            return entity;
        }).toList();

        if (kDataEntity != null) {
            records = records.stream().filter(record -> record.getTime().compareTo(kDataEntity.getTime()) > 0).toList();
        }
        records.forEach(e -> kDataMapper.insert(e));


        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException ignore) {
        }
    }

    private Date transformDate(String timeNY) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssxxx");
        OffsetDateTime offsetDateTime = OffsetDateTime.parse(timeNY, formatter);
        return Date.from(offsetDateTime.toInstant());
    }
}
