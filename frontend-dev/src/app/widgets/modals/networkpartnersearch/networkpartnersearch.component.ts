import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { FormControl } from '@angular/forms';
import { MarkdownService } from 'ngx-markdown';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { LogService } from '../../../services/log.service';
import { OsmNodeService } from '../../../services/osm-node.service';
import { ModalManagerService } from '../../../services/modal-manager.service';
import { NominatimService } from '../../../services/nominatim.service';
import { GeoportalApiService } from '../../../services/geoportal-api.service';
import { OsmNode } from '../../../models/OsmNode';
import { OsmNodeFactory } from '../../../models/OsmNodeFactory';

@Component({
	selector: 'app-networkpartnersearch',
	templateUrl: './networkpartnersearch.component.html',
	styleUrls: ['./networkpartnersearch.component.css']
})
export class NetworkpartnersearchComponent implements OnInit {
	@Input() node:OsmNode;
	@Input() asModal: boolean;
	@Output() close:EventEmitter<boolean> = new EventEmitter<boolean>();
	searchPattern:FormControl;
	constructor(
		private logger: LogService,
		private nominatimService:NominatimService,
		private apiService:GeoportalApiService,
		private nodeService:OsmNodeService
		) {
		this.searchPattern = new FormControl('');
	}

	search(){
		let pattern:string=this.searchPattern.value;
		if(pattern.length < 3){
			this.logger.notify('warning','Suchanfrage ist zu kurz (<3)');
			return;
		}
		this.logger.debug("Searching for "+JSON.stringify(this.searchPattern.value));
		this.apiService.searchGeoElements(this.searchPattern.value).subscribe(
			(response)=>{this.parseSearchResult(response);},
			(error)=>{this.parseError(error);});
		//this.nominatimService.search(this.searchForm.value.searchPattern).subscribe((response)=>{this.parseNominatimResult(response)},(error)=>{console.log("error reading from nominatim");});
	}    

	geoportalResultNodes: Array<OsmNode>;
	togglePeer(peer){
		if(this.node.hasPeer(peer)){
			this.node.removePeer(peer);
		}else{
			this.node.addPeer(peer);
		}
	}
	parseSearchResult(result){
		if(result){
			if(result['geoElements']){
				this.geoportalResultNodes = new Array<OsmNode>();
				for(let data of result['geoElements']){
					let node = OsmNodeFactory.create(data);
					this.nodeService.fillBliDimensions(node);
					node.osmId = data['osmId'];
					if(!this.node.hasPeer(node)){
						this.geoportalResultNodes.push(node);
					}
				}
			}

		}else{
			this.geoportalResultNodes.length=0;
		}
	}

	parseError(error){
	}    
	ngOnInit() {
		this.geoportalResultNodes= new Array<OsmNode>();
	}

}
