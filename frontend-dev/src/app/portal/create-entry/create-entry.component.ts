import { Component, OnInit, Input } from '@angular/core';
import { Router } from '@angular/router';

import { FormsModule ,FormBuilder, FormGroup, Validators } from '@angular/forms';
import { first, catchError, map, tap } from 'rxjs/operators';
import { User } from '../../models/User';
import { KeyValuePair } from '../../models/KeyValuePair';

import { BliDimension } from '../../models/BliDimension';
import { OsmNode } from '../../models/OsmNode';
import { OsmNodeFactory } from '../../models/OsmNodeFactory';
import { OsmNodeService } from '../../services/osm-node.service';
import { LogService } from '../../services/log.service';
import { GeoportalApiService } from '../../services/geoportal-api.service';
import { AuthService } from '../../services/auth.service';
import { BliService } from '../../services/bli.service';
import { Observable ,throwError } from 'rxjs';
import { OverpassService } from '../../services/overpass.service';
import { CacheService } from '../../services/cache.service';
import { CategoryService } from '../../services/category.service';
import { isArray } from 'util';

@Component({
  selector: 'app-create-entry',
  templateUrl: './create-entry.component.html',
  styleUrls: ['./create-entry.component.css']
})
  
export class CreateEntryComponent implements OnInit {
  @Input() asModal:boolean=false;
    osmNodeTypes = ['way','node','relation'];
    createEntryForm: FormGroup;
    addPeerForm: FormGroup;
    loading = false;
    submitted = false;
    message: string="";
	newKey: string="";
	newValue: string="";
    newPeerId: number;
    newPeerType:string;
    result: Array<OsmNode> = null;
    dimensions : Array<BliDimension>;
	elementToCreate: OsmNode;
    
    
	addKeyValuePair(){
      this.logger.debug("createEntryComponent: adding tag..");
      if(!this.elementToCreate.hasTag(this.newKey)){
        this.elementToCreate.addTag(new KeyValuePair({key:this.newKey,value:this.newValue}));
        this.logger.debug("createEntryComponent: tag added.");
        this.logger.debug("createEntryComponent: Element now: \n"+this.elementToCreate.debugString());
        this.newKey="";
        this.newValue="";
      }else{
        this.logger.debug("createEntryComponent: error: tag already exists.");
      }
    }

    createEntry(element: OsmNode){
      this.logger.debug("createEntryComponent: creating entry from "+element.name);
      this.elementToCreate = element;
      this.logger.debug(JSON.stringify(this.elementToCreate,null,3));
      if(!this.elementToCreate.hasCoordinates()){
        this.nodeService.fillNode(this.elementToCreate);
      }
    }
    
    
    abort(){
      this.elementToCreate = null;
      this.newKey = "";
      this.newValue = "";
    }
    removePeer(node: OsmNode){
        
        this.apiService.removePeer(this.elementToCreate,node.id).subscribe(
        (response)=>{this.logger.debug("remove-peer-response: "+JSON.stringify(response));this.elementToCreate.removePeer(node);},
        (error)=>{this.logger.debug("error removing:"+JSON.stringify(error));});
        
    }
    
    addPeer(){
        let peerId = this.addPeerForm.value.newPeerId;
        let peerType = this.addPeerForm.value.newPeerType;
        let peerNode = OsmNodeFactory.getNodeByOsmData(peerId,peerType);
        if(peerNode==null){
            this.logger.notify('error',"Peer-Element unknown.");
            return;
        }
        if(this.elementToCreate.id == peerNode.id){
            this.logger.notify('error','Element must not be peered up with itself.');
            return;
        }
        this.logger.debug("PeerID:"+peerId);
        this.logger.debug("Type: "+peerType);
        this.elementToCreate.addPeer(peerNode);
        
        /*
        this.apiService.peerUp(this.elementToCreate,peerId,peerType).subscribe(
            (response)=>{this.elementToCreate.copy(new OsmNode(response));},
            (errorResponse)=>{this.logger.debug("error-response from peering: "+JSON.stringify(errorResponse,null,3));
                  if(errorResponse.error && errorResponse.error.error){
                      this.logger.notify('error','error saving element: '+errorResponse.error.error);
                  }else{
                    this.logger.notify('error','unknown error while saving element');    
                  }
            }
        );
        */
    }
    
    save(){
        this.nodeService.save(this.elementToCreate);
    }
    
    
    constructor(
        private formBuilder: FormBuilder,
        private router: Router,
        private logger: LogService,
        private apiService: GeoportalApiService,
        private authService: AuthService,
        private overpass: OverpassService,
        private cache: CacheService,
        private bliService: BliService,
        private categoryService: CategoryService,
        private nodeService: OsmNodeService
      ) {
        this.logger.debug("createEntryComponent: constructing createEntry");
        
        this.dimensions = bliService.dimensions;
        this.newKey = " ";
        this.newValue = " ";
     }

    
    ngOnInit() {

      
        if(!this.authService.isLoggedIn()){
          this.logger.notify('warning','not logged in');
          this.router.navigate(['login']);
        }
        this.createEntryForm = this.formBuilder.group({
            osmId: ['', Validators.required],
            osmType: ['node',Validators.required],
        });
        this.addPeerForm = this.formBuilder.group({
            newPeerId: ['', Validators.required],
            newPeerType: ['node',Validators.required],
        });
        this.logger.debug("createEntryComponent: ngOnInit for formBuilder");
      
      if(this.nodeService.elementToEdit != null){
          this.elementToCreate = this.nodeService.elementToEdit;
          if(this.elementToCreate.knownEntity()){
            this.elementToCreate.mode = "edit";
          }

      }
    }

    // convenience getter for easy access to form fields
    get f() {
      this.logger.debug("createEntryComponent: f hit in entry."); 
      return this.createEntryForm.controls; 
    }

    onSubmit() {
        this.logger.debug("createEntryComponent: submit.");
      }


}
