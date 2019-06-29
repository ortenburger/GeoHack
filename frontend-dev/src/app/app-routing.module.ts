import { NgModule } from '@angular/core';
import { RouterModule, Routes, RouterOutlet } from '@angular/router';
import { HomeComponent } from './home';
import { AuthComponent } from './auth';
import { RegisterComponent } from './register';
import { UserconditionsComponent } from './register/userconditions';
import { MessagingComponent } from './messaging';
// import { NavigationComponent } from './navigation/navigation.component' ;
import { SearchComponent } from './search';
import { ImprintComponent } from './home/imprint';
import { UserprofilComponent } from './userprofil/userprofil.component';
import { TempDevComponent } from './portal/temp-dev/temp-dev.component';
import { ContactComponent } from './home/contact';
import { PrivacypolicyComponent } from './home/privacypolicy';
import { BliComponent } from './portal/bli/bli.component';
import { CategoryComponent } from './portal/category/category.component';
import { CreateEntryComponent } from './portal/create-entry/create-entry.component';
import { HelpComponent } from './portal/help/help.component';
import { ErrorComponent } from './error';
import { AutosuggestComponent } from './widgets/autosuggest';
import { VersionInfoComponent } from './home/version-info/version-info.component';
import { ContributeComponent } from './home/contribute/contribute.component';
import { ModalComponent } from './widgets/modals/modal/modal.component';
import { EditComponent } from './widgets/entry/edit/edit.component';
import { CreateComponent } from './widgets/entry/create/create.component';
import { LoginComponent } from './widgets/modals/login/login.component';
/// staging.
import { MarkdownComponent } from './staging/markdown/markdown.component';


const routes: Routes = [
  { path: 'autosuggest', component: AutosuggestComponent },
  { path: 'home', component: HomeComponent },
  { path: 'login', component: LoginComponent },

  { path: 'auth', component: AuthComponent },
  { path: 'help', component: HelpComponent },
  { path: 'error', component: ErrorComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'messages', component: MessagingComponent },
  { path: 'search', component: SearchComponent },
  { path: 'imprint', component: ImprintComponent },
  { path: 'versionInfo', component: VersionInfoComponent },
  { path: 'userconditions', component: UserconditionsComponent },
  { path: 'contact', component: ContactComponent },
  { path: 'privacypolicy', component: PrivacypolicyComponent },
  { path: 'userprofile', component: UserprofilComponent },
  { path: 'temp-dev', component: TempDevComponent },
  { path: 'bliDimensions', component: BliComponent},
  { path: 'categories', component: CategoryComponent},
  { path: 'createEntry', component: CreateEntryComponent },
  { path: 'entryedit', component: EditComponent },
  { path: 'entrycreate', component: CreateComponent },
  { 
    path: 'playground', 
    children: [
      { path: 'modal', component: ModalComponent },
      { path: 'markdown', component: MarkdownComponent }
    ]
  },

  { path: 'contribute', component: ContributeComponent },
  { path: '', component: SearchComponent }, //startpage
  { path: '**', redirectTo: 'error'},

  ];

@NgModule({
  imports: [ RouterModule.forRoot(routes, {useHash: true}) ],
  exports: [ RouterModule ]
})
export class AppRoutingModule {}

