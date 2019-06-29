import { Pipe, PipeTransform } from '@angular/core';
import { OsmNode } from '../models/OsmNode';
import { BliDimension } from '../models/BliDimension';

@Pipe({
  name: 'nodeFilter'
})
export class NodeFilterPipe implements PipeTransform {
  transform(nodes: Array<OsmNode>, filterMode?: string,data?:any): any {
      console.log("Filter Args: "+JSON.stringify(filterMode) + " with nodes: "+nodes.length);
      if(!filterMode){
          return nodes;
      }
      if(filterMode == "hasDimensions"&& !data ){
          if(!data){
            return nodes.filter( node => (node.dimensions && node.dimensions.length>0) );
          }
      }
      if(filterMode == "hasDimensionsAND" && data){
          if(!data){
                return nodes.filter( node => (node.dimensions && node.dimensions.length>0) );
          }
      }
      if(filterMode == "hasDimensionsOR" && data){
          if(!data){
            return nodes.filter( node => (node.dimensions && node.dimensions.length>0) );
          }
          
                return nodes.filter( node => {
                    for(let dimension of data){
                        if(node.hasBliDimension(dimension)){
                            return true;
                        }
                    }
                    return false;
                });
          
      }
      if(filterMode == "hasPeers"){
        return nodes.filter( node => (node.peers && node.peers.length>0) );   
      }
      
  }

}
