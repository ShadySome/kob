import { GameObject } from "./GameObject";
import { Snake } from "./Snake";
import { Wall } from "./Wall";

export class GameMap extends GameObject{
    constructor(ctx,parent,store){
        super();

        this.ctx = ctx;
        this.parent = parent;
        this.L = 0;
        this.rows = 13;
        this.cols = 14;
        this.store = store;

        this.walls = [];
        this.inner_wall_counts = 20;

        this.snakes = [
            new Snake({id:0,color:"#4876EC",r:this.rows-2,c:1},this),
            new Snake({id:1,color:"#F94848",r:1,c:this.cols-2},this)
        ];
    }

    add_listening_events()
    {
        this.ctx.canvas.focus();
        this.ctx.canvas.addEventListener("keydown",e=>{
            let d = -1;
            if(e.key === 'w') d = 0;
            else if(e.key === 'd') d = 1;
            else if(e.key === 's') d = 2;
            else if(e.key === 'a') d = 3;

            if(d >= 0)
            {
                this.store.state.pk.socket.send(JSON.stringify({
                    event: "move",
                    direction: d,
                }));
            }
        });
    }

    start()
    {
       this.create_walls();

       this.add_listening_events();
    }

    create_walls()
    {
        const g = this.store.state.pk.gamemap;
        if(g === null){
            this.store.state.pk.status = "matching";
            return;
        }
        for(let r = 0; r < this.rows; r ++)
        {
            for(let c = 0; c < this.cols; c ++)
            {
                if(g[r][c]) this.walls.push(new Wall(r,c,this));
            }
        }
    }

    update_size()
    {
        this.L = parseInt(Math.min(this.parent.clientWidth/this.cols , this.parent.clientHeight / this.rows));
        this.ctx.canvas.width = this.L * this.cols;
        this.ctx.canvas.height = this.L * this.rows;
    }

    check_ready() //检查两条蛇是否都准备好下一回合了
    {
        for(const snake of this.snakes)
        {
            if(snake.status !== "idle")return false;
            if(snake.direction === -1) return false;
        }
        // window.alert("ready");
        return true;
    }
    //检测下一个目标单元格是否合法
    check_valid(cell)
    {
        for(const wall of this.walls)
        {
            if(wall.r === cell.r && wall.c === cell.c) 
                return false;
        }

        for(const snake of this.snakes)
        {
            let k = snake.cells.length;
            //如果蛇尾会移动，也就是当前蛇尾这个格子会被删除，那么就不用考虑蛇尾了
            if(!snake.check_tail_increasing())
                k --;
            for(let i = 0; i < k; i ++)
            {
                if(snake.cells[i].r === cell.r && snake.cells[i].c === cell.c)
                    return false;
            }
        }
        return true;
    }

    next_step()
    {
        for(const snake of this.snakes)
            snake.next_step();
    }

    update(){
        this.update_size();
        if(this.check_ready())
        {
            this.next_step();
        }
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
