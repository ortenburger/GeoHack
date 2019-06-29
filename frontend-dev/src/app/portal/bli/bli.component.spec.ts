import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BliComponent } from './bli.component';

describe('BliComponent', () => {
  let component: BliComponent;
  let fixture: ComponentFixture<BliComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ BliComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BliComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
