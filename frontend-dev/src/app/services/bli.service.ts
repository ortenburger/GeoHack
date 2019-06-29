import { Injectable } from '@angular/core';
import { GeoportalApiService } from './geoportal-api.service';
import { LogService } from './log.service';
import { CacheService } from './cache.service';
import { AuthService } from './auth.service';
import { BliDimension } from '../models/BliDimension';
import { BliDimensionFactory } from '../models/BliDimensionFactory';
import { isArray } from 'util';

@Injectable({
  providedIn: 'root'
})
export class BliService {
	dimensions: Array<BliDimension>;
  constructor(
  private logger: LogService,
  private authService: AuthService,
  private apiService: GeoportalApiService,
  private cache: CacheService) {

  	this.dimensions=BliDimensionFactory.dimensions;
    this.logger.debug("BliService constructed.");
    this.getFromServer();
  }

    getIconClass(name){
        switch(name){
          case 'Einkommen':{return 'bliicon bliicon-income';}
          case 'Arbeit':{return 'bliicon bliicon-work';}
          case 'Wohnbedingungen':{return 'bliicon bliicon-housing';}
          case 'Gesundheit':{return 'bliicon bliicon-health';}
          case 'Work-Life-Balance':{return 'bliicon  bliicon-wlbalance';}
          case 'Bildung':{return 'bliicon bliicon-education';}
          case 'Gemeinschaft':{return 'bliicon bliicon-community';}
          case 'Engagement/Beteiligung':{return 'bliicon bliicon-engagement';}
          case 'Umwelt':{return 'bliicon bliicon-nature';}
          case 'Sicherheit':{return 'bliicon bliicon-security';}
          case 'Zufriedenheit':{return 'bliicon bliicon-satisfaction';}
          case 'Infrastruktur':{return 'bliicon bliicon-infrastructure';}
          case 'Freizeit und Kultur':{return 'bliicon bliicon-leisure';}
          default: return '';
       }
    }
    getIcon(name){
    switch(name){
      case 'Einkommen':{return 'fa fa-money';}
      case 'Arbeit':{return 'fa fa-briefcase';}
      case 'Wohnbedingungen':{return 'fa fa-home';}
      case 'Gesundheit':{return 'fa fa-medkit';}
      case 'Work-Life-Balance':{return 'fa fa-balance-scale';}
      case 'Bildung':{return 'fa fa-book';}
      case 'Gemeinschaft':{return 'fa fa-users';}
      case 'Engagement/Beteiligung':{return 'fa fa-check-square-o';}
      case 'Umwelt':{return 'fa fa-envira';}
      case 'Sicherheit':{return 'fa fa-shield';}
      case 'Zufriedenheit':{return 'fa fa-heart';}
      case 'Infrastruktur':{return 'fa fa-road';}
      case 'Freizeit und Kultur':{return 'fa fa-film';}
      default: return '';
    }
  }

  getFromServer() {
    let expires=new Date();
    expires.setDate(expires.getDate()+1);

    if(this.cache.has("blidimensions")){

      this.logger.debug("BliService: returning cached version.");
      this.parseResponse(this.cache.get("blidimensions"));
    }else{
    this.logger.debug("BliService: Reading bli-dimensions from server.");
    this.apiService.getBliDimensions().subscribe(
      (response)=>{
          this.cache.set("blidimensions",response,expires);
          this.parseResponse(response);
      },
      (error)=>{
        this.logger.debug( "BliService: Failed to get bli dimensions from server." +JSON.stringify(error));
        this.logger.notify('error','failed to read bli-dimensions');
      });
    }
   }

   parseResponse(response:any){
        this.logger.debug("BliService: parsing response.");

        this.logger.debug(JSON.stringify(response));
        if(isArray(response)){
          this.dimensions.length=0;
          for(let element of response ){
            BliDimensionFactory.create(element);
          }
        }else{
          this.logger.debug("BliService: result is not an array.");
        }
       this.logger.debug("response parsed.");
  }

}
