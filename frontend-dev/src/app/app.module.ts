import { BrowserModule } from '@angular/platform-browser';
import { LOCALE_ID, NgModule, enableProdMode } from '@angular/core';
import { FormsModule, ReactiveFormsModule  } from '@angular/forms';
import { MarkdownModule } from 'ngx-markdown';
import { HashLocationStrategy, Location, LocationStrategy } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { NgxSpinnerModule } from 'ngx-spinner';
import { NotifierModule } from 'angular-notifier';
import localeDe from '@angular/common/locales/de';



import { GeoportalApiService } from './services/geoportal-api.service';
import { LogService } from './services/log.service';
import { AppComponent } from './app.component';
import { AuthComponent } from './auth/auth.component';
import { HomeComponent } from './home/home.component';
import { AppRoutingModule } from './app-routing.module';
import { RegisterComponent } from './register/register.component';
import { OpenlayerComponent } from './openlayer/openlayer.component';
import { MessagingComponent } from './messaging/messaging.component';
// import { NavigationComponent } from './navigation/navigation.component';
import { SearchComponent } from './search/search.component';
import { FooterComponent } from './footer/footer.component';
//import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { FooterLightComponent } from './footer/footer-light/footer-light.component';
import { UserprofilComponent } from './userprofil/userprofil.component';
import { MessagingSearchComponent } from './messaging/messaging-search/messaging-search.component';
import { UserComponent } from './userprofil/user/user.component';
import { AvatarComponent } from './userprofil/avatar/avatar.component';
import { MenubarComponent } from './menubar/menubar.component';
import { ImprintComponent } from './home/imprint/imprint.component';
import { TempDevComponent } from './portal/temp-dev/temp-dev.component';
import { FreesearchComponent } from './portal/freesearch/freesearch.component';
import { OsmidinfoComponent } from './portal/osmidinfo/osmidinfo.component';
import { OsmidaddComponent } from './portal/osmidadd/osmidadd.component';
import { PrivacypolicyComponent } from './home/privacypolicy/privacypolicy.component';
import { ContactComponent } from './home/contact/contact.component';
import { ContributeComponent } from './home/contribute/contribute.component';
import { BliComponent } from './portal/bli/bli.component';
import { BliCheckComponent } from './portal/bli-check/bli-check.component';
import { CategoryComponent } from './portal/category/category.component';
import { ComposeMessageComponent } from './messaging/compose-message/compose-message.component';
import { CreateEntryComponent } from './portal/create-entry/create-entry.component';
import { ErrorComponent } from './error/error.component';
import { UserconditionsComponent } from './register/userconditions/userconditions.component';
import { NodeFilterPipe } from './pipes/node-filter.pipe';
import { BliEditorComponent } from './admin/bli-editor/bli-editor.component';
import { AutosuggestComponent } from './widgets/autosuggest/autosuggest.component';
import { VersionInfoComponent } from './home/version-info/version-info.component';
import { HelpComponent } from './portal/help/help.component';

import { ModalComponent } from './widgets/modals/modal/modal.component';
import { KontaktformularComponent } from './pages/kontaktformular/kontaktformular.component';
import { MarkdownComponent } from './staging/markdown/markdown.component';
import { ContentPublicComponent } from './widgets/modals/contentpublic/contentpublic.component';
import { UnsafedcontentComponent } from './widgets/modals/unsafedcontent/unsafedcontent.component';
import { TermsofuseComponent } from './widgets/modals/termsofuse/termsofuse.component';
import { NetworkpartnersearchComponent } from './widgets/modals/networkpartnersearch/networkpartnersearch.component';
import { AddsomethingComponent } from './widgets/modals/addsomething/addsomething.component';
import { LoginComponent } from './widgets/modals/login/login.component';
import { AddcategoryComponent } from './widgets/modals/addcategory/addcategory.component';
import { EditComponent } from './widgets/entry/edit/edit.component';
import { CreateComponent } from './widgets/entry/create/create.component';
import { NetworkpartnerComponent } from './widgets/entry/networkpartner/networkpartner.component';
import { OsminformationComponent } from './widgets/entry/osminformation/osminformation.component';
import { GoodplaceComponent } from './widgets/entry/goodplace/goodplace.component';
import { EditsourceComponent } from './widgets/modals/editsource/editsource.component';

enableProdMode();

@NgModule( {
  declarations: [
    AppComponent,
    AuthComponent,
    HomeComponent,
    RegisterComponent,
    OpenlayerComponent,
    MessagingComponent,
    // NavigationComponent,
    SearchComponent,
    FooterComponent,
    FooterLightComponent,
    UserprofilComponent,
    MessagingSearchComponent,
    UserComponent,
    AvatarComponent,
    MenubarComponent,
    ImprintComponent,
    TempDevComponent,
    FreesearchComponent,
    OsmidinfoComponent,
    OsmidaddComponent,
    PrivacypolicyComponent,
    ContactComponent,
    BliComponent,
    BliCheckComponent,
    CategoryComponent,
    ComposeMessageComponent,
    CreateEntryComponent,
    ErrorComponent,
    UserconditionsComponent,
    NodeFilterPipe,
    BliEditorComponent,
    AutosuggestComponent,
    VersionInfoComponent,
    HelpComponent,
    ModalComponent,
    ContributeComponent,
    KontaktformularComponent,
    MarkdownComponent,
    ContentPublicComponent,
    UnsafedcontentComponent,
    TermsofuseComponent,
    NetworkpartnersearchComponent,
    AddsomethingComponent,
    LoginComponent,
    AddcategoryComponent,
    EditComponent,
    CreateComponent,
    NetworkpartnerComponent,
    OsminformationComponent,
    GoodplaceComponent,
    EditsourceComponent,
    //MatAutocompleteModule,
  ],
  imports: [
    BrowserModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    AppRoutingModule,
    NgbModule,
    NgxSpinnerModule,
    NotifierModule,
    MarkdownModule.forRoot()
  ],
  entryComponents:[
    CreateEntryComponent,
    ModalComponent,
    MarkdownComponent,
    ContentPublicComponent,
    UnsafedcontentComponent,
    TermsofuseComponent,
    NetworkpartnersearchComponent,
    AddsomethingComponent,
  ],
  providers: [
    LogService,
    GeoportalApiService,
    {provide: LOCALE_ID, useValue: 'de'}
  ],
  bootstrap: [AppComponent]
})
export class AppModule { 


}

