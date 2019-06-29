import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BliCheckComponent } from './bli-check.component';

describe('BliCheckComponent', () => {
  let component: BliCheckComponent;
  let fixture: ComponentFixture<BliCheckComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ BliCheckComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BliCheckComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
