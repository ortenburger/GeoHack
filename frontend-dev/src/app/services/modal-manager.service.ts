import { Injectable,ComponentFactoryResolver, ViewContainerRef } from '@angular/core';
import { NgbModal, NgbModalConfig, NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Subject, isObservable } from 'rxjs';

import { AuthComponent } from '../auth/auth.component';
import { CreateEntryComponent } from '../portal/create-entry/create-entry.component';
import { UserconditionsComponent } from '../register/userconditions/userconditions.component';
import { ContentPublicComponent } from '../widgets/modals/contentpublic/contentpublic.component';
import { UnsafedcontentComponent } from '../widgets/modals/unsafedcontent/unsafedcontent.component';
import { TermsofuseComponent } from '../widgets/modals/termsofuse/termsofuse.component';
import { AddsomethingComponent } from '../widgets/modals/addsomething/addsomething.component';
import { NetworkpartnersearchComponent } from '../widgets/modals/networkpartnersearch/networkpartnersearch.component';
import { LoginComponent } from '../widgets/modals/login/login.component';
import { EditsourceComponent } from '../widgets/modals/editsource/editsource.component';

@Injectable({
  providedIn: 'root'
})
export class ModalManagerService {
	private modalRef: any;
  private currentComp :any;
  reference:any;
  viewContainerRef: any;
  
  onDismiss = new Subject<boolean>();
  onClose = new Subject<boolean>();
  onOpen = new Subject<boolean>();
  registerViewContainer(view:any){
    console.log("registering view container ref.");
    this.viewContainerRef = view;  
  }

  constructor(private modalService: NgbModal, private modalConfig: NgbModalConfig,private cfr: ComponentFactoryResolver) { 
    
  }

  resolveComponent(componentType:string):any{
    
    switch(componentType){
      case 'createEntry':{}
      case 'CreateEntryComponent':{
        return UnsafedcontentComponent;
      }
      case 'addSomething':{}
      case 'AddSomethingComponent':{
        return AddsomethingComponent;
      }
      case 'userConditions':{}
      case 'UserconditionsComponent':{
        return UserconditionsComponent;
      }
      case 'contentPublic':{}
      case 'ContentPublicComponent':{
        return ContentPublicComponent;
      }
      case 'editsource':{}
      case 'EditsourceComponent':{
        return EditsourceComponent;
      }
      case 'auth':{}
      case 'login':{}
      case 'AuthComponent':{
        return AuthComponent;
      }
      case 'networkpartnersearch':{}
      case 'networkpartnersearch':{}
      case 'NetworkpartnersearchComponent':{
        return NetworkpartnersearchComponent;
      }
      default :{
        return componentType;
      }
    }
  }
  removeComponent(){ 
    if(this.reference){
      this.reference.destroy();
    }
    this.viewContainerRef.clear();
  }

  
  configureComponent(comp: string){
    let component = this.resolveComponent(comp);
    if(component === null){
      console.log("Cannot resolve component: "+comp);
      return null;
    }
    if(this.viewContainerRef == null || this.viewContainerRef == undefined){
      console.log("ViewContainerRef is undefined/null");
      return null;
    }
    return component;
    /*
    const factory = this.cfr.resolveComponentFactory(component);
    
    if(this.reference){
      this.reference.destroy();
      this.reference = null;
    }
    this.reference = this.viewContainerRef.createComponent(factory);
    this.reference.instance.asModal = true;

    this.reference.changeDetectorRef.detectChanges();

    return this.reference;
    */
  }

  // doku: https://github.com/ng-bootstrap/ng-bootstrap/blob/master/src/modal/modal-config.ts
  
  open(_comp:any, dismissable?: boolean,options?: any,data?:any){
    this.close();

    let comp = this.configureComponent(this.resolveComponent(_comp));
    if(comp === null){
      comp=_comp;
    }else{
      console.log("Component Loaded successfully: "+_comp);
    }

    if(!dismissable){
      this.modalConfig.backdrop = 'static';
    }else{
      this.modalConfig.backdrop = true;
    }
    if(options){
      for(let key in options){
        this.modalConfig[key]=options[key];
      }
    }
    this.modalConfig['backdrop']=true;

    this.modalConfig['keyboard']=true;
    this.modalConfig['beforeDismiss']=()=>this.onDismiss.next(true);
    this.modalRef = this.modalService.open(comp,this.modalConfig);
    this.modalRef.onDismiss
    this.modalRef.componentInstance.asModal = true;
    if(data){
      for(let key in data){
        this.modalRef.componentInstance[key]=data[key];
      }
    }
    if(typeof this.modalRef.componentInstance['close'] == "function" && isObservable(this.modalRef.componentInstance['close'])){
      this.subscription$=this.modalRef.componentInstance['close'].subscribe(
        (event)=>{
          this.close();
        }
      )
    }
    this.onOpen.next(true);
  }
  subscription$:any;


  openLg(comp:any, dismissable?: boolean){
    this.open(comp,dismissable,{size:'lg'});
  }
  openSm(comp:any, dismissable?: boolean){
    this.open(comp,dismissable,{size:'sm'});
  }
  
  openCentered(comp:any, dismissable?: boolean){
    this.open(comp,dismissable,{centered:true});
  }
  resetModal(){
    this.clearSubscriptions();
    this.removeComponent();
  }
  dismiss(reason?:any){
    this.onDismiss.next(true);
    this.resetModal();
    if(this.modalRef){
      this.modalRef.dismiss(reason);
    }
    this.modalRef=null;
  }
  clearSubscriptions(){
    if(this.subscription$){
        this.subscription$.unsubscribe();
        this.subscription$=null;
    }
  }
      
  close(reason?:any){
    this.onClose.next(true);
    this.resetModal();
    if(this.modalRef){
      this.modalRef.close(reason);
    }
    this.modalRef=null;
    
  }
  currentModal(){
  	return this.modalRef;
  }
}
