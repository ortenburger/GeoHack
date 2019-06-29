import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse, HttpErrorResponse,HttpParams } from '@angular/common/http';
import { LogService } from '../services/log.service';
import { CacheService } from '../services/cache.service';
import { map, tap, catchError} from 'rxjs/operators';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
    
export class NominatimService {

  apiURL="https://nominatim.openstreetmap.org/"
  constructor(private httpClient: HttpClient, private cache: CacheService, private logger: LogService) { }
  search(term:string){
      return this.httpClient.get(this.apiURL+"?q="+encodeURI(term)+"&format=jsonv2");
  }
}
