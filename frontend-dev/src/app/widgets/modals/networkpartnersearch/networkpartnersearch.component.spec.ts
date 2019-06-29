import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NetworkpartnersearchComponent } from './networkpartnersearch.component';

describe('NetworkpartnersearchComponent', () => {
  let component: NetworkpartnersearchComponent;
  let fixture: ComponentFixture<NetworkpartnersearchComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ NetworkpartnersearchComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NetworkpartnersearchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
