export class Cell{
    constructor(r,c)
    {
        this.r = r;
        this.c = c;
        //圆心点
        this.x = c + 0.5;
        this.y = r + 0.5;
    }
}