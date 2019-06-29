import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { NotifierService } from 'angular-notifier';
@Injectable({
  providedIn: 'root'
})
export class LogService {
  private _debug = false;
  private readonly notifier: NotifierService;
  constructor(notifierService: NotifierService) {
    this.notifier = notifierService;
    this._debug = (!environment.production);
  }

  enableDebugging(): void {
    this._debug = true;
  }

  debug(msg: string): void {
    if (this._debug) {
      console.log(msg);
    }
  }
    
  notify(channel: string, message: string, id?: string){
      this.notifier.notify(channel,message,id);
  }



}
