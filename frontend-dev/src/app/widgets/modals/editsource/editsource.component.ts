import { Component, OnInit, Input } from '@angular/core';
import { MarkdownService } from 'ngx-markdown';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-editsource',
  templateUrl: './editsource.component.html',
  styleUrls: ['./editsource.component.css']
})
export class EditsourceComponent implements OnInit {

@Input() asModal: boolean;

  constructor(
  	private activeModal: NgbActiveModal 
  ) { }

  ngOnInit() {
  }

}
