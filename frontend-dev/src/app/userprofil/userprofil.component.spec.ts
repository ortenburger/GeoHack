import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UserprofilComponent } from './userprofil.component';

describe('UserprofilComponent', () => {
  let component: UserprofilComponent;
  let fixture: ComponentFixture<UserprofilComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UserprofilComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UserprofilComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
