import { Category } from './Category';
import { DataGroupFactory } from './DataGroupFactory';
export class DataGroup {
  
  id: number;
  name: string;
  description: string;
  //categories: Array<Category>;
  dataGroups: Array<DataGroup>;
  constructor(data:any){
    //this.categories = new Array<Category>();
    this.dataGroups = new Array<DataGroup>();
    if(data instanceof DataGroup){
      this.copy(data);
      return;
    }else{
        this.setData(data);
    }
  }
  hasDataGroup(_dg: DataGroup):boolean{
      if(!this.dataGroups || this.dataGroups.length == 0){
          return false;
      }
      for(let dg of this.dataGroups){
          if(dg.id == _dg.id){
              return true;
          }
      }
      return false;
  }
  copy(obj: DataGroup){
    this.id = obj.id;
    this.name = obj.name;
    for(let dg of obj.dataGroups){
        if(!this.hasDataGroup(dg)){
            this.dataGroups.push(DataGroupFactory.create(dg));
        }
    }

  }
  setData(data:any){
    if(data['id']){
      this.id = parseInt(data['id']);
    }
    if(data['name']){
      this.name = data['name'];
    }
    if(data['description']){
      this.description = data['description'];
    }
     /*
    if(data['categories']){
      for(let item of data['categories']){
        this.categories.push(new Category(item));
      }
    }*/
        
    if(data['dataGroups']){
      for(let item of data['dataGroups']){
        let dg = DataGroupFactory.create(item);
        if(dg){
            this.dataGroups.push(dg);
        }
      }
    }
  }
}
