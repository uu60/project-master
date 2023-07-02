package com.dekopon.display.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author dekopon
 * @since 2023/7/2 11:33
 */
@TableName("t_collection")
@Data
public class CollectionEntity {
    @TableId(type = IdType.AUTO)
    Integer id;
    String username;
    @TableField("stock_code")
    String stockCode;
    Date time;
}
