import { Component, OnInit } from '@angular/core';

import { environment } from '../../environments/environment';
@Component({
  selector: 'app-error',
  templateUrl: './error.component.html',
  styleUrls: ['./error.component.css']
})
export class ErrorComponent implements OnInit {
	public version: string = environment.VERSION;

  constructor() { }

  ngOnInit() {
  }

}
