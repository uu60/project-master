package com.dekopon.prediction.controller;

import com.dekopon.common.exception.RException;
import com.dekopon.common.pojo.ObjR;
import com.dekopon.prediction.entity.KDataPredictedEntity;
import com.dekopon.prediction.pojo.GetUpProbabilityVO;
import com.dekopon.prediction.pojo.ScoreVO;
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
    public ObjR getModelScore(@PathVariable String code, @RequestParam("fromDate") String fromDate,
                              @RequestParam("toDate") String toDate) {
        try {
            ScoreVO score = predictionService.computeAndGetScore(code, fromDate, toDate);
            return ObjR.ok().data(score);
        } catch (RException e) {
            return ObjR.e(e.getCode(), e.getMsg());
        }
    }

    @GetMapping("/up/{code}")
    public ObjR getUpProbability(@PathVariable String code, @RequestParam String date) {
        try {
            List<GetUpProbabilityVO> upProbability = predictionService.getUpProbability(code, date);
            return ObjR.ok().data(upProbability);
        } catch (RException e) {
            return ObjR.e(e.getCode(), e.getMsg());
        }
    }

    @GetMapping("/trend/{code}")
    public ObjR getTrend(@PathVariable String code, @RequestParam String fromDate) {
        try {
            List<KDataPredictedEntity> entities = predictionService.getTrend(code, fromDate);
            return ObjR.ok().data(entities);
        } catch (RException e) {
            return ObjR.e(e.getCode(), e.getMsg());
        }
    }
}
