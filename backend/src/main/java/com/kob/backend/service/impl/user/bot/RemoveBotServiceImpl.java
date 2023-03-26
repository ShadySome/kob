package com.kob.backend.service.impl.user.bot;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.backend.mapper.BotMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.User;
import com.kob.backend.service.user.bot.RemoveBotService;
import com.kob.backend.utils.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RemoveBotServiceImpl implements RemoveBotService {

    @Autowired
    BotMapper botMapper;

    @Override
    public Map<String, String> removeBot(Map<String, String> data) {
        int bot_id = Integer.parseInt(data.get("bot_id"));
        User user = UserUtil.getUserFromContext();
        Bot bot = botMapper.selectById(bot_id);
        Map<String,String> map = new HashMap<>();

        if(bot == null)
        {
            map.put("error_message","bot不存在或已经被删除");
            return map;
        }
        if(!bot.getUserId().equals(user.getId()))
        {
            map.put("error_message","不能删除不属于自己的bot");
            return map;
        }
        botMapper.deleteById(bot_id);
        map.put("error_message","success");
        return map;
    }
}
