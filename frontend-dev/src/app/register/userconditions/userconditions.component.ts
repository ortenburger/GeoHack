import { Component, OnInit, Input } from '@angular/core';
import { MarkdownService } from 'ngx-markdown';

@Component({
  selector: 'app-userconditions',
  templateUrl: './userconditions.component.html',
  styleUrls: ['./userconditions.component.css']
})
export class UserconditionsComponent implements OnInit {

  @Input() asModal: boolean;
  

  constructor() { }

  ngOnInit() {
  }

}
