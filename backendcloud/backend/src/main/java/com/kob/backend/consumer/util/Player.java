package com.kob.backend.consumer.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Player {
    private Integer id;
    private Integer botId;
    private String botCode;
    private Integer sx;
    private Integer sy;
    private List<Integer> steps;

    private boolean check_is_increasing(int step)
    {
        if(step <= 10) return true;
        return step % 3 == 1;
    }

    public List<Cell> getCell()
    {
        List<Cell> res = new ArrayList<>();
        int[] dx = {-1,0,1,0}, dy = {0,1,0,-1};

        int x = sx, y = sy;
        int step = 0;
        res.add(new Cell(x,y));
        for(int d : steps)
        {
            x = x + dx[d];
            y = y + dy[d];
            res.add(new Cell(x,y));
            if(!check_is_increasing(++step))
                res.remove(0);
        }
        return res;
    }

    public String getStepsString(List<Integer> steps)
    {
        StringBuilder res = new StringBuilder();
        for(int d : steps)
        {
            res.append(d);
        }
        return res.toString();
    }
}
