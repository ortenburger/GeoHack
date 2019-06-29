import { Injectable } from '@angular/core';
import { User } from '../models/User';
import { GeoportalApiService } from './geoportal-api.service';
import { LogService } from './log.service';
import { Observable,Subject  } from 'rxjs';
import { Router } from '@angular/router';
import { CacheService } from './cache.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
@Injectable({
  providedIn: 'root'
})

export class AuthService {

  currentUser: User;
  loggedIn: boolean = false;
  profileRetrieved: boolean = false;
  loginFailed: boolean = false;
  bearer: string;
  public onLoggedIn:Subject<boolean>;
  constructor(private apiService: GeoportalApiService, private logger: LogService, private router:Router,private cache:CacheService) {
    this.logger.debug("auth.service: Trying to load currentUser.");
    this.onLoggedIn = new Subject<boolean>();
    let currentUserObject:User;
    if(this.cache.has('currentUserObject')){
        currentUserObject = this.cache.get('currentUserObject');
    }
    if(currentUserObject == null){
      this.logger.debug("auth.service: failed to load user from localstorage.");
    }else{
      this.currentUser = new User(currentUserObject);
      this.profileRetrieved=true;
      this.loginFailed=false;
      this.loggedIn=true;
      apiService.loadBearer();
    }
  }

  login(username: string, password: string, savelogin: boolean) {
    this.apiService.login(username, password)
            .subscribe(
              response => {

                if ( response['Bearer'] ) {

                  this.bearer = response['Bearer'];

                  this.loggedIn = true;
                  this.loginFailed = false;
                  this.currentUser = new User(response);

                  this.apiService.setBearer(this.bearer);

                  this.apiService.getCurrentUser().subscribe(
                    res => {
                      console.log("Userdata:" +JSON.stringify(res,null,3));
                      this.currentUser.setData( res );

                      this.logger.debug( this.currentUser.toString() );
                      this.profileRetrieved=true;
                      this.logger.notify('success','Login erfolgreich.');
                      this.onLoggedIn.next(true);
                      if( savelogin ) {
                        this.saveLogin();
                      }else{
                        this.logger.debug("auth.service: not saving login: "+JSON.stringify(savelogin));
                      }
                    }
                  );

                }
              },
              error => {
                this.logger.debug('auth.service: errorpath.');
                this.logger.notify('error','Login fehlgeschlagen.');
                this.loginFailed = true;
                this.loggedIn = false;
              }
          );

  }
  logout(){
      this.profileRetrieved = false;
      this.loggedIn = false;
      this.currentUser.clear();
      this.apiService.deleteBearer();
      this.cache.remove("currentUserObject");
      this.logger.debug("logging out.");
      //this.router.navigate(['login']);
      return true;
  }

  saveLogin(){
    this.logger.debug("auth.service: Saving current user.");
    this.cache.set("currentUserObject",this.currentUser);
    this.logger.debug("auth.service: Saved to localStorage: "+JSON.stringify(this.currentUser));
    this.apiService.saveBearer();
    this.logger.debug("Bearer saved.");
  }


  getCurrentUser(): User {
    return this.currentUser;
  }


  isLoggedIn(): boolean {
    return this.loggedIn;
  }


  setUser(user: User) {
    this.currentUser = user;
  }


  setLoggedIn(loggedIn: boolean) {
    this.loggedIn = loggedIn;
  }
}
