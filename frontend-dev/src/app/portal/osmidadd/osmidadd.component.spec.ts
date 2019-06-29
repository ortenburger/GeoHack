import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { OsmidaddComponent } from './osmidadd.component';

describe('OsmidaddComponent', () => {
  let component: OsmidaddComponent;
  let fixture: ComponentFixture<OsmidaddComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ OsmidaddComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(OsmidaddComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
