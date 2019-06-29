import { Component, OnInit } from '@angular/core';
import { DatePipe } from '@angular/common';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { OverpassService } from '../services/overpass.service';
import { GeoportalApiService } from '../services/geoportal-api.service';
import { BliService } from '../services/bli.service';
import { GeoElementService } from '../services/geo-element.service';
import { LogService } from '../services/log.service';
import { MapService } from '../services/map.service';
import { DataGroup } from '../models/DataGroup';
import { Rectangle } from '../models/Rectangle';
import { DataGroupFactory } from '../models/DataGroupFactory';
import { OsmNode } from '../models/OsmNode';
import { BliDimension } from '../models/BliDimension';
import { OsmNodeFactory } from '../models/OsmNodeFactory';
import { OsmNodeService } from '../services/osm-node.service';
import { BliComponent }  from '../portal/bli/bli.component';
import { NominatimService } from '../services/nominatim.service'
import { AuthService } from '../services/auth.service';
import { registerLocaleData } from '@angular/common';
import localeDe from '@angular/common/locales/de';

import OlMap from 'ol/Map';
import OlView from 'ol/View';
import { NgxSpinnerService } from 'ngx-spinner';
import { NodeFilterPipe } from '../pipes/node-filter.pipe';
@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css']
})

export class SearchComponent implements OnInit { 
  hasResults = false;
  searched = false;
  state = 'visible';
  searchForm : FormGroup;
  searchPattern = "";
  allOsmNodes : Array<OsmNode>;
  osmNodes : Array<OsmNode>;
  nominatimNodes : Array<OsmNode>;
  dataGroups : Array<DataGroup>;
  currentElement : OsmNode;
  useFilter : boolean=true;
  showAll:boolean;
  dimensionsFilterArray : Array<BliDimension>;
  filterPipe = new NodeFilterPipe();
  sharingCollapsed = true;
  descCollapsed = true;
  constructor(
    private nodeService : OsmNodeService,
    private formBuilder : FormBuilder,
    private logger : LogService,
    private apiService : GeoportalApiService,
    private overpass : OverpassService,
    private geoElementService : GeoElementService,
    private mapService : MapService,
    private authService : AuthService,
    private spinner : NgxSpinnerService,
    private nominatimService : NominatimService,
    private router : Router
    ) {
   
    registerLocaleData(localeDe, 'de');
    this.showAll = true;
    this.dimensionsFilterArray=new Array<BliDimension>();
    this.osmNodes = new Array<OsmNode>();
    this.nominatimNodes = new Array<OsmNode>();
    this.allOsmNodes = new Array<OsmNode>();
    this.dataGroups = new Array<DataGroup>();
    this.searchForm = this.formBuilder.group({
      searchPattern: ['', Validators.required],
      searchInDescription: ['',]
    });
    this.logger.debug("search.component loaded.");
    if(this.authService.isLoggedIn()){
      this.logger.debug("user is logged  in");
    }else{
      this.logger.debug("user is not logged in.");
    }


  }
  termChanged(event){
  	console.log("Event: "+event);
    
  }
  show(element: OsmNode){
    this.nodeService.showDetails(element);
    this.logger.debug("Showing details for ");
    this.logger.debug(element.debugString());
    this.nodeService.showDetailsWidget();
    this.logger.debug("Showing all "+this.osmNodes.length + " nodes from searchresult");
    //    this.mapService.showNodes(this.osmNodes,'searchResult');
    this.mapService.show(element,'detailView');
    this.mapService.goto(element.getLon(),element.getLat());
  }

  showOsmElement(element: OsmNode){
    this.logger.debug("Showing: "+element.debugString());
    this.nodeService.showDetails(element);
    this.nodeService.showDetailsWidget();
    this.logger.debug("Showing all "+this.osmNodes.length + " nodes from searchresult");
    //    this.mapService.showNodes(this.osmNodes,'searchResult');
    this.mapService.show(element,'detailView');
  }
  filterChanged(dimensions: Array<BliDimension>){
    console.log("Filter changed. Size: "+dimensions.length + " Enabled: "+(this.useFilter?"yes":"false"));
  }


  toggleAllPlaces(value:any){
    this.logger.debug("search.component.ts::toggleAllPlaces() called.");
    
    if(value.target.checked){
      this.logger.debug("ToggleAllPlaces: off");
      
      this.showBoundingBoxElements();
    }else{
      this.logger.debug("ToggleAllPlaces: off");
      this.mapService.removeSet('default');
    }
  }

  showBoundingBoxElements(){
   var bboxNodes = new Array<OsmNode>();
   let bbox = new Rectangle(this.mapService.getBoundingBox(null));
   this.apiService.getGeoElementsByBoundingBox(bbox).subscribe(
    (response)=>{
      
      for(let data of response){
        bboxNodes.push(OsmNodeFactory.create(data));
      }
      let res = this.osmNodes;
      
      res.forEach(function(el){
        let idx=bboxNodes.indexOf(el);
        if(idx === -1){
          return;
        }
        bboxNodes.splice(idx,1);
      });
      this.mapService.showNodes(bboxNodes,'default');
      this.mapService.showConnections();
    },
    (error)=>{
      console.log("error: "+JSON.stringify(error));
    }
    );
 }

 translateTagValue(value:string){
  switch(value){
    default:{
      return value;
    }
  }
}

translateTagKey(key:string){
  switch(key){

    case 'contact:website':{
      return 'Homepage';
    }
    case 'addr:street':{
      return 'Straße';
    }
    case 'addr:housename':{
      return 'Gebäudename';
    }
    case 'addr:housenumber':{
      return 'Hausnr.';
    }
    case 'addr:country':{
      return 'Land';
    }
    case 'addr:postcode':{
      return 'PLZ';
    }
    case 'addr:city':{
      return 'Stadt';
    }
    case 'start_date':{
      return 'Gründung';
    }
    case '_gpd:organisationsForm':{
      return 'Organisations-form';
    }
    case '_gpd:ehrenamtliche':{
      return 'Ehrenamtliche Mitarbeiter';
    }
    case '_gpd:hauptamtliche':{
      return 'Hauptamtliche Mitarbeiter';
    }
    case 'source':{
      return 'Website';
    }
    case 'operator:type':{
      return 'Rechtsform';
    }
    case 'description':{
     return 'Beschreibung';
   }
   case 'name':{
     return 'Name';
   }
   case 'office':{
     return 'Organisation';
   } 
   case 'opening_hours':{
     return 'Öffnungszeiten';
   }
   case '_gpd:aktionsradius':{
    return 'Aktionsradius';
  }
  case '_gpd:welfare':{
    return 'Gemeinnützigkeit';
  }
	case '_gpd:sharing_offers':{
	    return 'Angebote des Teilen und Schenkens';
	  }
	case '_gpd:sharing_organisator':{
	    return 'Organisator*in';
	  }
	case '_gpd:sharing_free_offers':{
	    return 'Kostenfreie Angebote';
	  }
	case '_gpd:sharing_nonfree_offers':{
	    return 'Kostenpflichtige Angebote';
	  }
	case '_gpd:sustainable_nutrition':{
	    return 'Angebote zum Thema nachhaltiges Ernährung';
	  }
	case '_gpd:sustainable_nutrition_assortment':{
	    return 'Sortiment';
	  }
  default:{
    return key;
  } 
}
}

showGoodLife(){
  if(this.nodeService.detailView == null){
    return false;
  }
	let desc = this.nodeService.detailView.getDescription();
	return (desc != undefined && desc != null && desc != "" ) || (this.nodeService.detailView.dimensions.length > 0);
}

showSustainableNutrtition(){
  if(this.nodeService.detailView == null){
    return false;
  }
  let tags = [ 'sustainable_nutrition','sustainable_nutrition_assortment'];
  for(let tag of tags){
    if(this.nodeService.detailView.hasTag("_gpd:"+tag)){
      return true;
    }
  }
  return false;
}
showSharing(){
  if(this.nodeService.detailView == null){
    return false;
  }
  let tags = [ 'sharing_offers','sharing_free_offers','sharing_nonfree_offers','sharing_organisator'];
  for(let tag of tags){
    if(this.nodeService.detailView.hasTag("_gpd:"+tag)){
      return true;
    }
  }
  return false;
}
parseSearchResult(result){
  this.showAll = false;
  this.logger.debug("ParseSearchresults.");
  this.searched = true;
  this.nodeService.hideDetails();
  this.nodeService.hideDetailsWidget();
  this.osmNodes.length = 0;
  this.allOsmNodes.length = 0;
  this.dataGroups.length = 0;
  this.logger.debug("Search result.");
  if(result){
    if(result['dataGroups']){
      this.dataGroups = new Array<DataGroup>();
      for(let data of result['dataGroups']){
        this.dataGroups.push(DataGroupFactory.create(data));
      }
    }
    if(result['geoElements']){

      for(let data of result['geoElements']){
        let node = OsmNodeFactory.create(data);
        this.nodeService.fillBliDimensions(node);

        node.osmId = data['osmId'];
        if(this.searchForm.value.searchInDescription){
          this.logger.debug("Pushing anyway.");
          if(this.osmNodes.indexOf(node) == -1){
            this.osmNodes.push(node);
          }   
        }else{
          this.logger.debug(" for " + this.searchForm.value.searchPattern);
          if(node.name.toLowerCase().indexOf(this.searchForm.value.searchPattern.toLowerCase()) > -1){
            if(this.osmNodes.indexOf(node) == -1){
              this.osmNodes.push(node);
            }   
          }
        }
        if(this.allOsmNodes.indexOf(node) == -1){
          this.allOsmNodes.push(node);
        }

      }
    }
    this.spinner.hide();
    if(this.osmNodes.length > 0){
      this.mapService.showNodes(this.osmNodes,'searchResult');
      this.mapService.fitView();
    }
  }
  this.hasResults = (this.osmNodes.length > 0 || this.nominatimNodes.length > 0 || this.dataGroups.length > 0);
  this.logger.debug("searched: "+(this.searched?"yes":"no"));
  this.logger.debug("hasResults: "+(this.hasResults?"yes":"no"));
  this.logger.debug("osmNodes: "+this.osmNodes.length);
  this.logger.debug("dataGroups: "+this.dataGroups.length);
  this.logger.debug("nominatimNodes: "+this.nominatimNodes.length);
}

edit(node: OsmNode){
  this.nodeService.editElement(node)
  if(node.knownEntity()){

    this.logger.debug("redirecting to entryedit.");
    if(!this.authService.isLoggedIn()){
      this.router.navigate(["login",{redirectTo:"entryedit",id:node.id}])
      return;
    }
    this.router.navigate(['entryedit',{id:node.id}]);
  }else{
    this.logger.debug("redirecting to entrycreate.");
      node.removeName();
      if(!this.authService.isLoggedIn()){
      
        this.router.navigate(["login",{redirectTo:"entrycreate",id:node.getOsmId(),type:node.getOsmType()}]);
        return;
      }

      this.router.navigate(['entrycreate',{id:node.getOsmId(),type:node.getOsmType()}]);
      return;
  }
}

parseNominatimResult(response){
  this.logger.debug("Parsing Nominatim results.");
  this.nominatimNodes.length = 0;
  console.log(response.length + " nominatim results.");
  for(let item of response){
    let nodeData={
      osmId:item['osm_id'],
      lon:parseFloat(item['lon']),
      lat:parseFloat(item['lat']),
      type:item['osm_type'],
      name:item['display_name'],
      id:undefined
    };
    this.logger.debug("Nominatim-Result:"+item['display_name']+" "+item['osm_id']);
    let node = OsmNodeFactory.create(nodeData);
    this.logger.debug("Now:"+node.name + " " + node.osmId);
    this.nominatimNodes.push(node);
    this.allOsmNodes.push(node);

    this.hasResults = (this.osmNodes.length > 0 || this.nominatimNodes.length > 0 || this.dataGroups.length > 0);
  }
}

parseError(error){
  this.spinner.hide();
}    

search(){
  let pattern:string=this.searchForm.value.searchPattern;
  this.mapService.removeNodes('peers');
  this.mapService.removeNodes('detailView');
  if(pattern.length < 3){
    return;
  }
  this.logger.debug("Searching for "+JSON.stringify(this.searchForm.value.searchPattern));
  this.hasResults = false;
  //this.spinner.show();
  this.apiService.searchGeoElements(this.searchForm.value.searchPattern).subscribe((response)=>{this.parseSearchResult(response);},(error)=>{this.parseError(error);});
  this.nominatimService.search(this.searchForm.value.searchPattern).subscribe((response)=>{this.parseNominatimResult(response)},(error)=>{console.log("error reading from nominatim");});
}    

ngOnInit() {
  this.nominatimNodes.length = 0;
  this.osmNodes.length = 0;
  this.nodeService.showDetails(null);
}

clearResults(){
  this.logger.debug("clearing results.");

  this.searchForm.reset();
  this.osmNodes.length = 0;
  this.dataGroups.length = 0;
  this.allOsmNodes.length = 0;
  this.nominatimNodes.length = 0;
  this.hasResults = false;
  this.searched = false;
  this.hasResults = (this.allOsmNodes.length > 0 || this.dataGroups.length > 0);

}


  /**
    Sichtbarkeit der Widgets 
    **/
    toggleVisibility(){
      if(this.state=="hidden"){
        this.state="visible";
      }else{
        this.state="hidden";
      }
    }


    togglePeerVisibility(peer: OsmNode){
      //this.logger.debug("toggling visibility of peer "+peer.name);
      this.mapService.togglePeerVisibility(this.nodeService.detailView,peer);
    }
    detailsState():string{
      return this.nodeService.detailsWidgetState;
    }

    toggleVisibilityDetails(){
      return this.nodeService.toggleDetailsStateWidget();
    }
    isCurrentElement(node:OsmNode){

      if(this.nodeService.detailView==null){
        return false;
      }

      let equals = (this.nodeService.detailView.mapId == node.mapId);
      return equals;
    }


  }


