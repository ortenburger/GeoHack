import { Component, OnInit,Input } from '@angular/core';
import { OsmNode } from '../../../models/OsmNode';
import { ModalManagerService } from '../../../services/modal-manager.service';
import { FormsModule ,FormBuilder, ReactiveFormsModule, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-networkpartner',
  templateUrl: './networkpartner.component.html',
  styleUrls: ['./networkpartner.component.css']
})
export class NetworkpartnerComponent implements OnInit {
@Input('node') node : OsmNode;
@Input('form') form : FormGroup;
  constructor(
  	private modalService: ModalManagerService
  ) { }

  ngOnInit() {
  }

}
