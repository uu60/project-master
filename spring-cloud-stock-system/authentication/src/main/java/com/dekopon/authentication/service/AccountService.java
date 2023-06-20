package com.dekopon.authentication.service;

import com.dekopon.authentication.controller.AccountController;
import com.dekopon.authentication.entity.AccountEntity;
import com.dekopon.authentication.vo.UsernamePasswordVO;

/**
 * @author dekopon
 * @since 2023/6/18 18:18
 */
public interface AccountService {
    AccountEntity getAccountEntity(String username);

    boolean addAccount(UsernamePasswordVO vo);
}
