package com.dekopon.authentication.controller;

import com.dekopon.authentication.entity.AccountEntity;
import com.dekopon.authentication.service.AccountService;
import com.dekopon.authentication.vo.UsernamePasswordVO;
import com.dekopon.common.pojo.ObjR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author dekopon
 * @since 2023/6/18 16:52
 */
@RestController
@RequestMapping("/api/v1")
public class AccountController {

    @Autowired
    AccountService accountService;

    @PostMapping("/register")
    public ObjR register(@RequestBody UsernamePasswordVO vo) {
        AccountEntity accountEntity = accountService.getAccountEntity(vo.getUsername());
        if (accountEntity != null) {
            return ObjR.e(ObjR.Codes.USERNAME_EXISTED, "Username already exists.");
        }
        try {
            boolean res = accountService.addAccount(vo);
            return res ? ObjR.ok() : ObjR.e(ObjR.Codes.USERNAME_OR_PASSWORD_WRONG_FORMAT, "Invalid username or password.");
        } catch (Exception e) {
            e.printStackTrace();
            return ObjR.e(ObjR.Codes.SERVER_INTERNAL_ERROR, "Server internal error.");
        }
    }
}
