package com.dekopon.display.service;

import com.dekopon.display.entity.KDataEntity;

import java.util.List;

/**
 * @author dekopon
 * @since 2023/6/13 19:14
 */
public interface KDataService {
    List<KDataEntity> getExistedData(String code);
}