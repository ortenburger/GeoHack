import { BliDimension } from './BliDimension';
import { DataGroupFactory } from './DataGroupFactory';
import { DataGroup } from './DataGroup';
import { KeyValuePair } from './KeyValuePair';
import { BliDimensionFactory } from './BliDimensionFactory';
import { OsmNodeFactory } from './OsmNodeFactory';
/*
namespace OsmNodePool{
   allNodes:Map<number,OsmNode>=new Map<number,OsmNode>();
}*/
export class OsmNode {
  
  id:number;
  osmId=-1;
  osmType: string;
  mapId = -1;
  osmtags:Array<KeyValuePair>;
  lat: number;
  lon: number;
  last_change_date: Date;
  created_date: Date;
  mode="create";
  
  description="";
  name="";
  dimensions:Array<BliDimension>;
  nodes:Array<OsmNode>; // nodes (z.B. this.type = way hat nodes)
  peers:Array<OsmNode>; // peers (wie in "sinn des geoportals".)
  tags:Array<KeyValuePair>;
  dataGroups : Array<DataGroup>;
  copy(data: OsmNode){
    this.name = data.name;
    this.description=data.description;
    this.osmId = data.osmId;
    this.osmType = data.osmType;
    this.peers = data.peers;
    this.dataGroups=data.dataGroups;
    this.osmtags = data.osmtags;
    this.dimensions = data.dimensions;
    this.nodes = data.nodes;
    this.tags = data.tags;
    this.id = data.id;
    this.lon = data.lon;
    this.lat = data.lat;
    this.mode = data.mode;
    this.created_date = data.created_date;
    this.last_change_date = data.last_change_date;
   }
    
  constructor(data: any){
    this.dimensions=new Array<BliDimension>();
    this.nodes = new Array<OsmNode>();
    this.tags = new Array<KeyValuePair>();
    this.osmtags = new Array<KeyValuePair>();
    this.dataGroups = new Array<DataGroup>();
    this.lon = NaN;
    this.lat = NaN;
    this.peers= new Array<OsmNode>();
    
    if(data instanceof OsmNode){
      this.copy(data);
      return;
    }
    this.name = "";
    
    if(data['id'] && data['osmId'] && data['type']){
        // aus unserer datenbank
        this.osmId = data['osmId'];
        this.osmType = data['type'].toLowerCase();;
        this.id = data['id'];
    }else if(data['id'] && data['type']){
        // von overpass
            this.osmId=data['id'];
            this.osmType = data['type'].toLowerCase();
    }else if(data['osmId'] && data['type']){
        this.osmId = data['osmId'];
        this.osmType = data['type'].toLowerCase();    
    }
    if(data['type']){
      this.osmType = data['type'].toLowerCase();
    }
    if(data['tags'] && Array.isArray(data['tags'])){
      for(let tagData of data['tags']){
             this.tags.push(new KeyValuePair(tagData));
      }
    }
    
    if(data['name']){
        this.name = data['name'];
    }
      
    if(data['lon']){
        this.lon = data['lon'];
    }else{
        this.lon = NaN;
    }
    
    if(data['lat']){
        this.lat = data['lat'];
    }else{
        this.lat = NaN;
    }
      
    if(data['created']){
        this.created_date = new Date(data['created']);
    }
    if(data['created']){
        this.last_change_date = new Date(data['lastChanged']);
    }

    if(data['description']){
        this.description=data['description'];
    }
    
    if(data['dataGroups']){
        for(let group of data['dataGroups']){
            if(group['id']){
                this.addDataGroup(DataGroupFactory.create(group));
            }
        }    
    }
    if(data['bliDimensions']){
        for(let dimData of data['bliDimensions']){
            let dimension = BliDimensionFactory.create(dimData);
            if(dimension != null){
                this.dimensions.push(dimension);
            }
        }
    }
    if(data['peers']){
        if(!this.peers){
            this.peers = new Array<OsmNode>();
        }
        for(let pdata of data['peers']){
            let peer = OsmNodeFactory.create(pdata);
            if(peer != null){
                this.addPeer(peer);
            }else{
                console.log("Not adding empty node as peer, json: "+JSON.stringify(pdata));
            }
        }
    }
    if(data['nodes']){
      for(let node of data['nodes']){
          if(parseInt(node) == NaN ){
              let newNode = OsmNodeFactory.create(node);
              newNode.osmType = 'node';
              this.nodes.push(newNode);
           }else{
              let tmp = OsmNodeFactory.create({"id":parseInt(node),"type":"node"});
              tmp.osmId=parseInt(node);
              this.nodes.push(tmp);
           }
        }
    }
    
    
  }
  getName(){
    return this.name;
  }
  setName(name:string):void{
    this.name=name;
  }
  removeName(){
    this.name = '';
  }
  toggleDataGroup(datagroup: DataGroup){
    
      if(!this.hasDataGroup(datagroup)){
          this.addDataGroup(datagroup);
      }else{
          this.removeDataGroup(datagroup);
      }
  }
  addDataGroup(datagroup: DataGroup){
     if(!this.hasDataGroup(datagroup) && datagroup != undefined && datagroup != null && datagroup.id != null){
        this.dataGroups.push(datagroup);
     }
  }
  removeDataGroup(datagroup: DataGroup){
      const idx = this.dataGroups.indexOf(datagroup,0);
      if(idx > -1){
          this.dataGroups.splice(idx,1);
      }
  }

  hasDataGroup(_dg: DataGroup):boolean{
      if(!this.dataGroups || this.dataGroups.length == 0){
          return false;
      }
      for(let dg of this.dataGroups){
          if(dg.id == _dg.id){
              return true;
          }
      }
      return false;
  }
  hasCoordinates():boolean {
      let hascoords:boolean = (!isNaN(this.lat)&&!isNaN(this.lon) && this.lat != null && this.lon != null );
      return hascoords;
  }
  setCoordinatesFromChilds(){
        let _lon:number=0;
        let _lat:number=0;
        let _items:number=0;
        for(let node of this.nodes){
            if(node.hasCoordinates()){
                _lon=_lon+node.lon;
                _lat=_lat+node.lat;
                _items++;
            }   
        }
      this.lon = _lon / _items;
      this.lat = _lat / _items;
  }


  toggleDimension(dim: BliDimension){
      if(!this.hasBliDimension(dim)){
          this.addBliDimension(dim);

      }else{
          this.removeBliDimension(dim);
      }
  }

  addBliDimension(dim: BliDimension){
     if(!this.dimensions){
         this.dimensions = new Array<BliDimension>();
     }
     for(let dimension of this.dimensions){
         if(dimension.id == dim.id){
             return;
         }
     }
     this.dimensions.push(dim);
  }

  removeBliDimension(dim: BliDimension){
      const idx = this.dimensions.indexOf(dim,0);
      if(idx > -1){
          this.dimensions.splice(idx,1);
      }
  }

  hasBliDimension(dim: BliDimension){
      for(let d of this.dimensions){
          if(dim.id == d.id){
              return true;
          }
      }
      return false;
  }

  isValid(): boolean{
    return (this.osmType != "" && this.osmId != -1)
  }
    
  clearOsmTags(){
    this.osmtags.length=0;
  }

  getOsmTagKeys(){
    let keys = new Array<String>();
    for(let tag of this.osmtags){
        keys.push(tag.key);
    }
    return keys;
  }

  getOsmTagValue(key){
    for(let kvp of this.osmtags){
      if(kvp.key == key){
        return kvp.value;
      }
    }
    return undefined;
  }
    
  removeOsmTag(tag: KeyValuePair){
    let idx=(this.osmtags.indexOf(tag,0));
    if(idx > -1){
        this.osmtags.splice(idx,1);
    }
      
  }

  addOsmTag(tag: KeyValuePair){
      this.osmtags.push(tag);
  }

  hasOsmTag(tag:KeyValuePair|string){
    if(tag instanceof KeyValuePair){
      return (this.osmtags.indexOf(tag,0)>-1);
    }else{
      for(let _tag of this.tags){
        if(_tag.key == tag){
          return true;
        }
      }
      return false;
    }
  }  
  getTag(key:string){
    for(let tag of this.tags){
      if(tag.key == key){
        return tag;
      }
    }
    return null;
  }
  setTag(key:string,value:string,displayName?:string,source?:string){
    
    let tag = this.getTag(key); 
    if(tag == null){
      tag = new KeyValuePair({});
    }else{
      this.removeTag(tag);
    }
    tag.key = key;
    tag.value = value;
    tag.displayName = displayName;
    tag.source = source;
    this.addTag(tag);
    
  }

  addTag(tag:KeyValuePair){
      if(!this.tags){
          this.tags = new Array<KeyValuePair>();
      }
      for(let _tag of this.tags){
        if(tag.key == _tag.key){
            return;
        }    
      }
      this.tags.push(tag);
  }
  
  getTagKeys(){
    let keys = new Array<string>();
    for(let item in this.tags){
      keys.push(item);
    }
  }
  getTagValue(key){
    let tag = this.getTag(key);
    if(tag){
      return tag.value;
    }
    return null;
  }
  hasTag(key:string){
    for(let item of this.tags){
        if(item.key == key){
            return true;
        } 
    }
    return false;
  }
  removeTag(tag:KeyValuePair|string){
    let search = "";
    if(tag instanceof KeyValuePair){
      search=tag.key;
    }else{
      search=tag;
    }
    let idx=-1;
    for(let i in this.tags){
      if(this.tags[i].key == search){
        idx=parseInt(i);
      }
    }
    if(idx > -1){
        this.tags.splice(idx,1);
    }
  }
  getChild(id:number){
      if(this.osmId == id){
          return this;
      }
      for(let node of this.nodes){
        if(node.osmId == id){
            return node; 
        }
        if(node.nodes.length > 0){
            let child = node.getChild(id);
            if(child){
                return child;
            }   
        }
      }
  }
  addPeer(node:OsmNode){
    if(this.peers.indexOf(node)== -1){
        this.peers.push(node);
    }
  }
  hasPeer(node:OsmNode){
    return (this.peers.indexOf(node)!=-1);
  }
  removePeer(node: OsmNode){
    let idx=-1;
    let i=0;
    for(let peer of this.peers){
        if(peer.id == node.id){
            idx = i;
        }
        i++;
    }
    if(idx > -1){
        this.peers.splice(idx,1);
    }
  }
  update(data:any){
      if(data instanceof OsmNode){
        this.updateFromOsmNode(data);
        return;
      }else{
        let updateNode = new OsmNode(data);
        this.updateFromOsmNode(updateNode);
      }
  }
  
  /*
    Check if element is already known (i.E. already in the Database)  
  */
  knownEntity(){
      return (this.id != undefined && this.id != null && this.id > 0) ;
  }
  updateFromOsmNode(data: any){
    if(data.id){
        this.id=data.id;
    }
    if(data.description){
        this.description=data.description;
    }
    if(data.osmType){
        this.osmType = data.osmType.toLowerCase();
    }
    if(data.dimensions){
        for(let item of data.dimensions){
            this.addBliDimension(item);    
        }
    }
    if(data.dataGroups){
    	for(let item of data.dataGroups){
            this.addDataGroup(item);
        }
    }
    if(data.peers){
        for(let item of data.peers){
            this.addPeer(item);    
        }
    }
    if(data.tags){
        for(let item of data.tags){
                this.addTag(item);
        }
    }
    if(data.nodes){
        for(let n of data.nodes){
            this.addPeer(n);
        }
    }
    if(data.lon){
        this.lon = data.lon;
    }
    if(data.lat){
        this.lat = data.lat;
    }
    
    this.mode = data.mode;
   }
    
   getDate(){
       if(this.last_change_date){
           return new Date(this.last_change_date);
       }
       if(this.created_date){
           return new Date(this.created_date);
       }
       return Date();
   }
   setOsmType(t:string){
    this.osmType=t;
   }
   getOsmType(){
    if(this.osmType){
      return this.osmType;
    }else{
      return "";
    }
   }
   setOsmId(id:number){
      this.osmId = id;
   }
   getOsmId(){
    if(this.osmId){
      return this.osmId;
    }else{
      return "";
    }
   }
   hasOsmTags(){
    return this.osmtags.length != 0;
   }
   setId(id:number){
    this.id = id;
   }
    getId(){
      if(this.id){
        return this.id;
      }else{
        return "";
      }
    }

   debugString():string{
       let debugString : string;
       debugString = "Name: " + this.name + "\n";
       debugString += "ID: "+ this.id +"\n";
       debugString += "osmId: "+this.osmId +"\n";
       debugString += "osmType: "+this.osmType +"\n";
       debugString += "mapId: "+ this.mapId+"\n";
       debugString += "Lon/Lat: "+this.lon +"/"+this.lat+"\n";
       debugString += "Peers: \n";
       if(!this.peers){
           debugString += "null\n";
       }
       for(let peer of this.peers){
           debugString += "\t "+peer.name + "(id:"+peer.id+" ,osmId: "+peer.osmType+"::"+peer.osmId+")\n";
       }
       debugString += "Tags: \n";
       if(!this.tags){
           debugString += "null\n";
       }
       for(let tag of this.tags){
            debugString += "Tag\t ["+tag.key + "]=["+tag.value+"]\n";
       }
       debugString += "OSM-Tags: \n";
       for(let tag of this.osmtags){
            debugString += "OSM-Tag\t ["+tag.key + "]=["+tag.value+"]\n";
       }
       debugString += "Dimensions: \n";
       for(let dim of this.dimensions){
            debugString += "BLI\t ["+dim.id + "]=["+dim.name+"]\n";
       }
       debugString += "DataGroups:\n";
       if(!this.dataGroups){
           debugString += "null\n";
       }
       for(let dg of this.dataGroups){
           debugString += "\t "+dg.name + " ("+dg.id+")\n";
       }
       return debugString;
   }
    getLon():number{
      return this.lon
    }
    getLat():number{
      return this.lat;
    }
    flatten(){
        let obj = { 
            name : this.name,
            id : this.id,
            osmId : this.osmId,
            osmType : this.osmType,
            description : this.description,
            peers : new Array,
            tags : new Array,
            dimensions : new Array,
            dataGroups : new Array,
            lon: this.lon,
            lat: this.lat
        };
        
        for(let dg of this.dataGroups){
            obj['dataGroups'].push({id:dg.id,name:dg.name});
        }
        
        for(let peer of this.peers){
            obj['peers'].push({id:peer.id,name:peer.name});
        }
                
        for(let dim of this.dimensions){
            obj['dimensions'].push({id:dim.id,name:dim.name});
        }

        for(let tag of this.tags){
            obj['tags'].push({id:tag.id,key:tag.key,value:tag.value});
        }
        return obj;
    }

    getDescription():string{
      return this.description;
    }  
    setDescription(desc:string):void{
      this.description = desc;
    }

}