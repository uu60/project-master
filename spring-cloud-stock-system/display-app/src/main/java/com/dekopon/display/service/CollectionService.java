package com.dekopon.display.service;

import com.dekopon.display.vo.collection.CollectionListItemVO;

import java.util.List;

/**
 * @author dekopon
 * @since 2023/7/2 11:35
 */
public interface CollectionService {
    void collect(String username, String code);

    void delete(String username, String code);

    List<CollectionListItemVO> list(String username);
}
