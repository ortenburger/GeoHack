import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { LogService } from '../services/log.service';
import { CacheService } from '../services/cache.service';
import { map, tap, catchError} from 'rxjs/operators';
import { Observable } from 'rxjs';
import { User } from '../models/User';
import { BliDimension } from '../models/BliDimension';
import { Category } from '../models/Category';
import { Message } from '../models/Message';
import { OsmNode } from '../models/OsmNode';
import { Rectangle } from '../models/Rectangle';


@Injectable({
    providedIn: 'root'
})


export class GeoportalApiService {

  httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        'Accept': 'application/json'
    })
  };

  //private apiURL = 'http://10.0.8.29:8080/geoportal';
  
  private apiURL = 'http://entwicklungssystem.transformationsstadt.de:18080/geoportal';
  //private apiURL = 'http://geoportal-api.transformationsstadt.de:8080/geoportal';
  //private apiURL = 'http://127.0.0.1:8080/geoportal';
  
  private endpoints: GeoportalApiEndpoint[];
  private defaultEndpoint: GeoportalApiEndpoint;
  private bearer: string;


  /**
  * Get this endpoint.
  */
  constructor(private logger: LogService, private http: HttpClient, private cache:CacheService) {
    this.defaultEndpoint = new GeoportalApiEndpoint('*', '*', this.defaultResponseHandler, this.defaultErrorHandler);
    this.loadBearer();
  }

  loadBearer(){
    if(this.cache.has('bearer')){
      
        this.setBearer(this.cache.get('bearer'));
        this.logger.debug("GeoportalApiService: bearer loaded from cache.");
    }else{
        this.logger.debug("GeoportalApiService: failed to load bearer from cache.");
    }
  }
  saveBearer(){
    this.logger.debug("Saving bearer.");
    this.cache.set('bearer',this.bearer);
  }
  deleteBearer(){
      this.cache.remove('bearer');
      this.bearer = '';
      this.httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        'Accept': 'application/json'
        })
    };
  }

  ping() {
    this.logger.debug('ping?');
    this.http.get(this.apiURL + '/accounts/ping/', this.httpOptions).subscribe(
      (response) => {
        this.logger.debug('pong!');
      },
      (error) => {
        this.logger.debug('no pong :/');
        this.logger.notify('error','server seems to be unreachable.');
      }
    );

  }
  registerUser(user: User): Observable<User> {
    let post = this.http.post<User>(this.apiURL + '/accounts/register/', user , this.httpOptions );
    return post;
  }

  login(username: string, password: string): Observable<any> {
    this.logger.debug('geoportalApiService.login("'+username+'", "***");');
    let post = this.http.post<User>(
      this.apiURL + '/accounts/authenticate/',
    {'username': username, 'password': password},
    this.httpOptions );
    return post;
  }

  setBearer(bearer: string) {
    this.logger.debug("Bearer set." +bearer);
    this.bearer = bearer;
    this.httpOptions = {
      headers : new HttpHeaders(
        {
          'Content-Type': 'application/json',
          'Accept': 'application/json',
          'Authorization': 'Bearer ' + bearer
        }
      ) 
    };
    //this.ping();
  }

  getCurrentUser() {
    return this.http.get<User>(this.apiURL + '/accounts/currentUser/', this.httpOptions);
  }

  getBliDimensions(){
    return this.http.get<Array<BliDimension>>(this.apiURL+"/BliDimensions/all/",this.httpOptions);
  }

  getCategories(){
    return this.http.get<Array<Category>>(this.apiURL+"/Categories/all/",this.httpOptions);
  }

  getMessages(){
    return this.http.get<Array<Message>>(this.apiURL+"/Messages/all/",this.httpOptions);
  }

  updateMessage(message: Message){
    return this.http.patch(this.apiURL+"/Messages/"+message.id, message, this.httpOptions);
  }

  getEndpoint(path: string, method: string): GeoportalApiEndpoint {
  for (const endpoint of this.endpoints) {
    if (endpoint.getPath() === path && endpoint.getMethod() === method.toUpperCase()) {
      return endpoint;
    }
  }

  this.logger.debug('geoportal-api.service: Warning: Did not find endpoint for ' + method + ' on ' + path + '. Using default endpoint.');
    return this.defaultEndpoint;
  }

   
  getGeoportalElementByOsmId(id:number){
      return this.http.get(this.apiURL+"/GeoElements/byOsmId/"+id+"/",this.httpOptions);
  }
  getGeoElementByOsmId(node:OsmNode|number,type?:string){
        if(node == null){
          this.logger.debug("Cannot get element from server: argument is null");
        }

        let osmType = "";
        let osmId = -1;
        if(node instanceof OsmNode){

          if(node.osmType == "way"){
              osmType = "ways";
          }
          if(node.osmType == "node"){
              osmType = "nodes";
          }
          osmId = (node.osmId);
        }else{
          osmType = type;
          osmId = node;
        }

        if(osmType != "" && osmType != null && osmType != undefined){
          return this.http.get(this.apiURL+"/GeoElements/byOsmId/"+osmType+"/"+osmId+"/",this.httpOptions);
        }
  }

  getGeoElementById(id: number){
      return this.http.get<OsmNode>(this.apiURL+"/GeoElements/"+id+"/",this.httpOptions);
  }

  getGeoElementsByBoundingBox(rect: Rectangle){
      
      let qryStr="?minX="+rect.minX+"&minY="+rect.minY+"&maxX="+rect.maxX+"&maxY="+rect.maxY;
      return this.http.get<Array<OsmNode>>(this.apiURL+"/GeoElements/byBoundingBox/"+qryStr,this.httpOptions);
  }

  getGeoElements(){
      return this.http.get<OsmNode>(this.apiURL+"/GeoElements/all/",this.httpOptions);
  }

  peerUp(node: OsmNode, peerId: number,peerType:string){
      if(!node.id){
          this.logger.debug("error: node.id is undefined.");
          return;
      }
      let path="";
      let peers=[{'key':peerId,'value':peerType}];
      return this.http.post(this.apiURL + '/GeoElements/'+node.id+'/addPeer/', peers,this.httpOptions );
  }

  removePeer(node:OsmNode,peerId:number){
      if(!node.id){
          this.logger.debug("Error, id is undefined.");
      }

      return this.http.delete(this.apiURL + '/GeoElements/'+node.id+'/removePeer/'+peerId+'/',this.httpOptions );
  }
    
  saveGeoElement(_node: OsmNode){
     let node = _node.flatten();
     return this.http.post(this.apiURL + '/GeoElements/', node , this.httpOptions );
  }

  public updateGeoElement(_node: OsmNode){
      this.logger.debug("Updating on server");
      this.logger.debug("Sending node to server for update: "+_node.id);
      let node = _node.flatten();
      this.logger.debug("URL: "+this.apiURL + '/GeoElements/'+_node.id+"/");
      
      return this.http.patch(this.apiURL + '/GeoElements/'+_node.id, node , this.httpOptions );
  }
  
  public autocomplete(pattern:string){
      let params = new HttpParams();
      params.set('q',pattern);
      console.log(params.toString());
      //?q='+encodeURI(pattern)
      return this.http.get(this.apiURL + '/search/autocomplete/?q='+encodeURI(pattern) , this.httpOptions );
  }
  public searchGeoElements(pattern:string){
      let params = new HttpParams();
      params.set('q',pattern);
      console.log(params.toString());
      //?q='+encodeURI(pattern)
      return this.http.get(this.apiURL + '/search/?q='+encodeURI(pattern) , this.httpOptions );
  }
  public searchGeoElementsBLI(pattern:string,dimensions : Array<BliDimension>,bbox: Rectangle){
      this.logger.debug("Search-Pattern: "+pattern);
      let params = new HttpParams();
      params.set('q',pattern);
      let bboxString = '';
      if(bbox){
        let bboxString="?minX="+bbox.minX+"&minY="+bbox.minY+"&maxX="+bbox.maxX+"&maxY="+bbox.maxY;   
        params.set('minX',bbox.minX+'');
        params.set('minY',bbox.minY+'');
        params.set('maxX',bbox.maxX+'');
        params.set('maxY',bbox.maxY+'');
      }
      let dimIds = new Array<number>();
      let dimString = '';
      if(dimensions && dimensions.length > 0){
          dimString = '&';
          for(let dimension of dimensions){
              dimString += "dimensions="+dimension.id;
              params.set('dimensions',dimension.id+'');
          }
      }
      //?q='+encodeURI(pattern)+bboxString+dimString
      return this.http.get(this.apiURL + '/search/?'+encodeURI(pattern)+bboxString+dimString , this.httpOptions);
      
      
  }
  addEndpoint(endpoint: GeoportalApiEndpoint): void {
    this.logger.debug('geoportal-api.service: Adding endpoint to ApiService for ' + endpoint.getMethod() + ' on ' + endpoint.getPath());
    this.endpoints.push(endpoint);
  }


  public defaultErrorHandler(error: HttpErrorResponse): void {
  this.logger.debug('error');
  }

  public defaultResponseHandler(response: HttpResponse<any>): void {
    this.logger.debug('geoportal-api.service: Default Response handler.');
  }

}


type errorHandlerType = (error: HttpErrorResponse) => void;
type responseHandlerType = (response: HttpResponse<any>) => void;

export class GeoportalApiEndpoint {
  public constructor(
    private path: string,
    private method: string,
    private responseHandler: responseHandlerType,
    private errorHandler: errorHandlerType) {
    this.method = method.toUpperCase();
  }

  public getPath(): string {
    return this.path;
  }

  public getMethod(): string {
    return this.method;
  }
}
