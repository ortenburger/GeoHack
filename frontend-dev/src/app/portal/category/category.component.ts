import { Component, OnInit, Input } from '@angular/core';
import { FormsModule ,FormBuilder, ReactiveFormsModule, FormGroup, Validators } from '@angular/forms';
import { GeoportalApiService } from '../../services/geoportal-api.service';
import { LogService } from '../../services/log.service';
import { AuthService } from '../../services/auth.service';
import { CacheService } from '../../services/cache.service';
import { Category } from '../../models/Category';
import { isArray } from 'util';
import { OsmNode } from '../../models/OsmNode';
//import { FormsModule ,FormBuilder, ReactiveFormsModule, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-category',
  templateUrl: './category.component.html',
  styleUrls: ['./category.component.css']
})
export class CategoryComponent implements OnInit {
  @Input('node') node : OsmNode;
  @Input('form') form : FormGroup;
  //@Input('form') form : FormGroup;
  categories : Array<Category>;
  sharingCollapsed = true;
  sustCollapsed = true;
  
  constructor(private apiService: GeoportalApiService, private logger: LogService, private authService: AuthService, private cache: CacheService) { 
    this.categories = new Array<Category>();
  }
  ngOnInit() {
    
    if(this.cache.has("categories")){
      this.logger.debug("returning cached version.");
      this.categories = this.cache.get("categories");
    }else{
    this.logger.debug("Reading categories from server.");
    this.apiService.getCategories().subscribe(
      (response)=>{
        if(isArray(response)){
          this.cache.set("categories",response);
        for(const element of response ){
            let cat: Category = new Category(element);
            this.categories.push(cat);
          }
        }else{
          this.logger.debug("result is not an array.");
        }
      },
      (error)=>{
        this.logger.debug( "Failed to get categories from server." +JSON.stringify(error));
      });
    }
  }

}

