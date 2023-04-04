package com.kob.matchingsystem.service.impl.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
@Component
public class MatchingPool extends Thread {

    private List<Player> players = new ArrayList<>();

    private ReentrantLock lock = new ReentrantLock();

    private static RestTemplate restTemplate;

    private final static String startGameUrl = "http://127.0.0.1:3000/pk/start/game/";

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate)
    {
        MatchingPool.restTemplate = restTemplate;
    }

    public void addPlayer(Integer userId,Integer rating, Integer botId)
    {
        lock.lock();
        try{
            List<Player> newPlayers = new ArrayList<>();
            for(Player player : players)
            {
                //防止自己和自己匹配到一起
                if(!player.getUserId().equals(userId))
                    newPlayers.add(player);
            }
            newPlayers.add(new Player(userId,rating,botId,0));
            players = newPlayers;
        }finally {
            lock.unlock();
        }
    }

    public void removePlayer(Integer userId)
    {
        lock.lock();
        try{
            List<Player> newPlayers = new ArrayList<>();
            for(Player player : players)
            {
                if(player.getUserId() != userId)
                    newPlayers.add(player);
            }
            players = newPlayers;
        }finally {
            lock.unlock();
        }
    }

    private void increasingWaitingTime()
    {
        for(Player player : players)
        {
            player.setWaitingTime(player.getWaitingTime()+1);
        }
    }

    //判断两名玩家是否能够匹配
    private boolean checkMatched(Player a, Player b)
    {
        Integer ratingDelta = Math.abs(a.getRating() - b.getRating());
        Integer minWaitingTime = Math.min(a.getWaitingTime(),b.getWaitingTime());
        return ratingDelta <= minWaitingTime * 10;
    }

    //将匹配结果发送给backend
    private void sendMatchResult(Player a, Player b)
    {
        MultiValueMap<String,String> data = new LinkedMultiValueMap<>();
        data.add("a_id",a.getUserId().toString());
        data.add("a_bot_id",a.getBotId().toString());
        data.add("b_id",b.getUserId().toString());
        data.add("b_bot_id",b.getBotId().toString());

        restTemplate.postForObject(startGameUrl,data,String.class);
    }

    //从前往后（按照等待时间从长到短），对玩家进行匹配
    private void matchPlayers()
    {
        System.out.println("match players" + players.toString());
        boolean[] used = new boolean[players.size()];

        for(int i = 0; i < players.size(); i ++)
        {
            if(used[i])
                continue;
            for(int j = i + 1; j < players.size(); j ++)
            {
                Player a = players.get(i), b = players.get(j);
                if(used[j])
                    continue;
                if(checkMatched(a,b))
                {
                    used[i] = used[j] = true;
                    sendMatchResult(a,b);
                    break;
                }
            }
        }

        List<Player> newPlayers = new ArrayList<>();
        for(int i = 0; i < players.size(); i ++)
        {
            if(!used[i]) newPlayers.add(players.get(i));
        }
        players = newPlayers;
    }

    @Override
    public void run() {
        while (true)
        {
            try {
                Thread.sleep(1000);
                lock.lock();
                try{
                    increasingWaitingTime();
                    matchPlayers();
                }finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
