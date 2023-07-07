package com.dekopon.prediction.service;

import com.dekopon.prediction.entity.KDataPredictedEntity;
import com.dekopon.prediction.pojo.GetUpProbabilityVO;

import java.util.List;

public interface PredictionService {


    Double computeAndGetScore(String code, String fromDate, String toDate);

    List<GetUpProbabilityVO> getUpProbability(String code, String date);

    List<KDataPredictedEntity> getTrend(String code, String fromDate);
}
