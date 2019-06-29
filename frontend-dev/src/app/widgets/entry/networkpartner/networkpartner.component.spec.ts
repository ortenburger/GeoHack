import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NetworkpartnerComponent } from './networkpartner.component';

describe('NetworkpartnerComponent', () => {
  let component: NetworkpartnerComponent;
  let fixture: ComponentFixture<NetworkpartnerComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ NetworkpartnerComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NetworkpartnerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
