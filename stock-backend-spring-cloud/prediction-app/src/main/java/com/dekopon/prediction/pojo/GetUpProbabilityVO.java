package com.dekopon.prediction.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author dekopon
 * @since 2023/7/8 16:01
 */
@AllArgsConstructor
@Data
public class GetUpProbabilityVO implements Serializable {
    private String code;
    private String field;
    private Double up;
    private Date time;
}
