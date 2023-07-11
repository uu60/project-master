package com.dekopon.prediction.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class UpPredictionIntegrityVO {
    private Boolean open;
    private Boolean high;
    private Boolean low;
    private Boolean close;
    private Boolean volume;
    private Date time;
}
