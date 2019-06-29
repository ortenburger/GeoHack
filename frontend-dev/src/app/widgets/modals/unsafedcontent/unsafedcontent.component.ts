import { Component, OnInit, Input } from '@angular/core';
import { MarkdownService } from 'ngx-markdown';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-unsafedcontent',
  templateUrl: './unsafedcontent.component.html',
  styleUrls: ['./unsafedcontent.component.css']
})
export class UnsafedcontentComponent implements OnInit {

@Input() asModal: boolean;

  constructor(
  	private activeModal: NgbActiveModal 
  ) { }

  ngOnInit() {
  }

}
