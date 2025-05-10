package com.wuzj.vo.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wuzj
 * @date 2023/8/29
 */


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usage {
   private Integer prompt_tokens;
   private Integer completion_tokens;
   private Integer total_tokens;
}
