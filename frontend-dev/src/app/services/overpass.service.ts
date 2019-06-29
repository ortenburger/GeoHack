import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { LogService } from '../services/log.service';
import { CacheService } from '../services/cache.service';
import { OsmNode } from '../models/OsmNode';
import { KeyValuePair } from '../models/KeyValuePair';
import { map, tap, catchError} from 'rxjs/operators';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class OverpassService {
  private apiURL = "http://overpass-api.de/api/interpreter";
  //private apiURL="http://geoportal-api.transformationsstadt.de:8888/api/interpreter";
  constructor(private httpClient: HttpClient, private cache: CacheService, private logger: LogService) { }
  debug(msg:string){
    this.logger.debug("OverpassService: "+msg);
  }
    
  getNodesByIdArray(ids :Array<number>){
    this.debug("Getting array of nodes.");
    let query="";
    for(let id of ids){
        query += "node(" + id + ");";
    }
    
    return this.httpClient.get(this.apiURL + '?data=[out:json];(' + query + ');out;');
  }



  getOsmTags(node: OsmNode){
    if(node == null || node == undefined){
      this.debug("Node null or undefined. aborting");
      return;
    }
    if(node.hasOsmTags()){
      this.debug("Already has tags from osm. aborting.");
      return;
    }
    this.debug("Reading osm-tags from overpass.");
    let query="";
    switch(node.getOsmType()){
      case 'node':{
        query='node('+node.getOsmId()+');';
        break;
      }
      case 'way':{
        query='way('+node.getOsmId()+');';
        break;
      }
      case 'relation':{
        query='relation('+node.getOsmId()+');';
        break;
      }
      default:{
        this.debug("unsupported type: "+ node.getOsmType());
        return false;
      }
    }
    let TagWhiteList = new Array<string>();
    TagWhiteList.push('name');
    TagWhiteList.push('source');
    TagWhiteList.push('addr:country');
    TagWhiteList.push('addr:street');
    TagWhiteList.push('addr:housenumber');
    TagWhiteList.push('addr:postcode');
    TagWhiteList.push('addr:city');
    TagWhiteList.push('email');
    TagWhiteList.push('opening_hours');
    TagWhiteList.push('start_date');
    TagWhiteList.push('operator:type');
    this.httpClient.get(this.apiURL + '?data=[out:json];(' + query + ');out;').subscribe(
      (data)=>{
        if(data['elements'] && data['elements'].length == 1){
          if(data['elements'][0]['tags']){
              let tags = data['elements'][0]['tags'];
              node.clearOsmTags();
              for(let key in tags){
                let tag = new KeyValuePair({key:key,value:tags[key],source:"osm"});
                if(TagWhiteList.includes(key)){
                  node.addOsmTag(tag);
                }
                //this.debug("Adding tag: "+JSON.stringify(tag));
              }
          }else{
            //this.debug("no tags in this response: \n" +JSON.stringify(data,null,3));
          }
        }else{
          //this.debug("no elements in this response.");
        }
      },
      (error)=>{
        this.debug("Error fetching data from overpass.");
        this.debug(JSON.stringify(error,null,3));
        this.logger.notify('error','error fetching data from overpass.');
      }
    );
  }
  getNodeById(id:number){
    if(id==undefined || id==null){
      return;
    }
    let query="node(" + id + ");";
    this.debug("getting node: "+id);
    return this.httpClient.get(this.apiURL + '?data=[out:json];' + query + 'out;');
  }

  getWayById(id:number){
    if(id==undefined || id==null){
      return;
    }
    this.debug("getting way: "+id);
    let query="way(" + id + ");";
    return this.httpClient.get(this.apiURL + '?data=[out:json];' + query + 'out;');
  }

  getRelationById(id:number){
    if(id==undefined || id==null){
      return;
    }
    this.debug("getting way: "+id);
    let query="relation(" + id + ");";
    return this.httpClient.get(this.apiURL + '?data=[out:json];' + query + 'out;');
  }

}
