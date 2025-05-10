package com.wuzj.canal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SqlType {
    private Integer id;
    private String username;
    private String password;
    private LocalDateTime createTime;
}
