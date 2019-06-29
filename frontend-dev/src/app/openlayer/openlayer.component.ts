import { Component, OnInit } from '@angular/core';

import { MapService } from '../services/map.service';
import { GeoportalApiService } from '../services/geoportal-api.service';
import { Rectangle } from '../models/Rectangle';
import { OsmNode } from '../models/OsmNode';
import { BliDimension }  from '../models/BliDimension'; 
import { OsmNodeFactory }  from '../models/OsmNodeFactory';

import { BliDimensionFactory }  from '../models/BliDimensionFactory'; 
@Component({
  selector: 'app-openlayer',
  templateUrl: './openlayer.component.html',
  styleUrls: ['./openlayer.component.css']
})
export class OpenlayerComponent implements OnInit {

    constructor(private mapService:MapService,private apiService: GeoportalApiService){
    }

	zoomIn(){
		this.mapService.zoomIn();
	}
	zoomOut(){
		this.mapService.zoomOut();
	}
  ngOnInit() {
   this.mapService.initMap();
   let bbox = new Rectangle(this.mapService.getBoundingBox(null));
   this.apiService.getGeoElementsByBoundingBox(bbox).subscribe(
    (response)=>{
            let nodes = new Array<OsmNode>();
            for(let data of response){
                nodes.push(OsmNodeFactory.create(data));
            }
            this.mapService.showNodes(nodes);
            this.mapService.showConnections();
        },
    (error)=>{
            console.log("error: "+JSON.stringify(error));
        }
   );
  }
}
