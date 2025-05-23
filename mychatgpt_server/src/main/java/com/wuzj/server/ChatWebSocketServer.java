package com.wuzj.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wuzj.entity.UserLog;
import com.wuzj.model.ChatModel;
import com.wuzj.service.UserLogService;
import com.wuzj.service.UserService;
import com.wuzj.vo.chat.ChatRequestParameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * @author wuzj
 * @date 2023/8/29
 */
@Component
@ServerEndpoint("/chatWebSocket/{username}")
@Slf4j
public class ChatWebSocketServer {

    /**
     * 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
     */
    private static int onlineCount = 0;
    /**
     * concurrent包的线程安全Map，用来存放每个客户端对应的MyWebSocket对象。
     */
    private static ConcurrentHashMap<String, ChatWebSocketServer> chatWebSocketMap = new ConcurrentHashMap<>();

    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;
    /**
     * 接收的username
     */
    private String username = "";

    private UserLog userLog;

    private static UserService userService;
    private static UserLogService userLogService;

    @Resource
    public void setUserService(UserService userService) {
        ChatWebSocketServer.userService = userService;
    }

    @Resource
    public void setUserLogService(UserLogService userLogService) {
        ChatWebSocketServer.userLogService = userLogService;
    }

    private ObjectMapper objectMapper = new ObjectMapper();
    private static ChatModel chatModel;

    @Resource
    public void setChatModel(ChatModel chatModel) {
        ChatWebSocketServer.chatModel = chatModel;
    }

    ChatRequestParameter chatRequestParameter = new ChatRequestParameter();

    /**
     * 建立连接
     * @param session 会话
     * @param username 连接用户名称
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) {
        this.session = session;
        this.username = username;
        this.userLog = new UserLog();
        // 这里的用户id不可能为null，出现null，那么就是非法请求
        try {
            this.userLog.setUserId(userService.queryByName(username).getId());
        } catch (Exception e) {
            e.printStackTrace();
            try {
                session.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        this.userLog.setUsername(username);
        chatWebSocketMap.put(username, this);
        onlineCount++;
        log.info("{}--open",username);
    }

    @OnClose
    public void onClose() {
        chatWebSocketMap.remove(username);
        log.info("{}--close",username);
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        String handledMessage = message.substring(1);
        System.out.println(username + "--" + handledMessage);
        // 记录日志
        setType(message);
        this.userLog.setDateTime(LocalDateTime.now());
        this.userLog.setPreLogId(this.userLog.getLogId() == null ? -1 : this.userLog.getLogId());
        this.userLog.setLogId(null);
        this.userLog.setQuestion(handledMessage);
        long start = System.currentTimeMillis();
        // 将问题的答案进行返回
        Consumer<String> resConsumer = res -> {
            try {
                session.getBasicRemote().sendText(res);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
        // 这里就会返回结果
        String answer = chatModel.getAnswer(resConsumer ,chatRequestParameter, handledMessage);
        if(answer.isEmpty()){
            try {
                session.getBasicRemote().sendText("问题之城崩溃了╥﹏╥...");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        long end = System.currentTimeMillis();
        this.userLog.setConsumeTime(end - start);
        this.userLog.setAnswer(answer);
        userLogService.save(userLog);
    }
    public void setType(String message){
        if(message.charAt(0) == 'c'){
            this.userLog.setType("child");
        }else if(message.charAt(0) == 'p') {
                this.userLog.setType("parent");
        } else if(message.charAt(0) == 'q'){
            this.userLog.setType("question");
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    public static void sendInfo(String message, String toUserId) throws IOException {
        chatWebSocketMap.get(toUserId).sendMessage(message);
    }
}
