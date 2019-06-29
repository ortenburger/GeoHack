export class Rectangle{
    minX: number;
    maxX: number;
    minY: number;
    maxY: number;
    constructor(data: any){
        this.minX = parseFloat(data[0]);
        this.minY = parseFloat(data[1]);
        this.maxX = parseFloat(data[2]);
        this.maxY = parseFloat(data[3]);    
    }
}