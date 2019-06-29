import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { KontaktformularComponent } from './kontaktformular.component';

describe('KontaktformularComponent', () => {
  let component: KontaktformularComponent;
  let fixture: ComponentFixture<KontaktformularComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ KontaktformularComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(KontaktformularComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
