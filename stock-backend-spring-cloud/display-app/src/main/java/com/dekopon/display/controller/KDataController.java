package com.dekopon.display.controller;

import com.dekopon.common.exception.RException;
import com.dekopon.common.pojo.ObjR;
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


    @GetMapping("/data/update/{code}")
    public ObjR update(@PathVariable String code) {
        code = code.toUpperCase();
        KDataEntity latestData = kDataService.update(code);
        if (latestData == null) {
            return ObjR.e(ObjR.Codes.K_DATA_WAIT, "Please wait for update.");
        }
        return ObjR.ok();
    }

    @GetMapping("/data/daily/{code}")
    public ObjR getSpecificPeriodData(@PathVariable String code, @RequestParam String fromDate,
                                      @RequestParam String toDate) {
        code = code.toUpperCase();
        List<KDataEntity> dailyData;
        try {
            dailyData = kDataService.getSpecificPeriodDailyData(code, fromDate, toDate);
        } catch (RException e) {
            return ObjR.e(e.getCode(), e.getMsg());
        }
        if (dailyData == null || dailyData.isEmpty()) {
            return ObjR.e(ObjR.Codes.K_DATA_WAIT, "Please wait for update.");
        }
        return ObjR.ok().data(dailyData);
    }

    @GetMapping("/data/today/{code}")
    public ObjR getTodayData(@PathVariable String code) {
        return null;
    }
}
