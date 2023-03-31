package com.kob.matchingsystem.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Set;

@Configuration
public class IpAddressFilter implements Filter {
    private final static Set<String> ipWhiteList = Set.of(
            "127.0.0.1",
            "0:0:0:0:0:0:0:1"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        if (!ipWhiteList.contains(req.getRemoteAddr())) {
            return;
        }

        try {
            chain.doFilter(request, response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}