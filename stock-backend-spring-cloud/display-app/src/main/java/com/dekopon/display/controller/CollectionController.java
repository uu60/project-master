package com.dekopon.display.controller;

import com.dekopon.common.exception.RException;
import com.dekopon.common.pojo.ObjR;
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
    public ObjR collect(@PathVariable String code) {
        String username = SecurityUtils.getCurrentUsername();
        try {
            collectionService.collect(username, code);
            return ObjR.ok();
        } catch (RException e) {
            return ObjR.e(e.getCode(), e.getMsg());
        }
    }

    @DeleteMapping("/collect/{code}")
    public ObjR deleteCollection(@PathVariable String code) {
        String username = SecurityUtils.getCurrentUsername();
        collectionService.delete(username, code);
        return ObjR.ok();
    }

    @GetMapping("/collect/list")
    public ObjR list() {
        String username = SecurityUtils.getCurrentUsername();
        List<CollectionListItemVO> vos = collectionService.list(username);
        return ObjR.ok().data(vos);
    }
}
