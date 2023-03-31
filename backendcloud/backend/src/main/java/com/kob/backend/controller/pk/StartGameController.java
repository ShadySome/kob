package com.kob.backend.controller.pk;

import com.kob.backend.service.StartGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class StartGameController {

    @Autowired
    StartGameService startGameService;

    @PostMapping("/pk/start/game/")
    private String startGame(@RequestParam MultiValueMap<String,String> data)
    {
       Integer aId = Integer.parseInt(Objects.requireNonNull(data.getFirst("aId")));
       Integer bId = Integer.parseInt(Objects.requireNonNull(data.getFirst("bId")));

       return startGameService.startGame(aId,bId);
    }
}
