import { BliDimension }  from './BliDimension';
export class BliDimensionFactory{
    static dimensions : Array<BliDimension>;    
    static init(){

        BliDimensionFactory.dimensions = new Array<BliDimension>();
    }
    static getDimensionById(id:number){
        for(let dim of BliDimensionFactory.dimensions){
            if(dim.id == id){
                return dim;
            }
        }
    }
    static add(dim:BliDimension){
        
        for(let d of BliDimensionFactory.dimensions){
            if(d.id == dim.id || d.name == dim.name){
                console.log("Warning: Dimension " +dim.name + " already exists. (" + d.id+":"+d.name +"/"+dim.id+":"+dim.name +")");
                return;
            }
        }
        BliDimensionFactory.dimensions.push(dim);
    }
    static create(data:any) : BliDimension{
        
        if(data instanceof BliDimension){
            let dim = BliDimensionFactory.getDimensionById(data.id);
            if(dim === null){
                BliDimensionFactory.add(data);
                return data;
            }else{
                let existing = BliDimensionFactory.getDimensionById(data.id);
                existing.update(data);
                return;
            }
        }
        let id:number;
        
        if(data['id']){
            id=data['id']*1;
        }else{
            if(!isNaN(parseFloat(data)) && !isNaN(data - 0)){
                id=data*1;
            }else{
                return null;
            }
        }
        
        let dim = BliDimensionFactory.getDimensionById(id);
        if(dim != null){
            dim.update(data);
            return dim;
        }else{
            dim = new BliDimension(data);
            if(dim.isValid()){
                BliDimensionFactory.add(dim);
                return dim;
            }else{
                return null;
            }
        }
            
        
        
    }
}

BliDimensionFactory.init();