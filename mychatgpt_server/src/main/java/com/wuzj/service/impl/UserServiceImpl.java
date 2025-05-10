package com.wuzj.service.impl;

import com.wuzj.entity.User;
import com.wuzj.mapper.UserMapper;
import com.wuzj.service.UserService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author wuzj
 * @date 2023/8/29
 */

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    @Cacheable(key = "#username", value = "user",unless = "#result eq null")
    public User queryByName(String username) {
        return userMapper.queryByName(username);
    }
}