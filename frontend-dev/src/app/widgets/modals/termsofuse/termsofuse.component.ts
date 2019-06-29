import { Component, OnInit, Input } from '@angular/core';
import { MarkdownService } from 'ngx-markdown';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-termsofuse',
  templateUrl: './termsofuse.component.html',
  styleUrls: ['./termsofuse.component.css']
})
export class TermsofuseComponent implements OnInit {

@Input() asModal: boolean;

  constructor(
  	private activeModal: NgbActiveModal 
  ) { }

  ngOnInit() {
  }

}
