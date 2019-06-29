import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UnsafedcontentComponent } from './unsafedcontent.component';

describe('UnsafedcontentComponent', () => {
  let component: UnsafedcontentComponent;
  let fixture: ComponentFixture<UnsafedcontentComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UnsafedcontentComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UnsafedcontentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
