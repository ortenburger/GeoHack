import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AddsomethingComponent } from './addsomething.component';

describe('AddsomethingComponent', () => {
  let component: AddsomethingComponent;
  let fixture: ComponentFixture<AddsomethingComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AddsomethingComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddsomethingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
