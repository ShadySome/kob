package com.kob.backend.service.impl.pk;

import com.kob.backend.consumer.WebSocketServer;
import com.kob.backend.consumer.util.Game;
import com.kob.backend.service.ReceiveBotMoveService;
import org.springframework.stereotype.Service;

@Service
public class ReceiveBotMoveServiceImpl implements ReceiveBotMoveService {
    @Override
    public String receiveBotMove(Integer userId, Integer direction) {

        Game game= WebSocketServer.users.get(userId).game;
        if(game.getPlayerA().getId().equals(userId))
        {
            game.setNextStepA(direction);
        } else if (game.getPlayerB().getId().equals(userId)) {
            game.setNextStepB(direction);
        }
        System.out.println("receive-bot-move: " + userId + " " + direction);
        return "receive bot move success";
    }
}
