import { Component, OnInit, Input, OnDestroy } from '@angular/core';
import { MarkdownService } from 'ngx-markdown';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ModalManagerService } from '../../../services/modal-manager.service';
import { AuthService } from '../../../services/auth.service';
import { Router } from '@angular/router';
@Component({
  selector: 'app-login',
  //templateUrl: './login.component.html',
  template:'<div></div>',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

@Input() asModal: boolean;

  constructor(
  	private modalManager: ModalManagerService,
  	private authService: AuthService,
    private router : Router,
  ) { }

  ngOnInit() {
  	this.modalManager.open('AuthComponent');
  }

}
