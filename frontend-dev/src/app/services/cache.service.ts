import { Injectable } from '@angular/core';
import { LogService } from './log.service';
@Injectable({
  providedIn: 'root'
})
export class CacheService {

  expiration = 30;
  data : Array<CacheItem> = new Array<CacheItem>();
  constructor() {
    console.log("Constructing CacheService.");
    //localStorage.clear();
    let storageJSON=localStorage.getItem('CacheService.data');
    if(storageJSON){
        this.data=JSON.parse(storageJSON);
    }else{
        this.data= new Array<CacheItem>();
    }
   }
  // TODO: expiration
  get(item:string){
    for(let cacheItem of this.data){
          if(cacheItem.key == item){
            return JSON.parse(cacheItem.value);   
          }
      }
  return null;
  }
  // todo: persistence.
  save(){

  }
  set(key: string,value:any,expires?:Date){
      for(let cacheItem of this.data){
         if(cacheItem.key == key){
                cacheItem.value=JSON.stringify(value);
             if(expires){
                cacheItem.expires = expires;
             }else{
                cacheItem.expires.setSeconds(cacheItem.expires.getSeconds()+this.expiration);
             }
            return;
          }
       }
       this.data.push(new CacheItem(key,JSON.stringify(value),expires));
       localStorage.setItem('CacheService.data',JSON.stringify(this.data));
  }
    
    
  has(key: string): boolean{ 
     
     for(let cacheItem of this.data){
         if(cacheItem.key == key){
             console.log("cache hit for "+key);
             return true;
         }
    }
         console.log("cache miss for "+key);
     return false;
  }
  remove(key:string): boolean{
    let index :any = -1;
    if(this.has(key)){
        for(let i in this.data){
            if(this.data[i].key == key){
                index = i;
            }
        }
        if(index != -1){
            this.data.splice(index,1);
            localStorage.setItem('CacheService.data',JSON.stringify(this.data));
            return true;
        }
    }
    return false;
  }
}


export class CacheItem{
    private defaultExpireSeconds=30;
    constructor(public key:string,public value:string,public expires?: Date){
        if(!expires){
            this.expires = new Date();
            this.expires.setSeconds(this.expires.getSeconds()+this.defaultExpireSeconds);
        }
    }

}
