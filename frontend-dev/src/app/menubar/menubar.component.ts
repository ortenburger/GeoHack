import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { User } from '../models/User';
import { AuthService } from '../services/auth.service';
import { LogService } from '../services/log.service';
import { ModalManagerService } from '../services/modal-manager.service';
import { GeoportalApiService } from '../services/geoportal-api.service';
@Component({
  selector: 'app-menubar',
  templateUrl: './menubar.component.html',
  styleUrls: ['./menubar.component.css']
})
export class MenubarComponent implements OnInit {
  
  constructor(private authService: AuthService, private apiService: GeoportalApiService, private logger:LogService,private modalService: ModalManagerService, private router:Router) {
    
  }
    
  logout(){
      if(this.authService.logout()){
          this.logger.notify("info","Logged out.");
          this.router.navigate(['search']);
      }
  }
  ngOnInit() {
  }

}
