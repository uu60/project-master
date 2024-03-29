package com.dekopon.display.service;

import com.dekopon.display.entity.KDataEntity;

import java.util.List;

/**
 * @author dekopon
 * @since 2023/6/13 19:14
 */
public interface KDataService {
    KDataEntity update(String code);

    KDataEntity getLatestKData(String code);

    List<KDataEntity> getSpecificPeriodDailyData(String code, String fromDate, String toDate);
}
