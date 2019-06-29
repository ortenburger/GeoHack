import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TempDevComponent } from './temp-dev.component';

describe('TempDevComponent', () => {
  let component: TempDevComponent;
  let fixture: ComponentFixture<TempDevComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TempDevComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TempDevComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
