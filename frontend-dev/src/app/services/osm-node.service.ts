import { Injectable } from '@angular/core';
import { OsmNode } from '../models/OsmNode';
import { OsmNodeFactory } from '../models/OsmNodeFactory';
import { LogService } from './log.service';
import { GeoportalApiService } from './geoportal-api.service';
import { AuthService } from './auth.service';
import { BliService } from './bli.service';
import { BliDimension } from '../models/BliDimension';
import { OverpassService } from './overpass.service';
import { GeoElementService } from './geo-element.service';
import { CacheService } from './cache.service';
import { CategoryService } from './category.service';
import { isArray } from 'util';

@Injectable({
  providedIn: 'root'
})
export class OsmNodeService {
	detailView:OsmNode;
    elementToEdit:OsmNode;
    message: string;
    result: Array<OsmNode> = null;
    dimensions : Array<BliDimension>;
    detailsWidgetState : string = "hidden";
  constructor(
        private logger: LogService,
        private apiService: GeoportalApiService,
        private authService: AuthService,
        private overpass: OverpassService,
        private cache: CacheService,
        private bliService: BliService,
        private categoryService: CategoryService,
        private nodeService: OsmNodeService,
        private geoElementService: GeoElementService) {
			this.result = new Array<OsmNode>();
            this.dimensions = bliService.dimensions;
            this.detailView = null;
		}

  /**
    Detail-View-Sichtbarkeit
  **/
  toggleDetailsStateWidget(){
    if(this.detailsWidgetState=="hidden"){
      this.detailsWidgetState="visible";
    }else{
      this.detailsWidgetState="hidden";
    }
  }
  hideDetailsWidget(){
    this.detailsWidgetState = 'hidden';
  }
  showDetailsWidget(){
    this.detailsWidgetState = 'visible';
  }
    updateElementFromServer(item: OsmNode){
        if(item.id != -1 && item.id != undefined){
            this.apiService.getGeoElementById(item.id).subscribe(
            (response)=>{
                OsmNodeFactory.create(response);
            },
            (error)=>{
            });
        }
        if(item.osmId && item.osmType){
            this.apiService.getGeoElementByOsmId(item).subscribe(
            (response)=>{
                OsmNodeFactory.create(response);
            },
            (error)=>{
            });
        }
    }

    showDetails(node:OsmNode){

        this.detailView = node;
        if(node != null){
          this.overpass.getOsmTags(node);
        }else{
          this.debug("Warning: showDetails called with null");
        }
        this.showDetailsWidget();

        if(node){
            if(!node.hasCoordinates()){
              this.fillNode(node);

            }
            this.updateElementFromServer(node);
        }
    }
    
    hideDetails(){
        this.detailView = null;
    }
    
    editElement(node:OsmNode){
        this.elementToEdit=node;
        if(node){
            this.updateElementFromServer(node);
        }
    }
    
    parseResponse(response:any){
      this.logger.debug("OsmNodeService: parseResponse");
      this.result.length = 0;
      this.logger.debug("OsmNodeService: parseResponse");
      this.message = JSON.stringify(response);
          if(response['elements']){
            let elements = response['elements'];
            if(isArray(elements)){
              for(const element of elements){
                let osmNode = OsmNodeFactory.create(element);
                if(osmNode.isValid()){
                  this.checkServerForElement(osmNode);
                  this.result.push(osmNode);
                  this.logger.debug("OsmNodeService: adding element "+element['id']);
                }
                
              }
            }else{
              this.logger.debug("OsmNodeService: exepcted property \"elements\" not found in response.");
            }
          }
    }
    
    save(node: OsmNode){
        this.logger.debug("OsmNodeService: saving element "+node.name);
        if(!node.knownEntity()){
            this.geoElementService.save(node);
        }else{
            this.logger.debug("OsmNodeService send item to server: "+node.id);
            this.geoElementService.update(node);
        }
    }
	
	
  fillBliDimensions(node: OsmNode){
	for(let bli of node.dimensions){
		for(let dim of this.bliService.dimensions){
			if(dim.id == bli.id){
				bli.name = dim.name;
				bli.description = dim.description;
			}
		}
	}
  }
    

  checkServerForElement(node:OsmNode){
    this.apiService.getGeoportalElementByOsmId(node.osmId).subscribe(
        (response)=>{
            this.logger.debug("Got element from server to pad existing element with:");
            let existingNode=OsmNodeFactory.create(response);
            node.mode="edit";
        },
        (error)=>{
          this.logger.debug(JSON.stringify(error));
        });
    }
  
    getIncompleteNodes(node:OsmNode): Array<number>{
        
        let incompleteNodes = new Array<number>();
        if(node.osmType == "node"){
            if(!node.hasCoordinates()){
                incompleteNodes.push(node.osmId);
            }
        }
        if(node.osmType == "way"){
                for(let subnode of node.nodes){
                        //this.logger.debug("looking at "+subnode.osmId);
                        let tmp = this.getIncompleteNodes(subnode);
                        
                        for(let id of tmp){
                            incompleteNodes.push(id);
                        }
                }
        }
        return incompleteNodes;
    }
	
    fillCurrentNode(data:any,targetNode:OsmNode){
        if(data['elements']){
            for(let element of data['elements']){
                let node = OsmNodeFactory.create(element);
                let child = targetNode.getChild(node.osmId);
                if(child){
                    //this.logger.debug("updating coordinates of "+child.osmId + " to "+node.lon + "/"+node.lat);
                    child.lon = node.lon;
                    child.lat = node.lat;
                }else{
                   //this.logger.debug("child not found: "+node.osmId);
                }
            }    
        }
        
        targetNode.setCoordinatesFromChilds();
        this.logger.debug("OsmNodeService: Node after filling:");
        this.logger.debug(JSON.stringify(targetNode));
    }
	
	/**
		Node auffüllen mit daten aus overpass.
	**/
    fillNode(node: OsmNode){
        
        let incompleteNodes = this.getIncompleteNodes(node);
        let cacheIdentifier = "nodequery";
        for(let id of incompleteNodes){
            cacheIdentifier += ""+id;
        }
        
        if(this.cache.has(cacheIdentifier)){
           let data = this.cache.get(cacheIdentifier);
           if(data){
            this.logger.debug("OsmNodeService: got data from cache.");  
            this.fillCurrentNode(data,node);
            return;
           }
        }
        
        this.overpass.getNodesByIdArray(incompleteNodes).subscribe(
        (response)=>{
            this.cache.set(cacheIdentifier,response);
            this.logger.debug("filling node.");
        },
        (error)=>{
            this.logger.debug("OsmNodeService: error getting incomplete nodes.");
        });
        
    }
	
    getOSMElementById(type: string,id:number){
      this.logger.debug("OsmNodeService: getting osm element.");
      let cacheIdentifier = 'overpassItemId:'+type+id;
      this.logger.debug("OsmNodeService: checking cache.");
      if(this.cache.has(cacheIdentifier)){
        
        this.parseResponse(this.cache.get(cacheIdentifier));
        this.logger.debug("OsmNodeService: returning cached response for item "+cacheIdentifier);
        return;
      }
      if(type == 'way'){
        this.logger.debug("OsmNodeService: hitting server for "+cacheIdentifier);
      this.overpass.getWayById(id).subscribe(
        (response) => {
          this.cache.set(cacheIdentifier,response);
          this.logger.debug("OsmNodeService: caching and parsing response.");
          this.logger.debug("OsmNodeService: Overpass-Response: "+JSON.stringify(response,null,3));
          this.parseResponse( response );
        },
        (error) => {
          this.message = JSON.stringify( error );
        }
      );
    }
      if(type == 'node' ){
      this.logger.debug("OsmNodeService: hitting server for "+cacheIdentifier);
      this.overpass.getNodeById(id).subscribe(
        (response) => {
          this.cache.set(cacheIdentifier,response);
          this.logger.debug("OsmNodeService: caching and parsing response.");
          this.parseResponse( response );
        },
        (error) => {
          this.message = JSON.stringify( error );
        }
      );
      }
  }
  debug(msg: string){
    this.logger.debug("OsmNodeService: "+msg);
  }
  
}
