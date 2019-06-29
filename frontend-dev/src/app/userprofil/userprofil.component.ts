import { Component, OnInit } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { User } from '../models/User';
import { Router } from '@angular/router';
import { LogService } from '../services/log.service';

@Component({
  selector: 'app-userprofil',
  templateUrl: './userprofil.component.html',
  styleUrls: ['./userprofil.component.css']
})
export class UserprofilComponent implements OnInit {
  currentUser: User;
  debug : boolean =
   false; 
  
  constructor(
    private router:Router, 
    private authService: AuthService,
    private logger: LogService
    ) {
    this.logger.debug("Constructing UserProfilComponent.");
    this.currentUser = this.authService.currentUser;
  }

  ngOnInit() {
  }

}
