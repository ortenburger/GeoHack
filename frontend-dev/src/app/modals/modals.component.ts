import { Component, OnInit } from '@angular/core';
import { OsmNodeService } from '../services/osm-node.service';
@Component({
	selector: 'app-modals',
	templateUrl: './modals.component.html',
	styleUrls: ['./modals.component.css']
})
export class ModalsComponent implements OnInit {

	step:number;
	maxSteps:5;

	constructor(private nodeService:OsmNodeService ) { 
		this.step = 0;
	}

	modalService(){
		return false;
	}

	onNextStep(){
		if(this.step < this.maxSteps){
			this.step++;
		}
	}

	onPreviousStep(){
		if(this.step > 0){
			this.step--;
		}
	}

	ngOnInit() {
	}

}