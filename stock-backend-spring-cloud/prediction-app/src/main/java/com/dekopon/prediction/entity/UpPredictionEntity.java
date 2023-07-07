package com.dekopon.prediction.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@TableName("t_up_prediction")
@Data
public class UpPredictionEntity {
    @TableId(type = IdType.AUTO)
    Integer id;
    String code;
    String field;
    Double up;
    Date time;
    @Deprecated
    Integer correct;
}
