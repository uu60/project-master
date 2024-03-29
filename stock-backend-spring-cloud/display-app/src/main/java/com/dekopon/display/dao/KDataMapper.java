package com.dekopon.display.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dekopon.display.entity.KDataEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author dekopon
 * @since 2023/6/13 19:04
 */
@Mapper
public interface KDataMapper extends BaseMapper<KDataEntity> {

}
