package com.kob.backend.service.impl.user.bot;

import com.kob.backend.mapper.BotMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.User;
import com.kob.backend.service.user.bot.UpdateBotService;
import com.kob.backend.utils.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
@Service
public class UpdateBotServiceImpl implements UpdateBotService {

    @Autowired
    BotMapper botMapper;

    @Override
    public Map<String, String> updateBot(Map<String, String> data) {
        int bot_id = Integer.parseInt(data.get("bot_id"));

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
        User user = UserUtil.getUserFromContext();
        Bot bot = botMapper.selectById(bot_id);
        if(bot == null)
        {
            map.put("error_message","bot不存在或者已经被删除");
            return map;
        }
        if(!bot.getUserId().equals(user.getId()))
        {
            map.put("error_message","没有权限修改别人的bot");
            return map;
        }

        Bot new_bot = new Bot(
                bot_id,
                user.getId(),
                title,
                description,
                content,
                bot.getRating(),
                bot.getCreateTime(),
                new Date()
                );
        botMapper.updateById(new_bot);
        map.put("error_message","success");
        return map;
    }
}
