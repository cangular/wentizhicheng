package com.wuzj.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wuzj.vo.chat.ChatMessage;
import com.wuzj.vo.chat.ChatRequestParameter;
import com.wuzj.vo.chat.ChatResponseParameter;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.async.methods.AbstractCharResponseConsumer;
import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.nio.support.AsyncRequestBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

import org.json.JSONObject;
import okhttp3.*;

/**
 * @author wuzj
 * @date 2023/8/29
 */

@Component
@Slf4j
public class ChatModel {


    private final Charset charset = StandardCharsets.UTF_8;

    @Value("${gpt.model.api-key}")
    private String API_KEY;

    @Value("${gpt.model.secret-key}")
    private String SECRET_KEY;

    static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().build();
    @Resource(name = "httpAsyncClient")
    private CloseableHttpAsyncClient asyncClient;

    @Resource
    private ObjectMapper objectMapper;

    public ChatModel() throws IOException {
    }

    /**
     * 该方法会异步请求chatGpt的接口，返回答案
     *
     * @param resConsumer                 函数式接口，处理每次返回的结果
     * @param chatGptRequestParameter 请求参数
     * @param question                问题
     * @return 返回chatGpt给出的答案
     */
    public String getAnswer(Consumer<String> resConsumer,ChatRequestParameter chatGptRequestParameter, String question) throws IOException {
        asyncClient.start();

        // 创建一个post请求
        AsyncRequestBuilder asyncRequest = AsyncRequestBuilder.post();

        // 设置请求参数
        chatGptRequestParameter.addMessages(new ChatMessage("user", question));

        // 请求的参数转换为字符串
        String valueAsString = null;
        try {
            valueAsString = objectMapper.writeValueAsString(chatGptRequestParameter);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        // 设置编码和请求参数
        ContentType contentType = ContentType.create("text/plain", charset);
        asyncRequest.setEntity(valueAsString, contentType);
        asyncRequest.setCharset(charset);

        //设置网页
        asyncRequest.setUri("https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/completions?access_token="+getAccessToken());

        // 设置请求头
        asyncRequest.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        asyncRequest.addParameter("domain","aip.baidubce.com");

        // 下面就是生产者消费者模型
        CountDownLatch latch = new CountDownLatch(1);
        // 用于记录返回的答案
        StringBuilder sb = new StringBuilder();
        // 消费者
        AbstractCharResponseConsumer<HttpResponse> consumer = new AbstractCharResponseConsumer<HttpResponse>() {
            HttpResponse response;

            @Override
            protected void start(HttpResponse response, ContentType contentType) {
                setCharset(charset);
                this.response = response;
            }

            @Override
            protected int capacityIncrement() {
                return Integer.MAX_VALUE;
            }

            @Override
            protected void data(CharBuffer src, boolean endOfStream) {
                // 收到一个请求就进行处理
                String ss = src.toString();
                // 通过data:进行分割，如果不进行此步，可能返回的答案会少一些内容
                for (String s : ss.split("data:")) {
                    // 去除掉data:
                    if (s.startsWith("data:")) {
                        s = s.substring(5);
                    }
                    // 返回的数据可能是（DONE）
                    if (s.length() > 8 && !s.contains("[DONE]")) {
                        // 转换为对象
                        ChatResponseParameter responseParameter = null;
                        try {
                            responseParameter = objectMapper.readValue(s, ChatResponseParameter.class);
                            // 处理结果
                            String content = responseParameter.getResult();
                            // 保存结果
                            sb.append(content);
                            // 处理结果
                            resConsumer.accept(content);
                        } catch (JsonProcessingException e) {
                            log.warn("转换异常，{} 不能被转换为json", s.trim());
                        }

                    }
                }
            }

            @Override
            protected HttpResponse buildResult() throws IOException {
                return response;
            }

            @Override
            public void releaseResources() {
            }
        };

        // 执行请求
        asyncClient.execute(asyncRequest.build(), consumer, new FutureCallback<HttpResponse>() {

            @Override
            public void completed(HttpResponse response) {
                latch.countDown();
                chatGptRequestParameter.addMessages(new ChatMessage("assistant", sb.toString()));
                System.out.println(sb.toString());
                System.out.println("回答结束！！！");
            }

            @Override
            public void failed(Exception ex) {
                latch.countDown();
                System.out.println("failed");
                ex.printStackTrace();
            }

            @Override
            public void cancelled() {
                latch.countDown();
                System.out.println("cancelled");
            }

        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 返回最终答案，用于保存数据库的
        return sb.toString();
    }



    // 获取accesstoken,百度的api范例
    public String getAccessToken() throws IOException {
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "grant_type=client_credentials&client_id=" + API_KEY
                + "&client_secret=" + SECRET_KEY);
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/oauth/2.0/token")
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        return new JSONObject(response.body().string()).getString("access_token");
    }





}
