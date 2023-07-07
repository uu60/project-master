package com.dekopon.prediction.pojo;

import lombok.AllArgsConstructor;

import java.util.Date;

/**
 * @author dekopon
 * @since 2023/7/8 16:01
 */
@AllArgsConstructor
public class GetUpProbabilityVO {
    String code;
    String field;
    Double up;
    Date time;
}
