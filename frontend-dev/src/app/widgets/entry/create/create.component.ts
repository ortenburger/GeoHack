import { Component, OnInit, Input } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule ,FormBuilder, ReactiveFormsModule, FormGroup, Validators } from '@angular/forms';
import { first, catchError, map, tap } from 'rxjs/operators';
import { User } from '../../../models/User';
import { Tag } from '../../../models/Tag';


import { BliDimension } from '../../../models/BliDimension';
import { OsmNode } from '../../../models/OsmNode';
import { OsmNodeFactory } from '../../../models/OsmNodeFactory';
import { OsmNodeService } from '../../../services/osm-node.service';
import { LogService } from '../../../services/log.service';
import { GeoportalApiService } from '../../../services/geoportal-api.service';
import { AuthService } from '../../../services/auth.service';
import { BliService } from '../../../services/bli.service';
import { Observable ,throwError } from 'rxjs';
import { OverpassService } from '../../../services/overpass.service';
import { CacheService } from '../../../services/cache.service';
import { CategoryService } from '../../../services/category.service';
import { isArray } from 'util';

@Component({
  selector: 'app-create',
  templateUrl: './create.component.html',
  styleUrls: ['./create.component.css']
})
export class CreateComponent implements OnInit {

    sharingCollapsed = true;
    descCollapsed = true; 
    node : OsmNode;
    id:number;
    form: FormGroup;
    constructor(
        private nodeService: OsmNodeService,
        private overpass: OverpassService,
        private router: Router,
        private route: ActivatedRoute,
        private api: GeoportalApiService,
        private logger: LogService,
        private authService: AuthService,
        private formBuilder: FormBuilder
        ) { 
        
    }
    setupFormGroup(){
        if(this.node === null || this.node === undefined){
            return;
        }
        this.form = this.formBuilder.group({
            osmForm:this.formBuilder.group({
                name:[''],
            }),
            goodplaceForm:this.formBuilder.group({
                description:[this.node.getDescription()],
            }),
            networkpartnerForm:this.formBuilder.group({
                welfare:[this.node.getTagValue('_gpd:welfare')],
                aktionsradius:[this.node.getTagValue('_gpd:aktionsradius')],
            }),
            categoryForm:this.formBuilder.group({
                sharing_organisator:[this.node.getTagValue('_gpd:sharing_organisator')],
                sharing_offers:[this.node.getTagValue('_gpd:sharing_offers')],
                sharing_free_offers:[this.node.getTagValue('_gpd:sharing_free_offers')],
                sharing_nonfree_offers:[this.node.getTagValue('_gpd:sharing_free_offers')],
                sustainable_nutrition:[this.node.getTagValue('_gpd:sustainable_nutrition')],
                sustainable_nutrition_assortment:[this.node.getTagValue('_gpd:sustainable_nutrition_assortment')],
            })
            }
        );
    }
    
    save(){

        let osmForm = this.form.get('osmForm').value;

        if(osmForm.name){
            this.node.setName(osmForm.name);
        }else{
            this.node.setName(this.node.getOsmTagValue('name'));
        }
        let goodplaceForm = this.form.get('goodplaceForm').value;
        if(goodplaceForm.description){
            this.node.setDescription(goodplaceForm.description);
        }else{
            this.node.setDescription("");
        }

        let networkpartnerForm = this.form.get('networkpartnerForm').value;
        if(networkpartnerForm.welfare){
            this.node.setTag('_gpd:welfare',networkpartnerForm.welfare);
        }else{
            this.node.removeTag('_gpd:welfare');
        }
        if(networkpartnerForm.aktionsradius){
            this.node.setTag('_gpd:aktionsradius',networkpartnerForm.aktionsradius);
        }else{
            this.node.removeTag('_gpd:aktionsradius');
        }

        let categoryForm = this.form.get('categoryForm').value;
        if(categoryForm.sharingOrganisator){
            this.node.setTag('_gpd:sharing_organisator',categoryForm.sharing_organisator);
        }else{
            this.node.removeTag('_gpd:sharing_organisator');
        }

        if(categoryForm.sharing_offers){
            this.node.setTag('_gpd:sharing_offers',categoryForm.sharing_offers);
        }else{
            this.node.removeTag('_gpd:sharing_offers');
        }   

        if(categoryForm.sharing_organisator){
            this.node.setTag('_gpd:sharing_organisator',categoryForm.sharing_organisator);
        }else{
            this.node.removeTag('_gpd:sharing_organisator');
        }   

        if(categoryForm.sharing_free_offers){
            this.node.setTag('_gpd:sharing_free_offers',categoryForm.sharing_free_offers);
        }else{
            this.node.removeTag('_gpd:sharing_free_offers');
        }   
        
        if(categoryForm.sharing_nonfree_offers){
            this.node.setTag('_gpd:sharing_nonfree_offers',categoryForm.sharing_nonfree_offers);
        }else{
            this.node.removeTag('_gpd:sharing_nonfree_offers');
        }   

        if(categoryForm.sustainable_nutrition){
            this.node.setTag('_gpd:sustainable_nutrition',categoryForm.sustainable_nutrition);
        }else{
            this.node.removeTag('_gpd:sustainable_nutrition');
        }   


        if(categoryForm.sustainable_nutrition_assortment){
            this.node.setTag('_gpd:sustainable_nutrition_assortment',categoryForm.sustainable_nutrition_assortment);
        }else{
            this.node.removeTag('_gpd:sustainable_nutrition_assortment');
        }   
        this.logger.debug("updating on server..");
        this.logger.debug(this.node.debugString());

        
        this.api.saveGeoElement(this.node).subscribe(
            (data)=>{

                OsmNodeFactory.create(data);
                if(data['id']){
                    this.node.setId(data['id']);
                    this.logger.notify('success','Gespeichert: '+this.node.getId());
                }else{
                    this.logger.notify('success','Gespeichert.');
                }

                this.router.navigate(['search']);
            },
            (error)=>{

                this.logger.notify('error','Fehler beim Speichern.');
                if(error.status == 409){
                  this.logger.notify('info','Es sieht aus als wäre dieses Element in der Zwischenzeit von jemand anderem angelegt worden.');

                }
                this.router.navigate(['search']);

            }
        );
   }
    ngOnInit() {
        this.loadElement();
    }
    loadElement(){
        this.logger.debug("Loading element..."),
        this.route.params.subscribe(params => {
            this.logger.debug("Route-subscription emitted.");
            if(!this.authService.isLoggedIn()){
                params['redirectTo']='entrycreate';
                this.logger.notify('error','Nicht eingeloggt');
                this.router.navigate(['login',params]);
                return;
            }

            this.logger.debug("Params:");;
            let id = params['id'];
            let osmType = params['type'];
            this.logger.debug("Getting node from factory.");

            this.node = OsmNodeFactory.getNodeByOsmData(id,osmType);
            if(this.node == null){
                this.logger.debug("Element with id "+id+ " was not known to factory. reading from server.");
                this.api.getGeoElementByOsmId(id,osmType).subscribe(
                    (data)=>{
                        this.logger.debug("Got data to edit.");
                        this.logger.notify("warning","Achtung. Dieses Element ist schon angelegt worden.");
                        this.node = OsmNodeFactory.create(data);
                        this.overpass.getOsmTags(this.node);
                        this.router.navigate(['entryedit',{id:this.node.getId()}]);
                    },
                    (error)=>{
                        let node = new OsmNode({});
                        node.setOsmId(id);
                        node.setOsmType(osmType);
                        node.setId(-1);
                        this.overpass.getOsmTags(node);
                        this.node = node;
                        this.setupFormGroup();
                    });
            }else{  
                this.logger.debug("Got element. showing");
                this.logger.debug(this.node.debugString());
                this.overpass.getOsmTags(this.node);
                this.setupFormGroup();
            }
        });

    }        
    

}