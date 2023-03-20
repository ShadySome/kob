const GAME_OBJECTS = [];

export class GameObject{
    constructor(){
        GAME_OBJECTS.push(this);
        this.timedelta = 0;
        this.has_called_start = false;
    }
    //只执行一次
    start(){

    }
    //每一帧执行一次， 除了第一帧
    update()
    {

    }
    //销毁前执行
    on_destroy()
    {

    }
    //删除
    destroy()
    {
        this.on_destroy();
        for(let i in GAME_OBJECTS)
        {
            let obj = GAME_OBJECTS[i];
            if(this ==  obj) 
                GAME_OBJECTS.splice(i);
                break;
        }
    }
}
let last_timestamp;
const step = timestamp =>{
    for(let obj of GAME_OBJECTS)
    {
        if(!obj.has_called_start)
        {
            obj.has_called_start = true;
            obj.start();
        }
        else{
            obj.timedelta = timestamp - last_timestamp;
            obj.update();
        }
    }
    last_timestamp = timestamp;
    requestAnimationFrame(step);
}

requestAnimationFrame(step);
