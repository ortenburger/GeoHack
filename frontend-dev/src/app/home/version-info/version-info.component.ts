import { Component, OnInit } from '@angular/core';
import { environment } from '../../../environments/environment';
@Component({
  selector: 'app-version-info',
  templateUrl: './version-info.component.html',
  styleUrls: ['./version-info.component.css']
})
export class VersionInfoComponent implements OnInit {
public version: string = environment.VERSION;
  constructor() { }

  ngOnInit() {
  }

}
