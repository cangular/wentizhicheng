package com.wuzj.vo.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wuzj
 * @date 2023/8/29
 */
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class ChatResponseParameter {
//   private String id;
//   private String object;
//   private String created;
//   private String model;
//
//   private List<Choice> choices;
//}

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponseParameter {
   private String id;
   private String object;
   private String created;
   private String sentence_id;
   private String is_end;
   private String is_truncated;
   private String result;
   private String need_clear_history;
   private String finish_reason;
   private Usage usage;
}
