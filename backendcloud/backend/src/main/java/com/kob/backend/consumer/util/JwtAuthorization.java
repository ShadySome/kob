package com.kob.backend.consumer.util;

import com.kob.backend.utils.JwtUtil;
import io.jsonwebtoken.Claims;

public class JwtAuthorization {

    public static Integer getUserId(String token)
    {
        Integer userId = -1;
        try {//解析令牌，得到userid
            Claims claims = JwtUtil.parseJWT(token);
            userId = Integer.parseInt(claims.getSubject());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return userId;
    }
}
