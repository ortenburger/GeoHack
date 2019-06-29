import {DataGroup} from './DataGroup';
export class DataGroupFactory{
    
    static dataGroups: Array<DataGroup>;
    static init(){
        DataGroupFactory.dataGroups = new Array<DataGroup>();
    }
    public static create(data) : DataGroup{
        
        if(data instanceof DataGroup){
            let dg = DataGroupFactory.getById(data.id);
            if(dg != null){
                this.dataGroups.push(dg);
                return dg;
            }
        }
        if(typeof data == "number"){
            let dg = DataGroupFactory.getById(data);
            if(dg != null){
                return dg;
            }else{
                return null;
            }
        }     
        if(data['id']){
            let dg = DataGroupFactory.getById(parseInt(data['id']));
            if(dg != null){
                return dg;
            }
        }
        
        let dg = new DataGroup(data);
        DataGroupFactory.dataGroups.push(dg);
        return dg;
    }
    
    public static getById( id:number ) : DataGroup {
        for(let dg of DataGroupFactory.dataGroups){
            if(dg.id == id){
                return dg;
            }
        }
        return null;
    }
    

}
DataGroupFactory.init();