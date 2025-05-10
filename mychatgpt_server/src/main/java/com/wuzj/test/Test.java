package com.wuzj.test;

import com.wuzj.App;
import com.wuzj.model.ChatModel;
import com.wuzj.vo.chat.ChatRequestParameter;

import java.io.*;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Scanner;

/**
 * @author wuzj
 * @date 2023/8/29
 */
public class Test {

    public static void main(String[] args) throws InterruptedException, IOException {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(App.class, args);
        ChatModel chatModel = applicationContext.getBean("chatModel", ChatModel.class);
        ChatRequestParameter chatRequestParameter = new ChatRequestParameter();
        System.out.println("\n\n\n\n");

        while (true) {
            Thread.sleep(1000);
            System.out.print("请输入问题(q退出)：");
            String question = new Scanner(System.in).nextLine();
            if ("q".equals(question.trim())) break;
            chatModel.getAnswer(System.out::print, chatRequestParameter, question);
        }

        applicationContext.close();
    }
}


//public class Test {
//
//
//    public static void main(String[] args) throws InterruptedException, IOException {
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        String a = "data: {\"id\":\"as-qe8ynqt15x\",\"object\":\"chat.completion\",\"created\":1693902980,\"sentence_id\":1,\"is_end\":true,\"is_truncated\":false,\"result\":\"09-05 16:36:19\",\"need_clear_history\":false,\"usage\":{\"prompt_tokens\":4,\"completion_tokens\":6,\"total_tokens\":11}}\n";
//        DataParameter dataParameter = null;
//        ChatResponseParameter responseParameter = null;
//        try {
//            dataParameter =  objectMapper.readValue(a, DataParameter.class);
//            responseParameter =  dataParameter.getData();
//            // 处理结果
//            String content = responseParameter.getResult();
//            System.out.println(content);
//        } catch (JsonProcessingException e) {
//            System.out.println("转换异常，{} 不能被转换为json");
//        }
//        System.out.println(a);
//
//
//    }
//}