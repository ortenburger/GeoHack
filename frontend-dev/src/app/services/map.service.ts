import { Injectable } from '@angular/core';
import { Router } from '@angular/router';

import { default as OlMap } from 'ol/Map';
import { default as OlXYZ } from 'ol/source/XYZ';
import { default as OlOSM } from 'ol/source/OSM';
import { default as OlVectorSource } from 'ol/source/Vector'
import { default as OlTileLayer } from 'ol/layer/Tile';
import { default as OlView } from 'ol/View';
import OlVectorLayer from 'ol/layer/Vector';
import { default as OlMapBrowserEvent } from 'ol/MapBrowserEvent';
import OlProj from 'ol/proj';
//import OlProjection from 'ol/proj/projection';
import OlExtent from 'ol/extent';
import OlFeature from 'ol/Feature';
import { default as OlVectorFeature } from 'ol/Feature';
import { Icon as OlIcon, Style as OlStyle } from 'ol/style';
import { Stroke as OlStroke, Fill as OlFill } from 'ol/style';
import { fromLonLat } from 'ol/proj';
import { transformExtent } from 'ol/proj';
import OlPoint from 'ol/geom/Point';
import OlLineString from 'ol/geom/LineString';
import { LogService } from './log.service';
import { OsmNode } from '../models/OsmNode';
import { defaults as defaultControls  } from 'ol/control.js';
import { Attribution as OlAttribution } from 'ol/control.js';
import { Rectangle } from '../models/Rectangle';
import { OsmNodeService } from './osm-node.service';

//OlTransforms.transformExtent(extent,'EPSG:3857','EPSG:4326');

@Injectable({
	providedIn: 'root'
})
export class MapService {
	
	maxZoom : number = 14;
	minZoom: number = 2;
	map: OlMap;
	source: OlXYZ;
	layer: OlTileLayer;
	view: OlView;
	zoom: number;
	coordinates: Array<number>;
	mapLayer: OlTileLayer;
	iconLayer: OlVectorLayer;
	connectionLayer: OlVectorLayer;
	connections: Array<OlFeature>;
	icons: Array<OlFeature>;
	layers: Array<any>;
	text:string;
	attribution: OlAttribution;
	nodeSets:Map<String,Array<OsmNode>>;
	removedNodeSets:Map<String,Array<OsmNode>>;
	

	animationDuration:2500;
	padding:[150,350,150,350];


	constructor(
		private logger:LogService,
		private osmNodeService: OsmNodeService,
		private router: Router
		) {
		
		this.layers=new Array<any>();
		this.nodeSets=new Map<String,Array<OsmNode>>();
		this.removedNodeSets=new Map<String,Array<OsmNode>>();
		this.icons = new Array<OlFeature>();
		this.connections = new Array<OlFeature>();
		this.coordinates = [7.147,51.26];
		this.changeText();
		this.zoom = 12;


		this.iconLayer = new OlVectorLayer({
			style: function(feature) {
				return feature.get('style');
			},
			source: new OlVectorSource({features: this.icons})
		});


		this.connectionLayer = new OlVectorLayer({
			style: function(feature) {
				return feature.get('style');
			},
			source: new OlVectorSource({features: this.connections})
		});

		this.source = new OlOSM({
			url: 'http://tile.osm.org/{z}/{x}/{y}.png'
		});

		this.mapLayer = new OlTileLayer({
			source: this.source
		});

		this.view = new OlView({
			center: fromLonLat(this.coordinates),
			zoom: this.zoom,
			minZoom: this.minZoom
		});
	}

	goto(lon,lat,zoom?,timeout?){
		if(zoom == undefined){
			zoom = this.getZoom();
		}
		if(timeout == undefined){
			timeout = 100;
		}
		setTimeout(
			()=>{
				this.logger.debug("Going to"+lon+"/"+lat);
				this.map.getView().animate({
					center: fromLonLat([lon,lat]),
					duration:this.animationDuration,
					zoom:this.getZoom()
				});
			}
			,timeout
		);
		/*
		this.view = new OlView({
			center: fromLonLat([lon,lat]),
			zoom: zoom
		});	
		this.map.setView(this.view);
		*/
	}

	createElement(coords,node,setId?:string){
		if(!setId){
			setId='default';
		}

		if(node.mapId== -1){
			node.mapId = parseInt(node.osmId)+""+(Math.floor(Math.random()*100000)+1);
		}
		let iconFeature = new OlFeature(new OlPoint(fromLonLat(coords)));

		//https://www.flaticon.com/free-icon/map-marker_33622
		let marker:string;
		switch(setId){
			case 'searchResult':{
				marker='assets/img/map-marker_result.png';
				break;
			}
			case 'detailView':{
				marker='assets/img/map-marker_selected.png';
				break;
			}
			case 'default':{
				marker='assets/img/map-marker.png';
				break;
			}
			default:{
				marker='assets/img/map-marker.png';
			}
		}
		iconFeature.set('style', this.createStyle(marker, undefined));

		iconFeature.setId(parseInt(node.mapId));
		return iconFeature;

	}


	createStyle(src, img) {

		return new OlStyle({
			image: new OlIcon(/** @type {module:ol/style/Icon~Options} */ ({
				/*anchor:[0.5,0.4],*/
				crossOrigin: 'anonymous',
				src: src,
				img: img,
				imgSize: img ? [img.width, img.height] : undefined
			}))
		});
	}



	fitView(){
		this.map.getView().fit(this.iconLayer.getSource().getExtent(),{padding:[100,395,100,395]});
		
		this.logger.debug("Fitting view around "+this.icons.length+ " elements.");
		this.coordinates = this.getCenter();
		this.zoom = this.map.getView().getZoom();

		if(this.zoom > this.maxZoom){
			this.zoom = this.maxZoom;
			this.zoom
		}
		this.logger.debug("MapService: fitting view to "+JSON.stringify(this.coordinates)+" (zoom: "+this.zoom +")");
		
		this.map.setView(
			new OlView({
				center: fromLonLat(this.coordinates),
				zoom: this.zoom,
				minZoom: this.minZoom
			}));
		
	}

	addNode(node:OsmNode,setId?:string){
		if(!setId){
			setId='default';
		}
		this.logger.debug("adding "+node.name + " to set "+setId);
		if(!this.nodeSets.has(setId)){
			let nodeSet = new Array<OsmNode>();
			nodeSet.push(node);
			this.nodeSets.set(setId,nodeSet);
		}else{
			this.nodeSets.get(setId).push(node);
		}
		this.rerender();
	}


	showConnections(){

		let allnodes = new Array<OsmNode>();
		this.nodeSets.forEach( (nodeSet: Array<OsmNode>,key:string)=>{
			for( let node of nodeSet){
				if(!allnodes.some(element => node === element)){
					allnodes.push(node);
				}
			}
		});


		this.connections.length=0;
		allnodes.forEach( (node)=>{
			for( let peer of node.peers ){
				// nur anzeigen, wenn das element auch angezeigt wird.
				if(allnodes.indexOf(peer)>-1){
					let line = this.createLine(node,peer);
					this.connections.push(line);
				}
			}
		});

		this.map.removeLayer(this.connectionLayer);
		this.connectionLayer = new OlVectorLayer({
			source: new OlVectorSource({features: this.connections}),
		});

		this.map.addLayer(this.connectionLayer);
	}

	showNodes(_nodes: Array<OsmNode>,setId?:string){

		let nodes =  Object.assign([], _nodes);
		if(!setId){
			setId='default';
		}
		if(this.nodeSets.has(setId)){
			this.nodeSets.delete(setId);
		}

		this.nodeSets.set(setId,nodes);
		let center = {'lon':0,'lat':0};
		let bbox = {'minlon':0,'minlat':0,'maxlon':0,'maxlat':0};
		let nodeCount=0;
		for(let node of nodes){
			if(node.hasCoordinates()){
				center.lon += node.lon;
				center.lat += node.lat;
				if(bbox.minlon>node.lon){
					bbox.minlon=node.lon;
				}
				if(bbox.minlat>node.lat){
					bbox.minlat=node.lat;
				}
				if(bbox.maxlon < node.lon){
					bbox.maxlon=node.lon;
				}
				if(bbox.maxlat < node.lat){
					bbox.maxlat = node.lat;
				}
				nodeCount++;
			}
		}

		center.lon = center.lon/nodeCount;
		center.lat = center.lat/nodeCount;
		this.view = new OlView({
			center: fromLonLat([(center.lon),(center.lat)]),
			minZoom: this.minZoom
		});
		this.rerender();
		return;

	}
	/**
		* Zeig ein einzelnes Element an und überschreibt mit diesem das set (default).
		* Entfernt das entsprechende Element aus allen anderen Sets
		* Speichert die aus den anderen Sets entfernten Elemente in this.removedNodeSets.
		* Lädt die zuvor gespeicherten Elemente aus this.removedNodeSets zurück in ihr respektives nodeSet.

	**/
	show(node: OsmNode,setId?:string){
		// default-set benutzen, wenn nicht anders angegeben.
		if(!setId){
			setId='default';
		}
		/**
		* Alle zuvor entfernten Elemente zurückbringen.
		**/
		this.removedNodeSets.forEach( (removedNodes:Array<OsmNode>,key:string)=>{
			if(!this.nodeSets.has(key)){
				this.nodeSets.set(key,new Array<OsmNode>());
			}
			let nodeSet = this.nodeSets.get(key);
			for(let node of removedNodes){
				nodeSet.push(node);
			}
			removedNodes.length=0;
		});
		this.removedNodeSets.clear();



		// diesen node aus allen anderen sets rauswerfen.
		this.nodeSets.forEach( (nodeSet:Array<OsmNode>,key:string)=>{
			let idx = nodeSet.indexOf(node);

			if(idx == -1){
				return;
			}else{
				if(!this.removedNodeSets.has(key)){
					this.removedNodeSets.set(key,new Array<OsmNode>());
				}
				this.removedNodeSets.get(key).push(nodeSet[idx]);
				nodeSet.splice(idx,1);
			}
		});

		// nodeset leeren
		let nodeSet=new Array<OsmNode>();
		nodeSet.push(node);
		this.nodeSets.delete(setId);
		this.nodeSets.set(setId,nodeSet);

		this.rerender();
		return;
	}

	removeSet(setId?:string){

		if(!setId){
			setId='default';
		}
		if(this.nodeSets.has(setId)){
			this.nodeSets.delete(setId);
			this.rerender();
			this.showConnections();
		}
	}
	/**
	*
	* Alle Nodes aus den existierenden Sets neu rendern.
	**/
	rerender(){

		this.logger.debug("rerendering.");
		// nodeset leeren

		this.icons = new Array<OlFeature>();

		this.nodeSets.forEach( (nodeSet:Array<OsmNode>,key:string)=>{
			this.logger.debug("Showing nodeSet "+key + " with "+nodeSet.length+" element(s).");
			for(let currentNode of nodeSet){
				let feature = this.createElement([(currentNode.lon),(currentNode.lat)],currentNode,key);
				this.icons.push(feature);
			}
		});

		this.logger.debug("this.icons.length now: "+this.icons.length);
		this.map.removeLayer(this.iconLayer);

		this.iconLayer = new OlVectorLayer({
			style: function(feature) {
				return feature.get('style');
			},
			source: new OlVectorSource({features: this.icons})
		});
		this.iconLayer.setZIndex(1);
		this.map.addLayer(this.iconLayer);
		this.showConnections();
		this.logger.debug("rerender() done.");
	}


	removeNodes(setId?:string){
		if(!setId){
			this.nodeSets.clear();
			return;
		}
		if(this.nodeSets.has(setId)){
			this.nodeSets.delete(setId);
			return;
		}
	}
	removeNode(node:OsmNode,setId?:string){
		if(!setId){
			setId='default';
		}
		this.logger.debug("removing node "+node.name+ " from "+setId);
		if(this.nodeSets.has(setId)){
			let nodeSet = this.nodeSets.get(setId);
			let idx = nodeSet.indexOf(node);
			if(idx != -1){
				nodeSet.splice(idx,1);
			}
		}
		this.showConnections();
		this.fitView();
	}
	isVisible(node:OsmNode){
		let isVisible = false;
		this.nodeSets.forEach( (nodeSet: Array<OsmNode>,key:string)=>{
			if(nodeSet.some(element=>element===node)){
				this.logger.debug("Element visible: "+node+name);
				isVisible=true;
			}
		})

		return isVisible;
	}
	togglePeerVisibility(parent: OsmNode,peer: OsmNode){
		if(!this.isVisible(peer)){
			this.addNode(peer,'peers');
			this.rerender();
			this.fitView();
			return;
		}else{

			this.removeNode(peer,'peers');
			this.rerender();
			this.fitView();
			return;
		}

	}

	getBoundingBox(format:string){

		let defaultFormat = 'EPSG:4326';
		if(!format){
			format=defaultFormat;
		}
		let extent = this.map.getView().calculateExtent(this.map.getSize());
		let coords = transformExtent(extent,'EPSG:3857',format);
		return coords;
	}
	createLineStyle(){
		return new OlStyle({
			//stroke: new OlStroke({color: '#cdcdcd',width: 3 ,lineDash: [5, 5]})
			stroke: new OlStroke({color: 'rgba(43,56,9,0.5)' ,width: 1.5})
		});
	}

	createLine(lhs: OsmNode, rhs:OsmNode){

		let lhsCoords = fromLonLat( [lhs.lon,lhs.lat] );
		let rhsCoords = fromLonLat( [rhs.lon,rhs.lat] );


		let lineString = new OlLineString([lhsCoords,rhsCoords]);
		let feature = new OlFeature( {geometry: lineString});
		//feature.set('style',this.createLineStyle());
		feature.setStyle(this.createLineStyle());
		return feature;
		//;
		//feature.set('style', this.createStyle('assets/img/map-marker.png', undefined));
		//feature.set('style', this.createLineStyle());
	}


	showLine(lhs: OsmNode, rhs:OsmNode){
		let feature = this.createLine(lhs,rhs);
		this.connections.push(feature);
		this.map.removeLayer(this.connectionLayer);
		this.connectionLayer = new OlVectorLayer({
			source: new OlVectorSource({features: this.connections}),
		});

		this.map.addLayer(this.connectionLayer);
	}


	initMap(){
		this.layers.push(this.mapLayer);
		this.layers.push(this.iconLayer);
		this.layers.push(this.connectionLayer);
		this.attribution = new OlAttribution({
        	collapsible: false,
        	className: "ol-attribution",
        	collapsed:false
      	});
		this.map = new OlMap({
			target: 'map',
			layers: this.layers,
			view: this.view,
			controls: new defaultControls({attribution: false}).extend([this.attribution])
		});

		let handler = this.clickHandler;
		this.map.on('click', (event)=>{this.clickHandler(event);});
	}

	hasNode(node:OsmNode){

		for(let feature of this.icons){
			if(feature.getId() == node.mapId){
				return true;
			}
		}
		return false;

	}
	clickHandler(ev: OlMapBrowserEvent){
		console.log(this.router.url);
		switch(this.router.url.replace('/','')){
			case 'contact':
			case 'privacypolicy':
			case 'imprint':
			case 'home':
			case 'userconditions':
			case 'login':
			case '':{
				this.router.navigate(['search']);
			}
		}
		//let map=ev.map;
		let map=this.map;
		let pixel = ev.pixel;
		let features = map.getFeaturesAtPixel(pixel);

		let data=features;
		let clickedElement:OsmNode;
		if(data && data.length > 0){
			for(let dataItem of data){
				if(dataItem.getId() != undefined){
					this.nodeSets.forEach((nodeSet: Array<OsmNode>,key:string)=>{
						for(let node of nodeSet){
							if(node.mapId == dataItem.getId()){
								clickedElement = node;
								this.osmNodeService.showDetails(node);

							}
						}
					});
				}
			}
			if(clickedElement){
				console.log("showing detail-view");
				this.show(clickedElement,'detailView');
				this.goto(clickedElement.getLon(),clickedElement.getLat());

			}
		}else{
			console.log("no feature there..");
		}
	}
	getCenter(){
		let bbox=new Rectangle(this.getBoundingBox(null));

		return [ 0.5*(bbox.minX+bbox.maxX) , 0.5*(bbox.minY+bbox.maxY)]
	}

	getZoom(){
		return this.map.getView().getZoom();
	}
	zoomIn(){
		this.zoom = this.map.getView().getZoom();
		this.coordinates = this.getCenter();
		if(this.zoom<this.maxZoom){
			this.zoom++;
		}else{
			this.zoom=this.maxZoom;
		}
		this.logger.debug("zooming to "+this.zoom);
		this.map.setView(
			new OlView({
				center: fromLonLat(this.coordinates),
				zoom: this.zoom,
				minZoom: this.minZoom
			}));
	}

	zoomOut(){
		this.zoom = this.map.getView().getZoom();
		this.coordinates = this.getCenter();
		if(this.zoom>this.minZoom){
			this.zoom--;
		}
		this.logger.debug("zooming to "+this.zoom);
		this.map.setView(
			new OlView({
				center: fromLonLat(this.coordinates),
				zoom: this.zoom,
				minZoom: this.minZoom
			}));
	}

	changeText(){
	}
}
