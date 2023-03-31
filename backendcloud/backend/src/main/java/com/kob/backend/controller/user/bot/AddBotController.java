package com.kob.backend.controller.user.bot;

import com.kob.backend.service.user.bot.AddBotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AddBotController {

    @Autowired
    AddBotService addBotService;
    @PostMapping("/user/bot/add/")
    public Map<String,String> addBot(@RequestParam Map<String,String> data){
        return addBotService.addBot(data);
    }
}
