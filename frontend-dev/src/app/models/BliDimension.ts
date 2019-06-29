export class BliDimension{
  
  name: string;
  description: string;
  id: number;
  
  copy(data: BliDimension){
    this.name = data.name;
    this.description = data.description;
    this.id = data.id;
  }
  isValid(){
      if(this.id){
          return true;
      }
      return false;
  }
  constructor(data:any){
      
    if(data instanceof BliDimension){
          this.copy(data);
    }else{
        
          this.setData(data);
    }
    
  }
  update(data:any){
      this.setData(data);
  }
  setData(data:any):void{
    let id:number;
    if(data['id']){
        id=data['id']*1;    
    }else{
        if(!isNaN(parseFloat(data)) && !isNaN(data - 0)){
            id=data['id'];
        }else{
            return;
        }
    }
	if(data['id']){
      this.id=data['id'];
    }
	
    if(data['description']){
      this.description=data['description'];
    }
    if(data['name']){
      this.name=data['name'];
    }
      
  }  
}