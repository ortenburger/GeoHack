# GeoportalFrontend

This project was generated with [Angular CLI](https://github.com/angular/angular-cli) version 6.1.4.

## Development server

Run `ng serve` for a dev server. Navigate to `http://localhost:4200/`. The app will automatically reload if you change any of the source files.

## Code scaffolding

Run `ng generate component component-name` to generate a new component. You can also use `ng generate directive|pipe|service|class|guard|interface|enum|module`.

## Checkout dev branch

console: git checkout dev
eclipse: switch repository to "dev" branch.

## API url (optional)

When you want to use a local version of the Geoportal-backend, you need to change the api url (frontend/src/app/services/geoportal-api.service.ts) 

local: "private apiURL = 'http://127.0.0.1:8080/geoportal';"
Geoportal: "private apiURL = 'http://94.130.8.94:18080/geoportal';"

## Build

Run `ng build` to build the project. The build artifacts will be stored in the `dist/` directory. Use the `--prod` flag for a production build.

## Running unit tests

Run `ng test` to execute the unit tests via [Karma](https://karma-runner.github.io).

## Dependencies

Install the angular-cli by running `npm install "@angular/cli"`` from within the project-directory.
Install the openlayer-dependency by running `npm install ol` from within that same directory.

## Running end-to-end tests

Run `ng e2e` to execute the end-to-end tests via [Protractor](http://www.protractortest.org/).

## Further help

To get more help on the Angular CLI use `ng help` or go check out the [Angular CLI README](https://github.com/angular/angular-cli/blob/master/README.md).

## Updates

After updates to the used packets or their dependencies it can be necessary to run `npm install` from within the project-directory.  