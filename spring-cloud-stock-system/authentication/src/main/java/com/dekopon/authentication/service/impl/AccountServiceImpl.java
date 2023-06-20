package com.dekopon.authentication.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dekopon.authentication.entity.AccountEntity;
import com.dekopon.authentication.mapper.AccountMapper;
import com.dekopon.authentication.service.AccountService;
import com.dekopon.authentication.vo.UsernamePasswordVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author dekopon
 * @since 2023/6/18 18:19
 */
@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    AccountMapper accountMapper;
    @Autowired
    PasswordEncoder encoder;

    @Override
    public AccountEntity getAccountEntity(String username) {
        return accountMapper.selectOne(new LambdaQueryWrapper<AccountEntity>().eq(AccountEntity::getUsername,
                username));
    }

    @Override
    public boolean addAccount(UsernamePasswordVO vo) {
        if (StringUtils.hasText(vo.getUsername()) && StringUtils.hasText(vo.getPassword()) && vo.getPassword().length() >= 6) {
            AccountEntity accountEntity = new AccountEntity();
            accountEntity.setUsername(vo.getUsername());
            accountEntity.setPassword(encoder.encode(vo.getPassword()));
            accountMapper.insert(accountEntity);
            return true;
        }
        return false;
    }
}
