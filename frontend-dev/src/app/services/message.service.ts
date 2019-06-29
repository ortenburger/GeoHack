import { Injectable } from '@angular/core';
import { GeoportalApiService } from './geoportal-api.service';
import { CacheService } from './cache.service';
import { AuthService } from './auth.service';
import { Message } from '../models/Message';
import { User } from '../models/User';
import { LogService } from './log.service';
@Injectable({
  providedIn: 'root'
})
export class MessageService {
	inboxMessages: Array<Message>;
	sentMessages: Array<Message>;
  constructor(
  	private apiService: GeoportalApiService, 
  	private cache: CacheService,
  	private logger: LogService, 
  	private authService: AuthService
  	) {
      this.logger.debug("Getting messages from server.");
  		apiService.getMessages().subscribe(
  			(response) => {this.parseResponse(response);},
  			(error) => { this.parseError(error); });
  }
  setRead(message: Message){
    message.read = new Date();
    message.isRead = true;
    this.apiService.updateMessage(message);
  }
  parseResponse(response){
  	this.logger.debug("messages.service: got response from messages: "+JSON.stringify(response));
  }
  parseError(error){
  	this.logger.debug("messages.service: error catching messages: "+JSON.stringify(error));
  	
  }
}
