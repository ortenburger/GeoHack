import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { OsmidinfoComponent } from './osmidinfo.component';

describe('OsmidinfoComponent', () => {
  let component: OsmidinfoComponent;
  let fixture: ComponentFixture<OsmidinfoComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ OsmidinfoComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(OsmidinfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
