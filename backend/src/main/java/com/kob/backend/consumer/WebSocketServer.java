package com.kob.backend.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kob.backend.consumer.util.Game;
import com.kob.backend.consumer.util.JwtAuthorization;
import com.kob.backend.mapper.RecordMapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import jakarta.websocket.*;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@ServerEndpoint("/websocket/{token}")  // 注意不要以'/'结尾
public class WebSocketServer {

    public static ConcurrentHashMap<Integer,WebSocketServer> users = new ConcurrentHashMap<>();
    private static CopyOnWriteArrayList<User> matchpool = new CopyOnWriteArrayList<>();
    private User user;
    private Session session = null;

    private Game game = null;
    //这里由于websocket不是springboot的原生组件，不是单例的
    //所以在注入时要如下这样
    private static UserMapper userMapper;
    public static RecordMapper recordMapper;

    @Autowired
    public void setUserMapper(UserMapper userMapper)
    {
        WebSocketServer.userMapper = userMapper;
    }

    @Autowired
    public void setRecordMapper(RecordMapper recordMapper) {
        WebSocketServer.recordMapper = recordMapper;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) throws IOException {
        // 建立连接
        System.out.println("connected!");
        this.session = session;
        Integer userId = JwtAuthorization.getUserId(token);
        this.user = userMapper.selectById(userId);

        if(this.user != null) {
            users.put(userId, this);
            System.out.println(this.user);
        }
        else{
            this.session.close();
        }
    }

    @OnClose
    public void onClose() {
        // 关闭链接
        System.out.println("disconnected!");
        if(this.user != null)
        {
            matchpool.remove(this.user);
            users.remove(this.user.getId());
        }
    }

    private void startMatching()
    {
        System.out.println("start matching!");
        matchpool.add(this.user);

        while(matchpool.size() >= 2)
        {
            Iterator<User> it = matchpool.iterator();
            User a = it.next(), b = it.next();
            Game game = new Game(13,14,20,a.getId(),b.getId());
            game.createMap();
            users.get(a.getId()).game = game;
            users.get(b.getId()).game = game;

            game.start();

            JSONObject respGame = new JSONObject();
            respGame.put("a_id",game.getPlayerA().getId());
            respGame.put("a_sx",game.getPlayerA().getSx());
            respGame.put("a_sy",game.getPlayerA().getSy());
            respGame.put("b_id",game.getPlayerB().getId());
            respGame.put("b_sx",game.getPlayerB().getSx());
            respGame.put("b_sy",game.getPlayerB().getSy());
            respGame.put("map",game.getG());

            JSONObject respA = new JSONObject();
            respA.put("event","start-matching");
            respA.put("opponent_username",b.getUsername());
            respA.put("opponent_photo",b.getPhoto());
            respA.put("game",respGame);
            users.get(a.getId()).sendMessage(respA.toJSONString());

            JSONObject respB = new JSONObject();
            respB.put("event","start-matching");
            respB.put("opponent_username",a.getUsername());
            respB.put("opponent_photo",a.getPhoto());
            respB.put("game",respGame);
            users.get(b.getId()).sendMessage(respB.toJSONString());
            matchpool.remove(a);
            matchpool.remove(b);
        }
    }

    private void stopMatching()
    {
        System.out.println("stop matching!");
        matchpool.remove(this.user);
    }

    private void setMove(Integer direction)
    {
        if(game.getPlayerA().getId().equals(user.getId()))
        {
            game.setNextStepA(direction);
        } else if (game.getPlayerB().getId().equals(user.getId())) {
            game.setNextStepB(direction);
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {//作用类似于路由
        // 从Client接收消息

        JSONObject data = JSONObject.parseObject(message);
        String event = data.getString("event");
        if("start-matching".equals(event))
        {
            startMatching();
        }
        else if("stop-matching".equals(event))
        {
            stopMatching();
        }
        else if("move".equals(event))
        {
            setMove(data.getInteger("direction"));
        }
        System.out.println("receive message!");
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    //server主动向client发送消息，由于是异步的，要加锁
    public void sendMessage(String message)
    {
        synchronized (this.session){
            try{
                this.session.getBasicRemote().sendText(message);
            }catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}