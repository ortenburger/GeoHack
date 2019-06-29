import { Component, OnInit, Input, Output, EventEmitter, AfterViewInit, ViewChild, Compiler, Injector, NgModule, NgModuleRef, TemplateRef, ViewContainerRef, ComponentFactoryResolver } from '@angular/core';
import { ModalManagerService } from '../../../services/modal-manager.service';
//import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { AuthComponent } from '../../../auth/auth.component';
import { CreateEntryComponent } from '../../../portal/create-entry/create-entry.component';
import { UserconditionsComponent } from '../../../register/userconditions/userconditions.component';
import { ContentPublicComponent } from '../../../widgets/modals/contentpublic/contentpublic.component';
import { UnsafedcontentComponent } from '../../../widgets/modals/unsafedcontent/unsafedcontent.component';
import { TermsofuseComponent } from '../../../widgets/modals/termsofuse/termsofuse.component';
import { NetworkpartnersearchComponent } from '../../../widgets/modals/networkpartnersearch/networkpartnersearch.component';
import { EditsourceComponent } from '../../../widgets/modals/editsource/editsource.component';

@Component({
	selector: 'app-modal',
	template:'<div #content></div>',
	//templateUrl: './modal.component.html',
	styleUrls: ['./modal.component.css']
})
export class ModalComponent implements OnInit {
	@ViewChild('content',{ read: ViewContainerRef} ) viewContainerRef: ViewContainerRef;
	//@ViewChild('templateRef') templateRef: TemplateRef<any>;

	modalOpened$:any;
	modalClosed$:any;
	
	constructor(
		private modalService: ModalManagerService,
		private cfr: ComponentFactoryResolver,
		) {
		

	}

	ngOnInit() {
		console.log("init / modalComponent");
		this.modalService.registerViewContainer(this.viewContainerRef);
	}
	ngOnDestroy(){
		this.modalService.registerViewContainer(null);
	}
}
