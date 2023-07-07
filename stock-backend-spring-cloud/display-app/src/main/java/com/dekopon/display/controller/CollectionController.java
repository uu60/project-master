package com.dekopon.display.controller;

import com.dekopon.common.exception.RException;
import com.dekopon.common.pojo.R;
import com.dekopon.display.service.CollectionService;
import com.dekopon.display.utils.SecurityUtils;
import com.dekopon.display.vo.collection.CollectionListItemVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @author dekopon
 * @since 2023/7/2 11:14
 */
@RestController
@RequestMapping("/api/v1")
public class CollectionController {

    @Autowired
    CollectionService collectionService;

    @PostMapping("/collect/{code}")
    public R collect(@PathVariable String code) {
        String username = SecurityUtils.getCurrentUsername();
        try {
            collectionService.collect(username, code);
            return R.ok();
        } catch (RException e) {
            return R.e(e.getCode(), e.getMsg());
        }
    }

    @DeleteMapping("/collect/{code}")
    public R deleteCollection(@PathVariable String code) {
        String username = SecurityUtils.getCurrentUsername();
        collectionService.delete(username, code);
        return R.ok();
    }

    @GetMapping("/collect/list")
    public R list() {
        String username = SecurityUtils.getCurrentUsername();
        List<CollectionListItemVO> vos = collectionService.list(username);
        return R.ok().data(vos);
    }
}
