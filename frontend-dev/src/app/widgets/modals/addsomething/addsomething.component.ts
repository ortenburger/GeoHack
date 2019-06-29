import { Component, OnInit, Input } from '@angular/core';
import { MarkdownService } from 'ngx-markdown';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-addsomething',
  templateUrl: './addsomething.component.html',
  styleUrls: ['./addsomething.component.css']
})
export class AddsomethingComponent implements OnInit {

@Input() asModal: boolean;

  constructor(private activeModal: NgbActiveModal ) { 
  	
  }

  ngOnInit() {
  }

}
