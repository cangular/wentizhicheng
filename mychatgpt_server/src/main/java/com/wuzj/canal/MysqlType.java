package com.wuzj.canal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class MysqlType {
    private Integer id;
    private String username;
    private String password;
    private LocalDateTime createTime;
}
