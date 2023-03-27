package com.kob.backend.service.impl.user.bot;

import com.kob.backend.mapper.BotMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.User;
import com.kob.backend.service.user.bot.AddBotService;
import com.kob.backend.utils.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
@Service
public class AddBotServiceImpl implements AddBotService {
    @Autowired
    BotMapper botMapper;

    @Override
    public Map<String, String> addBot(Map<String, String> data) {
        User user = UserUtil.getUserFromContext();
        String title = data.get("title");
        String description = data.get("description");
        String content = data.get("content");

        Map<String,String> map = new HashMap<>();

        if(title == null || title.length() == 0)
        {
            map.put("error_message", "bot名称不能为空");
            return map;
        }
        if(title.length() > 100)
        {
            map.put("error_message","bot名称长度不能大于100");
            return map;
        }
        if(description == null || description.length() == 0)
        {
            description = "这是一个普通的bot";
        }
        if(description.length() > 300)
        {
            map.put("error_message","bot描述长度不能超过300");
            return map;
        }
        if(content == null || content.length() == 0)
        {
            map.put("error_message","bot代码不能为空");
            return map;
        }
        if(content.length()> 10000)
        {
            map.put("error_message","bot代码过长");
            return map;
        }
        Date now = new Date();
        Bot bot = new Bot(null,user.getId(),title,description,content,1500,now,now);
        botMapper.insert(bot);
        map.put("error_message" ,"success");
        return map;
    }
}
