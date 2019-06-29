import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FreesearchComponent } from './freesearch.component';

describe('FreesearchComponent', () => {
  let component: FreesearchComponent;
  let fixture: ComponentFixture<FreesearchComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FreesearchComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FreesearchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
