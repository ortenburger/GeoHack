import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UserconditionsComponent } from './userconditions.component';

describe('UserconditionsComponent', () => {
  let component: UserconditionsComponent;
  let fixture: ComponentFixture<UserconditionsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UserconditionsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UserconditionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
