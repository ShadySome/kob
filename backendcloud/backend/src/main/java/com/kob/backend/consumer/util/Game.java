package com.kob.backend.consumer.util;

import com.alibaba.fastjson2.JSONObject;
import com.kob.backend.consumer.WebSocketServer;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.Record;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class Game extends Thread {

    private final Integer rows;
    private final Integer cols;
    private final Integer inner_walls_count;
    private final int [][] g;
    private final static int[] dx = {-1,0,1,0},dy = {0,1,0,-1};

    private Player playerA,playerB;
    private Integer nextStepA = null;
    private Integer nextStepB = null;
    private ReentrantLock lock = new ReentrantLock();
    private String status = "playing"; // playing -> finished
    private String loser = ""; // all为平局,A为A输,B为B输
    public final static String sendCodeUrl = "http://127.0.0.1:3002/bot/add/";

    public Game(
            Integer rows,
            Integer cols,
            Integer inner_walls_count,
            Integer idA,
            Bot botA,
            Integer idB,
            Bot botB)
    {
        this.rows = rows;
        this.cols = cols;
        this.inner_walls_count = inner_walls_count;
        g = new int[rows][cols];

        Integer botIdA = -1, botIdB = -1;
        String botCodeA = "" , botCodeB = "";

        if(botA != null)
        {
            botIdA = botA.getId();
            botCodeA = botA.getContent();
        }
        if(botB != null)
        {
            botIdB = botB.getId();
            botCodeB = botB.getContent();
        }
        this.playerA = new Player(idA,botIdA,botCodeA,this.rows-2,1,new ArrayList<>());
        this.playerB = new Player(idB,botIdB,botCodeB,1,this.cols-2,new ArrayList<>());
    }

    public Player getPlayerA()
    {
        return this.playerA;
    }
    public Player getPlayerB()
    {
        return this.playerB;
    }
    public int[][] getG()
    {
        return this.g;
    }

    public void setNextStepA(Integer nextStepA) {
        lock.lock();
        try{
            this.nextStepA = nextStepA;
        }finally {
            lock.unlock();
        }
    }

    public void setNextStepB(Integer nextStepB) {
        lock.lock();
        try{
            this.nextStepB = nextStepB;
        }finally {
            lock.unlock();
        }
    }

    private boolean check_connectivity(int sx, int sy, int tx, int ty)
    {
        if(sx == tx && sy == ty) return true;

        g[sx][sy] = 1;

        for(int i = 0; i < 4; i ++)
        {
            int x = sx + dx[i] , y = sy + dy[i];
            if(x >= 0 && x < this.rows && y >= 0 && y < this.cols && g[x][y] == 0)
            {
                if(check_connectivity(x,y,tx,ty))
                {
                    g[sx][sy] = 0;
                    return true;
                }
            }
        }
        g[sx][sy] = 0;
        return false;
    }

    private boolean draw()
    {
        for(int i = 0; i < this.rows; i ++)
            for(int j = 0; j < this.cols; j ++)
                this.g[i][j] = 0;

        for(int r = 0;  r < this.rows; r ++)
            g[r][0] = g[r][this.cols-1] = 1;
        for(int c = 0; c < this.cols; c ++)
            g[0][c] = g[this.rows-1][c] = 1;

        Random random = new Random();
        for(int i = 0; i < this.inner_walls_count / 2 ; i ++)
        {
            for(int j = 0; j < 1000; j ++)
            {
                int r = random.nextInt(this.rows);
                int c = random.nextInt(this.cols);

                if(g[r][c] == 1 || g[this.rows - 1 - r][this.cols - 1 - c] == 1)
                    continue;
                if(r == this.rows - 2 && c == 1 || r == 1 && c == this.cols - 2)
                    continue;
                g[r][c] = g[this.rows - 1 - r][this.cols - 1 - c] = 1;
                break;
            }
        }

        return check_connectivity(this.rows-2,1,1,this.cols-2);
    }

    public void  createMap()
    {
        for(int i = 0; i < 1000; i ++)
        {
            if(draw()) break;
        }
    }

    private String getInput(Player player)//将当前的局面信息，编码成字符串
    {
        Player me, you;
        if(player.getId().equals(playerA.getId()))
        {
            me = playerA;
            you = playerB;
        }else{
            me = playerB;
            you = playerA;
        }
        //mapinfo # me.sx # me.sy # (me.steps) # you.sx # you.sy # (you.steps)
        return getMapString() + "#" +
                me.getSx() + "#" +
                me.getSy() + "#(" +
                me.getStepsString(me.getSteps()) + ")#" +
                you.getSx() + "#" +
                you.getSy() + "#(" +
                you.getStepsString(you.getSteps()) + ")";
    }

    private void sendBotCode(Player player)
    {
        if(player.getBotId().equals(-1)) return; //亲自出马
        MultiValueMap<String,String> data = new LinkedMultiValueMap<>();
        data.add("user_id",player.getId().toString());
        data.add("bot_code",player.getBotCode());
        data.add("input",getInput(player));
        WebSocketServer.restTemplate.postForObject(sendCodeUrl,data,String.class);
    }

    private boolean nextStep()//每隔一秒查看一次两名玩家是否都给出了下一步
    {
        //前端0.2秒才能渲染一个格子，所以后端最少等200ms，才获取一次玩家输入
        //还有点问题
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        sendBotCode(playerA);
        sendBotCode(playerB);
        for(int i = 0; i < 50; i ++)
        {
            try {
                Thread.sleep(100);
                lock.lock();
                try {
                    if(nextStepA != null && nextStepB != null)
                    {
                        playerA.getSteps().add(nextStepA);
                        playerB.getSteps().add(nextStepB);
                        return true;
                    }
                }finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private boolean check_valid(List<Cell>cellA,List<Cell>cellB)
    {
        int n = cellA.size();
        Cell cell = cellA.get(n - 1);

        if(g[cell.x][cell.y] ==  1) return false;

        for(int i = 0; i < n - 1; i ++)
        {
            if(cell.x == cellA.get(i).x && cell.y == cellA.get(i).y)
                return false;
        }

        for(int i = 0; i < n-1; i ++)
            if(cell.x == cellB.get(i).x && cell.y == cellB.get(i).y)
                return false;

        return true;
    }

    private void judge(){ //判断两名玩家下一步的操作是否合法
        List<Cell> cellA = this.playerA.getCell();
        List<Cell> cellB = this.playerB.getCell();
        //检测A的最后一步是否合法
        boolean validA = check_valid(cellA,cellB);
        //检测B的最后一步是否合法
        boolean validB = check_valid(cellB,cellA);

        if(!validA || !validB)
        {
            status = "finished";
            if(!validA && !validB){
                loser = "all";
            } else if (!validA) {
                loser = "A";
            } else {
                loser = "B";
            }
        }
    }

    private void sendAllMessage(String message){
        if(WebSocketServer.users.get(playerA.getId()) != null)
            WebSocketServer.users.get(playerA.getId()).sendMessage(message);
        if(WebSocketServer.users.get(playerB.getId()) != null)
        WebSocketServer.users.get(playerB.getId()).sendMessage(message);
    }

    private void sendMove(){ //向两名玩家传递下一步各自选择的操作是什么
        lock.lock();
        try{
            JSONObject resp = new JSONObject();
            resp.put("event","move");
            resp.put("a_direction",nextStepA);
            resp.put("b_direction",nextStepB);
            sendAllMessage(resp.toJSONString());
            nextStepA = nextStepB = null;
        }finally {
            lock.unlock();
        }
    }

    private String getMapString()
    {
        StringBuilder res = new StringBuilder();
        for(int i = 0; i < rows; i ++)
        {
            for(int j = 0; j < cols; j ++)
            {
                res.append(g[i][j]);
            }
        }
        return res.toString();
    }

    private void saveToDataBase()
    {
        Record record = new Record(
                null,
                playerA.getId(),
                playerA.getSx(),
                playerA.getSy(),
                playerB.getId(),
                playerB.getSx(),
                playerB.getSy(),
                playerA.getStepsString(playerA.getSteps()),
                playerB.getStepsString(playerB.getSteps()),
                getMapString(),
                loser,
                new Date()
        );

        WebSocketServer.recordMapper.insert(record);
    }

    private void sendResult() {//向玩家公布游戏结果
        JSONObject resp = new JSONObject();
        resp.put("event","result");
        resp.put("loser",loser);
        saveToDataBase();
        sendAllMessage(resp.toJSONString());
    }

    @Override
    public void run() {
        //最多13*14步就满了，获取1000次下一步是为了避免死循环
        for(int i = 0; i < 1000; i ++)
        {
            if(nextStep())
            {
                judge();
                if(status.equals("playing"))
                {
                    sendMove();
                }else{
                    sendResult();
                    break;
                }
            }else{
                status = "finished";
                lock.lock();
                try{
                    if(nextStepA == null && nextStepB == null)
                    {
                        loser = "all";
                    } else if (nextStepA == null) {
                        loser = "A";
                    }else{
                        loser = "B";
                    }
                }finally {
                    lock.unlock();
                }
                sendResult();
                break;
            }
        }
    }
}
