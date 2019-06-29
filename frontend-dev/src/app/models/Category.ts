import { DataGroup } from './DataGroup';
import { DataGroupFactory } from './DataGroupFactory';
import { KeyValuePair } from './KeyValuePair'
export class Category {
  name: string;
  description: string;
  id: number;
  displayName: string;
  suggestedKeys:Array<KeyValuePair>;
  dataGroups: Array<DataGroup>;
  constructor(data:any){
    this.dataGroups = new Array<DataGroup>();
    this.suggestedKeys = new Array<KeyValuePair>();
    this.setData(data);
  }
  setData(data:any):void{
    if(data['id']){
      this.id=data['id'];
    }
    if(data['description']){
      this.description=data['description'];
    }
    if(data['name']){
      this.name=data['name'];
    }
    if(data['displayName']){
      this.displayName = data['displayName'];
    }
    if(data['suggestedKeys']){
      for(let kvpData of data['suggestedKeys']){
        let kvp = new KeyValuePair(kvpData);
        this.suggestedKeys.push(kvp);
      }
    }
    if(data['dataGroups']){
        for(let element of data['dataGroups']){
            if(element){
                let dg = DataGroupFactory.create(element);
                if(dg){
                    this.dataGroups.push(dg);
                }
            }
        }
    }
  } 

}