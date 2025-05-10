package com.wuzj.vo.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wuzj
 * @date 2023/8/29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequestParameter {

    private List<ChatMessage> messages = new ArrayList<>();

    private boolean stream = true;

     public void addMessages(ChatMessage message) {
        this.messages.add(message);
    }
}