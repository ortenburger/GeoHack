import { Component, OnInit } from '@angular/core';
import { LogService } from '../../services/log.service';
import { AuthService } from '../../services/auth.service';
import { BliService } from '../../services/bli.service';
import { BliDimension } from '../../models/BliDimension';

@Component({
  selector: 'app-bli-check',
  templateUrl: './bli-check.component.html',
  styleUrls: ['./bli-check.component.css']
})
export class BliCheckComponent implements OnInit {

  dimensions : Array<BliDimension>;
  constructor(
  private logger: LogService,
  private bliService: BliService,
  private authService: AuthService) { 
    this.dimensions = bliService.dimensions;
  }
  
  ngOnInit() {
  
  }
}
