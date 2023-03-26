package com.kob.backend.service.impl.user.bot;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.backend.mapper.BotMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.User;
import com.kob.backend.service.user.bot.GetBotListService;
import com.kob.backend.utils.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetBotListServiceImpl implements GetBotListService {

    @Autowired
    BotMapper botMapper;

    @Override
    public List<Bot> getBotList() {
        User user = UserUtil.getUserFromContext();
        QueryWrapper<Bot> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",user.getId());
        List<Bot> bots = botMapper.selectList(queryWrapper);

        return bots;
    }
}
