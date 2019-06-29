import { AuthService } from '../services/auth.service';
import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { LogService } from '../services/log.service';
import { ModalManagerService } from '../services/modal-manager.service';
import { GeoportalApiService } from '../services/geoportal-api.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute, Params, RoutesRecognized } from '@angular/router';
@Component({
  selector: 'app-auth',
  templateUrl: './auth.component.html',
  styleUrls: ['./auth.component.css']
})

export class AuthComponent implements OnInit {

  @Input() asModal: boolean=false;
  @Output('closeModal') closeModal:EventEmitter<boolean> = new EventEmitter<boolean>();

  private navigated = false;
  private username: string;
  private password: string;
  private message: string;
  private sending: boolean;
  private loginForm: FormGroup;
  constructor(
    private logger: LogService, 
    private authService: AuthService,
    private formBuilder: FormBuilder, 
    private router: Router,
    private modalService: ModalManagerService,
    private activeRoute: ActivatedRoute,

    ) {

      this.logger.debug('AuthComponent instantiated.');
      this.loginForm = this.formBuilder.group({
            username: ['', Validators.required],
            password: ['', [Validators.required, Validators.minLength(6)]],
            savelogin: ['']

        });
      this.logger.debug("Getting current route.");
      this.activeRoute.firstChild.params.subscribe(
                  (params)=>{
                    this.routeParams = params;
                    this.logger.debug("Route: "+JSON.stringify(params,null,3));
                    }
                  );
  }
  private routeParams:any = null;
  get f() { 
    return this.loginForm.controls; 
  }
  
  
  login() {
    this.sending = true;
    this.logger.debug('authComponent: ' + this.loginForm.value.username);
    this.authService.login(this.loginForm.value.username, this.loginForm.value.password,this.loginForm.value.savelogin);
    this.authService.onLoggedIn.subscribe(
      (isLoggedIn)=>{
        if(isLoggedIn){
          if(this.asModal){
            setTimeout(
              ()=>{
                
                      console.log("params:"+JSON.stringify(this.routeParams,null,3));
                      switch(this.routeParams['redirectTo']){
                        case 'entryedit':{
                          this.logger.debug("Redirecting to entryEdit.");
                          this.router.navigate([this.routeParams['redirectTo'],{id:this.routeParams['id']}]);
                          this.navigated = true;
                          break;
                        }
                        case 'entrycreate':{
                          this.logger.debug("Redirecting to entryCreate.");
                          this.router.navigate([this.routeParams['redirectTo'],{id:this.routeParams['id'],type:this.routeParams['type']}]);
                          this.navigated = true;
                          break;
                        }
                        default:{
                          if(this.router.url == '/login'){
                             this.router.navigate(['home']);
                             this.navigated = true;
                             break;
                          }
                        }
                      }
                      this.modalService.close();
                
                },
                1500
            );
          }
        }
      },
      (error)=>{
        this.modalService.close();
      }
      );

    //this.closeModal.emit(true);
  }

  close(){
    
    console.log("close clicked..");
    this.modalService.close('Cross click')
    if(this.router.url.startsWith('/login') && this.navigated == false){
      this.router.navigate(['home']);
    }
  }


  ngOnInit() {
    this.navigated = false;
    this.modalService.onDismiss.subscribe(
      (closed)=>{

        this.router.navigate(['search']);
      }

      );

  }
  modalServiceSubscription : any;
  ngOnDestroy(){
    if(this.modalServiceSubscription != null){
      this.modalServiceSubscription.unsubscribe();
    }
  }

}
