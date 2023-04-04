package com.kob.botrunningsystem.utils;

import java.util.ArrayList;
import java.util.List;

public class Bot implements com.kob.botrunningsystem.utils.BotInterface{

    static class Cell{
        public int x, y;

        public Cell(int x, int y)
        {
            this.x = x;
            this.y = y;
        }
    }

    private boolean check_is_increasing(int step)
    {
        if(step <= 10) return true;
        return step % 3 == 1;
    }

    public List<Cell> getCell(int sx,int sy,String steps)
    {
        steps = steps.substring(1,steps.length()-1);
        List<Cell> res = new ArrayList<>();
        int[] dx = {-1,0,1,0}, dy = {0,1,0,-1};

        int x = sx, y = sy;
        int step = 0;
        res.add(new Cell(x,y));
        for(int i = 0; i < steps.length(); i ++)
        {
            int d = steps.charAt(i) - '0';
            x = x + dx[d];
            y = y + dy[d];
            res.add(new Cell(x,y));
            if(!check_is_increasing(++step))
                res.remove(0);
        }
        return res;
    }
    @Override
    public Integer nextMove(String input) {
        String[] args = input.split("#");
        int[][] g = new int[13][14];
        for(int i = 0, k = 0; i < 13; i ++)
        {
            for(int j = 0; j < 14;j ++, k ++)
            {
                if(args[0].charAt(k) == '1')
                {
                    g[i][j] = 1;
                }
            }
        }

        int a_sx = Integer.parseInt(args[1]), a_sy = Integer.parseInt(args[2]);
        int b_sx = Integer.parseInt(args[4]), b_sy = Integer.parseInt(args[5]);

        List<Cell> a_cells = getCell(a_sx,a_sy,args[3]);
        List<Cell> b_cells = getCell(b_sx,b_sy,args[6]);

        for(Cell c : a_cells) g[c.x][c.y] = 1;
        for(Cell c : b_cells) g[c.x][c.y] = 1;
        int[] dx = {-1,0,1,0}, dy = {0,1,0,-1};

        for(int i = 0; i < 4; i ++)
        {
            int x = a_cells.get(a_cells.size()-1).x + dx[i];
            int y = a_cells.get(a_cells.size()-1).y + dy[i];

            if(x > 0 && x < 13 && y > 0 && y < 14 && g[x][y] == 0)
                return i;
        }
        return 0;
    }
}
