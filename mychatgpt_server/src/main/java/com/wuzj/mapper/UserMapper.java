package com.wuzj.mapper;

import com.wuzj.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author wuzj
 * @date 2023/8/29
 */
@Mapper
public interface UserMapper {

    int insert(User user);

    User queryByName(String username);
}
