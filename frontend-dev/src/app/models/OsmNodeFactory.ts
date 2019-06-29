import {OsmNode} from './OsmNode';
export class OsmNodeFactory{
	
	static nodes: Array<OsmNode>;
	static init(){
		OsmNodeFactory.nodes = new Array<OsmNode>();
	}
	public static create(data:any){
		
		if(data instanceof OsmNode){
			let node = OsmNodeFactory.getNodeById(data.id);
			if(node === null){
				this.nodes.push(node);
				return node;
			}
		}
        
		if(data['osmId'] && data['type']){
			let node = OsmNodeFactory.getNodeByOsmData(data['osmId'],data['type']);
			if(node != null){
				node.update(data);
				return node;
			}else{
                this.nodes.push(new OsmNode(data));
            }
            
		}
            
		if(data['id']){
			let node = OsmNodeFactory.getNodeById(data['id']);
			if(node != null){
				node.update(data);
				return node;
			}
		}
		let node = new OsmNode(data);
		OsmNodeFactory.nodes.push(node);
		return node;
	}
	
	public static getNodeById( id:number ) : OsmNode {
		for(let node of OsmNodeFactory.nodes){
			if(node.id == id){
				return node;
			}
		}
		return null;
	}
	public static remove(id:number):boolean{
		let pos=-1;
		console.log("removing element with id "+id);
		for(let idx in OsmNodeFactory.nodes){
			if(OsmNodeFactory.nodes[idx].id == id){
				pos=parseInt(idx);
			}
		}
		if(pos != -1){
			OsmNodeFactory.nodes.splice(pos,0);
			return true;
		}
		return false;
	}


	public static getNodeByOsmData( osmId:number , osmType:string) : OsmNode {
		for(let node of OsmNodeFactory.nodes){
			if(node.osmId == osmId && node.osmType == osmType.toLowerCase()){
				return node;
			}
		}
		return null;
	}
}
OsmNodeFactory.init();