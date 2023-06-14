package com.dekopon.display.controller;

import com.dekopon.display.entity.KDataEntity;
import com.dekopon.display.service.KDataService;
import com.dekopon.pojo.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author dekopon
 * @since 2023/6/13 18:51
 */
@RestController
public class KDataController {

    @Autowired
    KDataService kDataService;


    @GetMapping("/data/{code}")
    public R getExistedData(@PathVariable String code) {

        List<KDataEntity> existedData = kDataService.getExistedData(code);
        if (existedData.isEmpty()) {
            return R.other(R.Codes.K_DATA_WAIT, "Please wait for update.");
        }
        return R.ok().setData(existedData);
    }
}
