import { Component, OnInit } from '@angular/core';
import { User } from '../../models/User';
import { AuthService } from '../../services/auth.service';
@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit {

  constructor(private authService: AuthService) {
	authService.currentUser;
   }

  ngOnInit() {
  }

}
