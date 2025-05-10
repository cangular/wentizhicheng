package com.wuzj.mapper;

import com.wuzj.entity.UserLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author wuzj
 * @date 2023/8/29
 */
@Mapper
public interface UserLogMapper {
    int insert(UserLog userLog);
}
