package com.hehe.chat;

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

/**
 * @author 谢辉
 * @Classname WebSocketHttpSessionConfigurator
 * @Description TODO
 * @Date 2020/11/7 11:45
 */
public class WebSocketHttpSessionConfigurator extends ServerEndpointConfig.Configurator {

    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        HttpSession httpSession = (HttpSession)request.getHttpSession();
        // 将httpSession对象存储到配置对象中
        sec.getUserProperties().put(HttpSession.class.getName(),httpSession);
    }
}
