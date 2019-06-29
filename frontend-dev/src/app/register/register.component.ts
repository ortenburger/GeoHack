import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { first, catchError, map, tap } from 'rxjs/operators';

import { LogService } from '../services/log.service';
import { GeoportalApiService } from '../services/geoportal-api.service';
import { AuthService } from '../services/auth.service';
import { User } from '../models/User';
import { Observable ,  throwError } from 'rxjs';
import { HttpClient, HttpHeaders, HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { HttpEventType } from '@angular/common/http';
@Component({
  templateUrl: 'register.component.html',
  styleUrls: ['./register.component.css']}
)
export class RegisterComponent implements OnInit {
    registerForm: FormGroup;
    loading = false;
    submitted = false;
    shoForm = true;
    message: string;
    constructor(
        private formBuilder: FormBuilder,
        private router: Router,
        private logger: LogService,
        private apiService: GeoportalApiService,
        private authService: AuthService
      ) { }

    ngOnInit() {
        this.registerForm = this.formBuilder.group({
            email: ['example@example.com', Validators.required],
            username: ['testuser', Validators.required],
            password: ['testuser', [Validators.required, Validators.minLength(6)]],
            firstname: ['test'],
            lastname: ['user'],
            description: ['description']
        });
    }

    // convenience getter for easy access to form fields
    get f() { return this.registerForm.controls; }

    onSubmit() {
        this.submitted = true;

        // stop here if form is invalid
        if (this.registerForm.invalid) {
          this.logger.debug('Error: Invalid data.');
          return;
        } else {
          this.loading = true;
          this.apiService.registerUser(this.registerForm.value as User)
            .pipe(
              tap(data => this.logger.debug('fetched some data: ' + JSON.stringify(data))),
              catchError(
                (error) => {
                  this.logger.debug('error:');
                  this.logger.debug(JSON.stringify(error) );
                  if (error.status) {
                    if (error.status === 409) {
                      this.message = 'username or email already in use.';
                      this.loading = false;
                      this.logger.notify('error',this.message);
                    }
                    if(error.status){
                      //this.message = 'Registration of new Users seems to be disabled by server.';
                      this.message = 'Die Registrierung ist aktuell leider noch nicht möglich.';
                      this.loading = false;

                      this.logger.notify('error',this.message);
                    }
                  }
                  this.logger.debug('end of error');
                  return throwError(error);
                }
              )
            )
            .subscribe(
              response => {
                this.message = 'User created.';
                this.logger.debug('user.' + JSON.stringify(response));
                this.router.navigate(['login']);
              },
              error => {
                this.logger.debug('errorpath.');
              }
          );
              this.loading = false;
      }

      this.loading = false;
      }

}

