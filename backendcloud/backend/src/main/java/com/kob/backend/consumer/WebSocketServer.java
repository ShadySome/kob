package com.kob.backend.consumer;

import com.alibaba.fastjson2.JSONObject;
import com.kob.backend.consumer.util.Game;
import com.kob.backend.consumer.util.JwtAuthorization;
import com.kob.backend.mapper.BotMapper;
import com.kob.backend.mapper.RecordMapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.User;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import jakarta.websocket.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@ServerEndpoint("/websocket/{token}")  // 注意不要以'/'结尾
public class WebSocketServer {

    public static ConcurrentHashMap<Integer,WebSocketServer> users = new ConcurrentHashMap<>();
    private User user;
    private Session session = null;

    public Game game = null;
    //这里由于websocket不是springboot的原生组件，不是单例的
    //所以在注入时要如下这样
    private static UserMapper userMapper;
    public static RecordMapper recordMapper;
    public static RestTemplate restTemplate;
    private static BotMapper botMapper;
    private final static String addPlayerUrl = "http://127.0.0.1:3001/player/add/";
    private final static String removePlayerUrl = "http://127.0.0.1:3001/player/remove/";


    @Autowired
    public void setUserMapper(UserMapper userMapper)
    {
        WebSocketServer.userMapper = userMapper;
    }

    @Autowired
    public void setRecordMapper(RecordMapper recordMapper) {
        WebSocketServer.recordMapper = recordMapper;
    }

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate)
    {
        WebSocketServer.restTemplate = restTemplate;
    }

    @Autowired
    public void setBotMapper(BotMapper botMapper) {
        WebSocketServer.botMapper = botMapper;
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
            users.remove(this.user.getId());
        }
    }

    public static void startGame(Integer aId,Integer aBotId,Integer bId,Integer bBotId)
    {
        User a = userMapper.selectById(aId), b = userMapper.selectById(bId);
        Bot botA = botMapper.selectById(aBotId),botB = botMapper.selectById(bBotId);
        Game game = new Game(
                13,
                14,
                20,
                a.getId(),
                botA,
                b.getId(),
                botB);
        game.createMap();
        if(users.get(a.getId()) != null)
            users.get(a.getId()).game = game;
        if(users.get(b.getId()) != null)
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
        if(users.get(a.getId()) != null)
            users.get(a.getId()).sendMessage(respA.toJSONString());

        JSONObject respB = new JSONObject();
        respB.put("event","start-matching");
        respB.put("opponent_username",a.getUsername());
        respB.put("opponent_photo",a.getPhoto());
        respB.put("game",respGame);
        if(users.get(b.getId()) != null)
            users.get(b.getId()).sendMessage(respB.toJSONString());
    }

    private void startMatching(Integer botId)
    {
        System.out.println("start matching!");
        MultiValueMap<String,String> data = new LinkedMultiValueMap<>();
        data.add("userId",this.user.getId().toString());
        data.add("rating",this.user.getRating().toString());
        data.add("botId",botId.toString());

        restTemplate.postForObject(addPlayerUrl,data,String.class);
    }

    private void stopMatching()
    {
        System.out.println("stop matching!");
        MultiValueMap<String,String> data = new LinkedMultiValueMap<>();
        data.add("userId",this.user.getId().toString());
        restTemplate.postForObject(removePlayerUrl,data,String.class);
    }

    private void setMove(Integer direction)
    {
        if(game.getPlayerA().getId().equals(user.getId()))
        {
            if(game.getPlayerA().getBotId().equals(-1)) //亲自出马时，发送每一步的选择
                game.setNextStepA(direction);
        } else if (game.getPlayerB().getId().equals(user.getId())) {
            if(game.getPlayerB().getBotId().equals(-1))
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
            startMatching(Integer.parseInt(data.getString("bot_id")));
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