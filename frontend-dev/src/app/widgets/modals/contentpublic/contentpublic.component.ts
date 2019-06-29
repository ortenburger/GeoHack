import { Component, OnInit, Input } from '@angular/core';
import { MarkdownService } from 'ngx-markdown';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-contentpublic',
  templateUrl: './contentpublic.component.html',
  styleUrls: ['./contentpublic.component.css']
})
export class ContentPublicComponent implements OnInit {

@Input() asModal: boolean;

  constructor(
  	private activeModal: NgbActiveModal 
  ) { }

  ngOnInit() {
  }

}
