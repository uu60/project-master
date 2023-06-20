package com.dekopon.authentication.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;

/**
 * @author dekopon
 * @since 2023/6/18 01:57
 */
@Data
@TableName("t_account")
public class AccountEntity {
    @TableId(type = IdType.AUTO)
    protected Integer id;
    protected String username;
    protected String password;
    protected Integer expired;
    protected Integer locked;
    @TableField("credentials_expired")
    protected Integer credentialsExpired;
    protected Integer enabled;
    @TableField("created_time")
    protected Date createdTime;
}
