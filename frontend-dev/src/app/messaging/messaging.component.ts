import { Component, OnInit } from '@angular/core';
import { MessageService } from '../services/message.service';
import { AuthService } from '../services/auth.service';
import { LogService } from '../services/log.service';
import { User } from '../models/User';
import { Message } from '../models/Message';
@Component({
  selector: 'app-messaging',
  templateUrl: './messaging.component.html',
  styleUrls: ['./messaging.component.css']
})
export class MessagingComponent implements OnInit {

  currentView: string = 'inbox';
  currentMessage: Message;
  currentUser: User;
  inboxMessages: Array<Message>;
  sentMessages: Array<Message>;
  constructor(
    private authService: AuthService, 
    private messageService: MessageService,
    private logger: LogService) {
    this.currentUser = authService.currentUser;
    this.currentView = 'inbox';
  }

  showSent() {
    this.currentView = 'sent';
  }
  
  showInbox() {
    this.currentView = 'inbox';
  }
  composeMessage() {
    this.currentMessage = new Message({});
    this.currentView = 'compose';
  }
  showMessage(id: number) {
    let message = this.getMessage(id);
    this.messageService.setRead(message);
  }

  getMessage(id: number): Message {
    if ( this.currentView == 'inbox' ) {
      if ( this.inboxMessages ) {
        for ( let message of this.inboxMessages) {
          if ( message.id == id ) {
            return message;
          }
        }
      }
    }
    if (this.currentView == 'sent') {
      if ( this.sentMessages ) {
        for ( let message of this.sentMessages ) {
          if (message.id == id) {
            return message;
          }
        }
      }
    }
  }

  ngOnInit() {
    this.inboxMessages = this.messageService.inboxMessages;
    this.sentMessages = this.messageService.sentMessages;
  }

}
