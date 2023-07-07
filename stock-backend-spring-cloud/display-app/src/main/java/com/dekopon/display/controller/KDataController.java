package com.dekopon.display.controller;

import com.dekopon.display.entity.KDataEntity;
import com.dekopon.display.service.KDataService;
import com.dekopon.common.pojo.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


    @GetMapping("/data/daily/{code}")
    public R getMonthPeriodData(@PathVariable String code) {
        List<KDataEntity> dailyData = kDataService.getMonthPeriodDailyData(code);
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
