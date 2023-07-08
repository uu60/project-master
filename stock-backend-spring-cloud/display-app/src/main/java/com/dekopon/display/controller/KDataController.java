package com.dekopon.display.controller;

import com.dekopon.common.exception.RException;
import com.dekopon.common.pojo.R;
import com.dekopon.display.entity.KDataEntity;
import com.dekopon.display.service.KDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author dekopon
 * @since 2023/6/13 18:51
 */
@RestController
@RequestMapping("/api/v1")
public class KDataController {

    @Autowired
    KDataService kDataService;


//    @GetMapping("/data/daily/{code}")
//    public R getMonthPeriodData(@PathVariable String code) {
//        List<KDataEntity> dailyData = kDataService.getMonthPeriodDailyData(code);
//        if (dailyData == null || dailyData.isEmpty()) {
//            return R.e(R.Codes.K_DATA_WAIT, "Please wait for update.");
//        }
//        return R.ok().data(dailyData);
//    }

    @GetMapping("/data/daily/{code}")
    public R getSpecificPeriodData(@PathVariable String code, @RequestParam String fromDate,
                                   @RequestParam String toDate) {
        List<KDataEntity> dailyData;
        try {
            dailyData = kDataService.getSpecificPeriodDailyData(code, fromDate, toDate);
        } catch (RException e) {
            return R.e(e.getCode(), e.getMsg());
        }
        if (dailyData == null || dailyData.isEmpty()) {
            return R.e(R.Codes.K_DATA_WAIT, "Please wait for update.");
        }
        return R.ok().data(dailyData);
    }

    @GetMapping("/data/today/{code}")
    public R getTodayData(@PathVariable String code) {
        return null;
    }
}
