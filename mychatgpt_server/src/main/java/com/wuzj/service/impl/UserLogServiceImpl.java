package com.wuzj.service.impl;

import com.wuzj.entity.UserLog;
import com.wuzj.mapper.UserLogMapper;
import com.wuzj.service.UserLogService;
import org.springframework.cache.annotation.CachePut;
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
public class UserLogServiceImpl implements UserLogService {
    @Resource
    private UserLogMapper userLogMapper;

    @Override
    public boolean save(UserLog userLog) {
       return userLogMapper.insert(userLog) > 0;
    }
}
