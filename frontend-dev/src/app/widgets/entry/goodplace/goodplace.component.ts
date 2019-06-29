import { Component, OnInit,Input } from '@angular/core';
import { OsmNode } from '../../../models/OsmNode';
import { FormsModule ,FormBuilder, ReactiveFormsModule, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-goodplace',
  templateUrl: './goodplace.component.html',
  styleUrls: ['./goodplace.component.css']
})
export class GoodplaceComponent implements OnInit {
	@Input('node') node : OsmNode;
	@Input('form') form : FormGroup;
  constructor() { }

  ngOnInit() {
  }

}
