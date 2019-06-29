import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { GoodplaceComponent } from './goodplace.component';

describe('GoodplaceComponent', () => {
  let component: GoodplaceComponent;
  let fixture: ComponentFixture<GoodplaceComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ GoodplaceComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GoodplaceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
