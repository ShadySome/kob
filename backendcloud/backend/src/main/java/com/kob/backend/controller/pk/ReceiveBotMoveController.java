package com.kob.backend.controller.pk;

import com.kob.backend.service.ReceiveBotMoveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class ReceiveBotMoveController {

    @Autowired
    ReceiveBotMoveService receiveBotMoveService;

    @RequestMapping("/pk/receive/bot/move/")
    private String receiveBotMove(@RequestParam MultiValueMap<String,String> data)
    {
        Integer userId = Integer.parseInt(Objects.requireNonNull(data.getFirst("user_id")));
        Integer direction = Integer.parseInt(Objects.requireNonNull(data.getFirst("direction")));
        return receiveBotMoveService.receiveBotMove(userId,direction);
    }
}
