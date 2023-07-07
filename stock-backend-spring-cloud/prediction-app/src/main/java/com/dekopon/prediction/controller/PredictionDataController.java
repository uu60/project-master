package com.dekopon.prediction.controller;

import com.dekopon.common.exception.RException;
import com.dekopon.common.pojo.R;
import com.dekopon.prediction.entity.KDataPredictedEntity;
import com.dekopon.prediction.pojo.GetUpProbabilityVO;
import com.dekopon.prediction.service.PredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class PredictionDataController {

    @Autowired
    PredictionService predictionService;

    @GetMapping("/score/{code}")
    public R getModelScore(@PathVariable String code, @RequestParam("fromDate") String fromDate,
                           @RequestParam("toDate") String toDate) {
        Double score = predictionService.computeAndGetScore(code, fromDate, toDate);
        return R.ok().data(score);
    }

    @GetMapping("/up/{code}")
    public R getUpProbability(@PathVariable String code, @RequestParam String date) {
        try {
            List<GetUpProbabilityVO> upProbability = predictionService.getUpProbability(code, date);
            return R.ok().data(upProbability);
        } catch (RException e) {
            return R.e(e.getCode(), e.getMsg());
        }
    }

    @GetMapping("/trend/{code}")
    public R getTrend(@PathVariable String code, @RequestParam String fromDate) {
        try {
            List<KDataPredictedEntity> entities = predictionService.getTrend(code, fromDate);
            return R.ok().data(entities);
        } catch (RException e) {
            return R.e(e.getCode(), e.getMsg());
        }
    }
}
