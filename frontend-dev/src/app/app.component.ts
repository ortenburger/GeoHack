import { Component, OnInit} from '@angular/core';
import { NgxSpinnerService } from 'ngx-spinner'; 
@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit{
  title = 'Geoportal-Frontend';
  footer='light';

  constructor(private spinner: NgxSpinnerService) {
  }
  
  ngOnInit() {
    /** spinner starts on init */
    this.spinner.show();
 
    setTimeout(() => {
        /** spinner ends after 5 seconds */
        this.spinner.hide();
        
    }, 3000);
  }
}
