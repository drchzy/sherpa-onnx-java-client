package com.k2fsa.websocket;

import cn.hutool.core.io.BufferUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * WebSocket客户端
 *
 * @author Eureka
 * @Date 2023/3/19
 */
@ClientEndpoint
public class WsClient {
    private static Logger log = Logger.getLogger("WsClient");
    private String serverUrl = "ws://127.0.0.1:6006/";
    private static Session session;

    public static Map<String, String> sessionTextMap = new HashMap<>();

    public static Session getSession() {
        return session;
    }

    /**
     * 初始化
     */
    //@PostConstruct
    void init() {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            URI uri = URI.create(serverUrl);
            session = container.connectToServer(WsClient.class, uri);
        } catch (DeploymentException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送消息
     */
    public void send(byte[] bytes) throws IOException {
        this.session.getAsyncRemote().sendBinary(BufferUtil.create(bytes));
    }

    public void sendStr(String str) throws IOException {
        this.session.getAsyncRemote().sendText(str);
    }


    /**
     * 打开连接
     *
     * @param session
     */
    @OnOpen
    public void onOpen(Session session) {
        //log.info("WebSocket打开");
        this.session = session;
    }

    /**
     * 接收消息
     *
     * @param text
     */
    @OnMessage
    public void onMessage(Session session, String text) {
        //log.info("WebSocket接收消息：" + text);
        if (!"Done!".equals(text)) {
            WsClient.sessionTextMap.put(session.getId(), text);
        }
    }

    /**
     * 异常处理
     *
     * @param throwable
     */
    @OnError
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
    }

    /**
     * 关闭连接
     */
    @OnClose
    public void onClosing() throws IOException {
        //log.info("WebSocket关闭");
        session.close();
    }

    public void close() throws IOException {
        if (this.session.isOpen()) {
            this.session.close();
        }
    }
}
