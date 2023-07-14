package com.dekopon.prediction.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dekopon.common.exception.RException;
import com.dekopon.common.pojo.R;
import com.dekopon.common.pojo.ObjR;
import com.dekopon.prediction.dao.KDataPredictedMapper;
import com.dekopon.prediction.dao.UpPredictionMapper;
import com.dekopon.prediction.entity.KDataPredictedEntity;
import com.dekopon.prediction.entity.UpPredictionEntity;
import com.dekopon.prediction.feign.DisplayFeignClient;
import com.dekopon.prediction.pojo.GetUpProbabilityVO;
import com.dekopon.prediction.pojo.KDataEntityVO;
import com.dekopon.prediction.pojo.ScoreVO;
import com.dekopon.prediction.pojo.UpPredictionIntegrityVO;
import com.dekopon.prediction.service.PredictionService;
import com.dekopon.prediction.utils.DateUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class PredictionServiceImpl implements PredictionService {

    @Autowired
    UpPredictionMapper upPredictionMapper;
    @Autowired
    KDataPredictedMapper kDataPredictedMapper;
    @Autowired
    DisplayFeignClient displayFeignClient;
    @Autowired
    ThreadPoolExecutor pool;
    @Autowired
    Gson gson;
    @Resource(name = "isoSdf")
    SimpleDateFormat isoSdf;

    @Override
    public synchronized ScoreVO computeAndGetScore(String code, String fromDate, String toDate) {
        code = code.toUpperCase();
        if (!StringUtils.hasText(fromDate) || !StringUtils.hasText(toDate)) {
            throw new RException(4, "Time period must be explicit.");
        }
        Date from = getDateOrException(fromDate);
        Date to = getDateOrException(toDate);
        if (to.getTime() - from.getTime() > 365L * 24 * 60 * 60 * 1000) {
            throw new RException(2, "Max period is 30 days.");
        }

        Date yesterdayOfFromDate;
        try {
             yesterdayOfFromDate = new Date(isoSdf.parse(fromDate).getTime() - 24 * 60 * 60 * 1000);
        } catch (ParseException e) {
            throw new RException(ObjR.Codes.DATE_WRONG_FORMAT, "Wrong date format.");
        }

        String finalCode = code;
        Future<String> historyFuture = pool.submit(() -> displayFeignClient.getSpecificPeriodData(finalCode,
                isoSdf.format(yesterdayOfFromDate),
                toDate));
        Future<List<UpPredictionEntity>> predictedFuture = pool.submit(() ->
                upPredictionMapper.selectList(new LambdaQueryWrapper<UpPredictionEntity>().eq(UpPredictionEntity::getCode, finalCode).apply("date(time) >= date({0})", fromDate).apply("date(time) <= date({0})", toDate))
        );
        try {
            String historyJson = historyFuture.get();
            R<List<KDataEntityVO>> temp = gson.fromJson(historyJson,
                    new TypeToken<R<List<KDataEntityVO>>>() {}.getType());
            List<KDataEntityVO> historyEntities = temp.data;
            List<UpPredictionEntity> upPredictionEntities = predictedFuture.get();

            return doComputeScore(historyEntities, upPredictionEntities);
        } catch (Exception e) {
            throw new RException(3, "Internal error: " + e.getMessage());
        }
    }

    private ScoreVO doComputeScore(List<KDataEntityVO> history, List<UpPredictionEntity> prediction) {
        List<UpPredictionIntegrityVO> upPredictionIntegrityVOs = processUpPredictionEntityToIntegrityMap(prediction);
        Map<String, Integer> correctNumMap = new HashMap<>();
        String[] fields = {"open", "high", "low", "close", "volume"};
        for (String field : fields) {
            correctNumMap.put(field, 0);
        }
        int total = 0;

        int i = 0, j = 0;
        while (i < history.size() && j < upPredictionIntegrityVOs.size()) {
            KDataEntityVO kDataEntity = history.get(i);
            UpPredictionIntegrityVO vo = upPredictionIntegrityVOs.get(j);
            int compare = kDataEntity.getTime().compareTo(vo.getTime());
            if (compare < 0) {
                // 历史数据小
                i++;
                continue;
            } else if (compare > 0) {
                j++;
                continue;
            }
            if (i > 0) {
                KDataEntityVO prevKDataEntity = history.get(i - 1);
                // 匹配上，检查模型预测是否正确
                boolean openDidUp = kDataEntity.getOpen().compareTo(prevKDataEntity.getOpen()) > 0;
                boolean highDidUp = kDataEntity.getHigh().compareTo(prevKDataEntity.getHigh()) > 0;
                boolean lowDidUp = kDataEntity.getLow().compareTo(prevKDataEntity.getLow()) > 0;
                boolean closeDidUp = kDataEntity.getClose().compareTo(prevKDataEntity.getClose()) > 0;
                boolean volumeDidUp = kDataEntity.getVolume().compareTo(prevKDataEntity.getVolume()) > 0;
                if (openDidUp == vo.getOpen()) {
                    correctNumMap.put("open", correctNumMap.get("open") + 1);
                }
                if (highDidUp == vo.getHigh()) {
                    correctNumMap.put("high", correctNumMap.get("high") + 1);
                }
                if (lowDidUp == vo.getLow()) {
                    correctNumMap.put("low", correctNumMap.get("low") + 1);
                }
                if (closeDidUp == vo.getClose()) {
                    correctNumMap.put("close", correctNumMap.get("close") + 1);
                }
                if (volumeDidUp == vo.getVolume()) {
                    correctNumMap.put("volume", correctNumMap.get("volume") + 1);
                }
                total++;
            }
            i++;
            j++;
        }

        ScoreVO scoreVO = new ScoreVO();
        scoreVO.setOpen((double) correctNumMap.get("open") / total);
        scoreVO.setHigh((double) correctNumMap.get("high") / total);
        scoreVO.setLow((double) correctNumMap.get("low") / total);
        scoreVO.setClose((double) correctNumMap.get("close") / total);
        scoreVO.setVolume((double) correctNumMap.get("volume") / total);
        return scoreVO;
    }

    private List<UpPredictionIntegrityVO> processUpPredictionEntityToIntegrityMap(List<UpPredictionEntity> prediction) {
        Map<Date, UpPredictionIntegrityVO> tempMap = new HashMap<>();
        prediction.forEach(up -> {
            Date date = up.getTime();
            if (!tempMap.containsKey(date)) {
                tempMap.put(date, new UpPredictionIntegrityVO());
            }
            UpPredictionIntegrityVO upPredictionIntegrityVO = tempMap.get(date);
            String field = up.getField();
            upPredictionIntegrityVO.setTime(up.getTime());
            boolean moreProbToUp = up.getUp() >= 0.5;
            switch (field) {
                case "open" -> upPredictionIntegrityVO.setOpen(moreProbToUp);
                case "high" -> upPredictionIntegrityVO.setHigh(moreProbToUp);
                case "low" -> upPredictionIntegrityVO.setLow(moreProbToUp);
                case "close" -> upPredictionIntegrityVO.setClose(moreProbToUp);
                case "volume" -> upPredictionIntegrityVO.setVolume(moreProbToUp);
            }
        });
        return tempMap.values().stream().sorted().toList();
    }

    private Date getDateOrException(String dateStr) {
        if (!StringUtils.hasText(dateStr) || (!DateUtils.isISOOffsetDateTimeFormat(dateStr) && !DateUtils.isISOInstantFormat(dateStr))) {
            throw new RException(ObjR.Codes.DATE_WRONG_FORMAT, "Wrong date format.");
        }
        try {
            return isoSdf.parse(dateStr);
        } catch (ParseException e) {
            throw new RException(ObjR.Codes.DATE_WRONG_FORMAT, "Wrong date format.");
        }
    }

    @Override
//    @Cacheable(value = "getUpProbability", sync = true)
    public List<GetUpProbabilityVO> getUpProbability(String code, String date) {
        if (!StringUtils.hasText(date) || (!DateUtils.isISOOffsetDateTimeFormat(date) && !DateUtils.isISOInstantFormat(date))) {
            throw new RException(ObjR.Codes.DATE_WRONG_FORMAT, "Wrong date format.");
        }
        code = code.toUpperCase();
        List<UpPredictionEntity> entities =
                upPredictionMapper.selectList(new LambdaQueryWrapper<UpPredictionEntity>().eq(UpPredictionEntity::getCode, code).apply("date({0}) = date(time)", date));
        return entities.stream().map(entity -> new GetUpProbabilityVO(entity.getCode(), entity.getField(),
                entity.getUp(), entity.getTime())).toList();
    }

    @Override
    public List<KDataPredictedEntity> getTrend(String code, String fromDate) {
        if (!DateUtils.isISOOffsetDateTimeFormat(fromDate) && !DateUtils.isISOInstantFormat(fromDate)) {
            throw new RException(ObjR.Codes.DATE_WRONG_FORMAT, "Wrong date format.");
        }
        code = code.toUpperCase();
        return kDataPredictedMapper.selectList(new LambdaQueryWrapper<KDataPredictedEntity>().eq(KDataPredictedEntity::getCode, code).apply("date(time) >= date({0})", fromDate));
    }
}
