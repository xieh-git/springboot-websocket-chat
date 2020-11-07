package com.hehe.chat;

import javax.websocket.server.ServerEndpoint;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by wdq on 2019/2/18.
 */
@ServerEndpoint("/ws/push/{userId}")
@Component
public class WebSocketDemo {

    // Hashtablex线程安全的map用来存储一登录用户信息，key为用户id。
    private static Hashtable<String, WebSocketDemo> userMap = new Hashtable<>();

    // 静态变量，用来记录当前在线连接数。
    private static int onlineCount = 0;

    // 与客户端的连接会话。
    private Session session;

    // 与客户端的连接的用户id。
    private String userId;

    //连接打开时执行
    @OnOpen
    public void onOpen(Session session,@PathParam("userId") String userId) throws IOException{
        System.out.println("新客户端接入，用户ID：" + userId);
        System.out.println("在线人数：" + WebSocketDemo.onlineCount);
        if(!StringUtils.isEmpty(userId)){
            //判断该用户是否已登录过
            if(!userMap.containsKey(userId)){
                this.userId = userId;
                this.session = session;
                userMap.put(userId,this); // 加入set中
                addOnlineCount(); // 在线数加1
            }
        }
        System.out.println("在线人数：" + WebSocketDemo.onlineCount);
    }

    //连接关闭调用的方法
    @OnClose
    public void onClose() {
        System.out.println("客户端关闭连接："+this.userId);
        userMap.remove(this.userId); // 从map中删除
        subOnlineCount(); // 在线数减1
        System.out.println("在线人数：" + WebSocketDemo.onlineCount);
    }

    //收到客户端消息后调用的方法
    @OnMessage
    public void onMessage(String message, Session session) {
        if(!StringUtils.isEmpty(this.userId)){
            if(!"ping".equals(message)){//不是心跳检测
                //收到消息后可以去做一些具体的业务处理在推送，此处直接推送
                sendAll("【"+this.userId+"】"+message);
            }
        }else{
            System.out.println("当前客户未登陆："+this.userId);
        }
        System.out.println("用户【"+this.userId+"】访问");
    }

    //发生错误时调用
    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    public void sendMessage(String userId,String message){
        try {
            if(StringUtils.isEmpty(userId)){
                System.out.println("客户ID不能为空");
                return ;
            }
            for(Map.Entry<String, WebSocketDemo> entry : userMap.entrySet()){
                if(entry.getKey().equals(userId)){
                    entry.getValue().getSession().getBasicRemote().sendText(message);
                    System.out.println("推送给用户【"+entry.getKey()+"】消息成功，消息为：" + message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(List<String> userIds,String message){
        try {
            if(userIds == null || userIds.size() == 0){
                System.out.println("客户ID不能为空");
                return ;
            }
            for(Map.Entry<String, WebSocketDemo> entry : userMap.entrySet()){
                if(userIds.contains(entry.getKey())){
                    entry.getValue().getSession().getBasicRemote().sendText(message);
                    System.out.println("推送给用户【"+entry.getKey()+"】消息成功，消息为：" + message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendAll(String message){
        try {
            if(StringUtils.isEmpty(userId)){
                System.out.println("客户ID不能为空");
                return ;
            }
            for(Map.Entry<String, WebSocketDemo> entry : userMap.entrySet()){
                entry.getValue().getSession().getBasicRemote().sendText(message);
                System.out.println("推送给用户【"+entry.getKey()+"】消息成功，消息为：" + message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //获取连接人数
    public static synchronized int getOnlineCount() {
        return onlineCount;
    }
    //连接人数加一
    public static synchronized void addOnlineCount() {
        onlineCount+=1;
    }
    //连接人数减一
    public static synchronized void subOnlineCount() {
        if(onlineCount > 0){
            onlineCount-=1;
        }
    }

    public Session getSession() {
        return session;
    }

}
