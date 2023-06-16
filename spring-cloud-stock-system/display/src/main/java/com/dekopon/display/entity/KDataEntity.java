package com.dekopon.display.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author dekopon
 * @since 2023/6/13 19:05
 */
@Data
@TableName("k_data")
public class KDataEntity {

    public static class Const {
        public static final Integer DAILY_DAILY = 1;
        public static final Integer DAILY_TODAY = 0;
    }

    @TableId(type = IdType.AUTO)
    Integer id;
    String code;
    Date time;
    Integer historical;
    Integer daily;
    BigDecimal open;
    BigDecimal high;
    BigDecimal low;
    BigDecimal close;
    Long volume;
    BigDecimal dividends;
    @TableField("stock_splits")
    BigDecimal stockSplits;
}
