import { GameObject } from "./GameObject";
import { Cell } from "./Cell";

export class Snake extends GameObject{
    constructor(info,gamemap)
    {
        super();

        this.id = info.id;
        this.color = info.color;
        this.gamemap = gamemap;

        this.cells = [new Cell(info.r,info.c)]; //存放蛇身，cells[0]放蛇头
        this.speed = 5; //每秒移动格子数
        
        this.next_cell = null; //下一步的目标位置
        this.direction = -1; //-1表示没有指令，0,1,2,3分别表示上右下左
        this.status = "idle"; // idle表示静止，move表示正在移动，die表示死亡

        this.dr = [-1,0,1,0]; //四个方向横坐标偏移量
        this.dc = [0,1,0,-1]; //四个方向纵坐标偏移量

        this.step = 0; //表示当前回合数
        this.eps = 1e-2; //当next_cell和当前蛇头距离小于eps时，认为二者重合

        this.eye_direction = 0;
        if(this.id === 1) this.eye_direction = 2;//左下角蛇头朝上，右上角蛇头朝下
        this.eye_dx = [ //眼睛相对于蛇头圆心的x的偏移量
            [-1,1],
            [1,1],
            [1,-1],
            [-1,-1],
        ];
        this.eye_dy = [ //眼睛相对于蛇头圆心的y的偏移量
            [-1,-1],
            [-1,1],
            [1,1],
            [1,-1],
        ]
    }
    start()
    {

    }

    set_direction(d)
    {
        this.direction = d;
    }

    next_step(){
        const d = this.direction;
        this.next_cell = new Cell(this.cells[0].r + this.dr[d], this.cells[0].c + this.dc[d]);
        this.eye_direction = d;
        //清空
        this.direction = -1;
        this.status = "move";
        this.step ++;

        const k = this.cells.length;
        for(let i = k; i > 0; i --)
        {
            this.cells[i] = JSON.parse(JSON.stringify(this.cells[i-1]));
        }
    }

    check_tail_increasing(){
        if(this.step <= 10) return true;
        if(this.step % 3 === 1) return true;
        return false;
    } 

    update_move(){
        const dx = this.next_cell.x - this.cells[0].x;
        const dy = this.next_cell.y - this.cells[0].y;
        //总的要走的距离
        const distance = Math.sqrt(dx*dx + dy*dy);

        if(distance < this.eps)
        {   
            this.cells[0] = this.next_cell;
            this.next_cell = null;
            this.status = "idle";

            if(!this.check_tail_increasing())
            {
                this.cells.pop();
            }
        }else{
            const move_distance = this.speed * this.timedelta / 1000; //每两帧之间，要走的距离
            this.cells[0].x += move_distance * dx / distance;
            this.cells[0].y += move_distance * dy / distance;

            if(!this.check_tail_increasing())
            {
                const k = this.cells.length;
                const tail = this.cells[k-1], target_tail = this.cells[k - 2];
                const tail_dx = target_tail.x - tail.x;
                const tail_dy = target_tail.y - tail.y;

                tail.x += move_distance * tail_dx / distance;
                tail.y += move_distance * tail_dy / distance;
            }
        }
    }

    update()
    {   
        if(this.status === 'move') 
        {
            this.update_move();
        }
        this.render();
    }

    render(){
        const L = this.gamemap.L;
        const ctx = this.gamemap.ctx;

        ctx.fillStyle = this.color;
        if(this.status === "die")
            ctx.fillStyle = "white";
        for(const cell of this.cells)
        {
            ctx.beginPath();
            ctx.arc(cell.x*L,cell.y*L,L/2 * 0.8,0,Math.PI*2);
            ctx.fill();
        }

        for(let i = 1; i < this.cells.length; i ++)
        {
            const a = this.cells[i-1] , b = this.cells[i];
            //当尾部距离目标很近时，就不用画长方形了
            if(Math.abs(a.x - b.x) < this.eps && Math.abs(a.y - b.y) < this.eps)
                continue;
            if(Math.abs(a.x - b.x) < this.eps) //竖直情况
            {
                ctx.fillRect((a.x - 0.4) * L, Math.min(a.y,b.y)*L,L*0.8,Math.abs(a.y - b.y)*L);
            }else{ //水平情况
                ctx.fillRect(Math.min(a.x,b.x)*L,(a.y - 0.4)*L,Math.abs(a.x-b.x)*L,L*0.8);
            }
        }

        ctx.fillStyle = "black";
        for(let i = 0; i < 2; i ++)
        {
            const eye_x = (this.cells[0].x + this.eye_dx[this.eye_direction][i] * 0.15) * L;
            const eye_y = (this.cells[0].y + this.eye_dy[this.eye_direction][i] * 0.15) * L;

            ctx.beginPath();
            ctx.arc(eye_x,eye_y,L*0.05,0,Math.PI*2);
            ctx.fill();
        }
    }
}