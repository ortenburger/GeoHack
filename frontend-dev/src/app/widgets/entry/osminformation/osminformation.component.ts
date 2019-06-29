import { Component, OnInit, Input } from '@angular/core';
import { OsmNode } from '../../../models/OsmNode';
import { FormsModule ,FormBuilder, ReactiveFormsModule, FormGroup, Validators } from '@angular/forms';
@Component({
  selector: 'app-osminformation',
  templateUrl: './osminformation.component.html',
  styleUrls: ['./osminformation.component.css']
})
export class OsminformationComponent implements OnInit {
	@Input('node') node : OsmNode;
  @Input('form') form : FormGroup;

	
  constructor() { }

  ngOnInit() {
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
  default:{
    return key;
  } 
}
}

}


