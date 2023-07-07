package com.dekopon.prediction.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author dekopon
 * @since 2023/7/8 16:22
 */
@TableName("t_k_data_predicted")
@Data
public class KDataPredictedEntity {
    @TableId(type = IdType.AUTO)
    Integer id;
    String code;
    Date time;
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
