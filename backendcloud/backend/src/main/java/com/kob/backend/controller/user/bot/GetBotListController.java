package com.kob.backend.controller.user.bot;

import com.kob.backend.pojo.Bot;
import com.kob.backend.service.user.bot.GetBotListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GetBotListController {

    @Autowired
    GetBotListService getBotListService;
    @GetMapping("/user/bot/getlist/")
    public List<Bot> getBotList()
    {
        return getBotListService.getBotList();
    }
}
