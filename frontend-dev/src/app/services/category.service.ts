import { Injectable } from '@angular/core';
import { Category } from '../models/Category';
import { DataGroup } from '../models/DataGroup';
import { OsmNode } from '../models/OsmNode';
import { CacheService } from './cache.service';
import { GeoportalApiService } from './geoportal-api.service';
import { LogService } from './log.service';
import { isArray }  from 'util';
@Injectable({
  providedIn: 'root'
})
export class CategoryService {
  
  categories: Array<Category>;
  currentRectangle="...";
  
  constructor(private apiService: GeoportalApiService, private cache: CacheService, private logger: LogService) {
    this.logger.debug("CategoryService: instantiating.");
    this.categories = new Array<Category>();
    this.readCategories();
   }
  
  readCategories(){
      this.apiService.getCategories().subscribe(
        (response)=>{
            
          this.parseResponse(response);
        },
        (error)=>{
          this.parseError(error);
        });
    
  }
  
  parseResponse(response:any){
    this.logger.debug("CategoryService: Received categories:");
    
   	if(isArray(response)){
   	    for(let data of response){
           let category : Category = new Category(data);      
           this.categories.push(category);
        }
   	} 
    
  }
  parseError(error:any){
    this.logger.debug("CategoryService: error parsing/getting data from server.");
  }
  
}
