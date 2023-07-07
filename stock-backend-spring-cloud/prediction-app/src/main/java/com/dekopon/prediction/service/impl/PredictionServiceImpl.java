package com.dekopon.prediction.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dekopon.common.exception.RException;
import com.dekopon.common.pojo.R;
import com.dekopon.prediction.dao.KDataPredictedMapper;
import com.dekopon.prediction.dao.UpPredictionMapper;
import com.dekopon.prediction.entity.KDataPredictedEntity;
import com.dekopon.prediction.entity.UpPredictionEntity;
import com.dekopon.prediction.pojo.GetUpProbabilityVO;
import com.dekopon.prediction.service.PredictionService;
import com.dekopon.prediction.utils.DateUtils;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class PredictionServiceImpl implements PredictionService {

    @Autowired
    UpPredictionMapper upPredictionMapper;
    @Autowired
    KDataPredictedMapper kDataPredictedMapper;

    @Override
    public Double computeAndGetScore(String code, String fromDate, String toDate) {

        return null;
    }

    @Override
    @Cacheable(value = "getUpProbability", sync = true)
    public List<GetUpProbabilityVO> getUpProbability(String code, String date) {
        if (!DateUtils.isISOOffsetDateTimeFormat(date) && !DateUtils.isISOInstantFormat(date)) {
            throw new RException(R.Codes.DATE_WRONG_FORMAT, "Wrong date format.");
        }
        code = code.toUpperCase();
        List<UpPredictionEntity> entities = upPredictionMapper.selectList(new LambdaQueryWrapper<UpPredictionEntity>().eq(UpPredictionEntity::getCode, code).apply("date({0}}) = date(time)", date));
        return entities.stream().map(entity -> new GetUpProbabilityVO(entity.getCode(), entity.getField(), entity.getUp(), entity.getTime())).toList();
    }

    @Override
    public List<KDataPredictedEntity> getTrend(String code, String fromDate) {
        if (!DateUtils.isISOOffsetDateTimeFormat(fromDate) && !DateUtils.isISOInstantFormat(fromDate)) {
            throw new RException(R.Codes.DATE_WRONG_FORMAT, "Wrong date format.");
        }
        code = code.toUpperCase();
        return kDataPredictedMapper.selectList(new LambdaQueryWrapper<KDataPredictedEntity>().eq(KDataPredictedEntity::getCode, code).apply("date(time) >= date({0})", fromDate));
    }
}
