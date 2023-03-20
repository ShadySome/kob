import { GameObject } from "./GameObject";
import { Wall } from "./Wall";

export class GameMap extends GameObject{
    constructor(ctx,parent){
        super();

        this.ctx = ctx;
        this.parent = parent;
        this.L = 0;
        this.rows = 13;
        this.cols = 13;

        this.walls = [];
        this.inner_wall_counts = 20;
    }

    start()
    {
       for(let i = 0; i < 1000; i ++)
       {
            if( this.creat_walls()) break;
       }
    }

    check_connectivity(g,sx,sy,tx,ty)
    {
        if(sx == tx && sy == ty) return true;
        g[sx][sy] = true;
        let dx = [0,1,0,-1] , dy = [1,0,-1,0];
        for(let i = 0; i < 4; i++)
        {
            let x = sx + dx[i] , y = sy + dy[i];
            if(!g[x][y] && this.check_connectivity(g,x,y,tx,ty)) 
                return true;
        }

        return false;
    }

    creat_walls()
    {
        const g = [];
        //g[i][j]为true的地方就创建障碍物
        for(let r = 0; r < this.rows; r ++)
        {
            g[r] = [];
            for(let c = 0; c < this.cols; c ++)
                g[r][c] = false;
        }
        //四周添加障碍物
        for(let r = 0; r < this.rows; r ++)
            g[r][0] = g[r][this.cols - 1] = true;
        
        for(let c = 0; c < this.cols; c ++)
            g[0][c] = g[this.rows-1][c] = true;

        //随机创建内部障碍
        for(let i = 0; i < this.inner_wall_counts; i ++)
        {
            for(let j = 0; j < 1000; j ++)
            {
                let r = parseInt(Math.random() * this.rows);
                let c = parseInt(Math.random() * this.cols);
                if(g[r][c]) continue;
                if(r == this.rows-2 && c == 1 || c == this.cols - 2 && r == 1 ) continue;

                g[r][c] = g[c][r] = true;
                break;
            }
        }
        let copy_g = JSON.parse(JSON.stringify(g));
        //检测随机出来的障碍物，是否导致地图不连通
        if(!this.check_connectivity(copy_g,this.rows-2,1,1,this.cols-2)) return false;
        for(let r = 0; r < this.rows; r ++)
        {
            for(let c = 0; c < this.cols; c ++)
            {
                if(g[r][c]) this.walls.push(new Wall(c,r,this));
            }
        }
        return true;
    }

    update_size()
    {
        this.L = parseInt(Math.min(this.parent.clientWidth/this.cols , this.parent.clientHeight / this.rows));
        this.ctx.canvas.width = this.L * this.cols;
        this.ctx.canvas.height = this.L * this.rows;
    }

    update(){
        this.update_size();
        this.render();
    }

    render(){
        const color_even = "#b3d665" , color_odd = "#acd05e";
        for(let r = 0; r < this.rows; r ++)
        {
            for(let c = 0; c < this.cols; c++)
            {
                if((r+c) % 2 == 0) this.ctx.fillStyle = color_even;
                else this.ctx.fillStyle = color_odd;
                this.ctx.fillRect(c*this.L,r*this.L,this.L,this.L);
            }
        }
    }
}
