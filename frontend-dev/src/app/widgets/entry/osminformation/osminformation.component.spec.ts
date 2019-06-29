import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { OsminformationComponent } from './osminformation.component';

describe('OsminformationComponent', () => {
  let component: OsminformationComponent;
  let fixture: ComponentFixture<OsminformationComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ OsminformationComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(OsminformationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
