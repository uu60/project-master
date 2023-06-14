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
    @TableId(type = IdType.AUTO)
    Integer id;
    String code;
    Date time;
    BigDecimal open;
    BigDecimal high;
    BigDecimal low;
    BigDecimal close;
    Integer volume;
    BigDecimal dividends;
    @TableField("stock_splits")
    BigDecimal stockSplits;
    Boolean historical;
}
