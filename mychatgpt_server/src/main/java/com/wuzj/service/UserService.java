package com.wuzj.service;

import com.wuzj.entity.User;

/**
 * @author wuzj
 * @date 2023/8/29
 */
public interface UserService{


    User queryByName(String username);
}
