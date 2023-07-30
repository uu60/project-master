package com.dekopon.prediction.feign;

import com.dekopon.common.pojo.ObjR;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @author dekopon
 * @since 2023/7/10 23:17
 */
@FeignClient("display")
public interface DisplayFeignClient {
    @GetMapping(value = "/api/v1/data/daily/{code}", headers = {
            "Authorization=Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJkanoiLCJhdXRob3JpdGllcyI6W10sImlhdCI6MTY4NzEwNzIzNCwiZXhwIjoxNjkyMzc0NDAwfQ.LrBS9y9i4JeyTMQLRmfgqbJNuOGh04UtGGXm_e0UGoI"
    })
    String getSpecificPeriodData(
            @PathVariable String code,
            @RequestParam String fromDate,
            @RequestParam String toDate
    );

    @GetMapping("/api/v1/data/update/{code}")
    ObjR update(@PathVariable String code);
}
