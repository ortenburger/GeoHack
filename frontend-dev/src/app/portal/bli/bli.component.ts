import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { LogService } from '../../services/log.service';
import { BliDimension } from '../../models/BliDimension';
import { BliService } from '../../services/bli.service';
import { AuthService } from '../../services/auth.service';
import { OsmNode } from '../../models/OsmNode';


@Component({
  selector: 'app-bli',
  templateUrl: './bli.component.html',
  styleUrls: ['./bli.component.css']
})

export class BliComponent implements OnInit{
    @Output('changed') changed = new EventEmitter<Array<BliDimension>>();
    @Input('node') node: OsmNode;
    @Input('displayType') displayType: string;
    @Input('dimensions') inputDimensions: Array<BliDimension>;
    dimensions: Array<BliDimension>;
    constructor(private bliService: BliService, private logger: LogService,private authService: AuthService) {
        if(this.inputDimensions){
            this.dimensions = this.inputDimensions;
        }else{
            this.dimensions = new Array<BliDimension>();
        }
    }


    hasDimension(node:OsmNode, dimName:String){
        for(let dim of node.dimensions){
            if(dim.name == dimName){
                return true;
            }    
        }
        return false;
    }
    
    /**
     * Für das array
     */
    toggleDimensionOnOutputArray(dimension:BliDimension){
        let idx = this.dimensions.indexOf(dimension);
        if(idx == -1){
            this.dimensions.push(dimension);
        }else{
            this.dimensions.splice(idx,1);
        }
        this.changed.emit(this.dimensions);
    }
    ngOnInit(){
        if(this.inputDimensions){
            this.dimensions = this.inputDimensions;
        }else{
            this.dimensions = this.bliService.dimensions;
        }

    }
}
