package com.dekopon.display.vo.collection;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author dekopon
 * @since 2023/7/2 14:39
 */
@Data
public class CollectionListItemVO {
    String code;
    BigDecimal closePrice;
    BigDecimal openPrice;
    Date collectTime;
    Date dataTime;
}
