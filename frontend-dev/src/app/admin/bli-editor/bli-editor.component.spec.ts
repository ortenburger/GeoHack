import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BliEditorComponent } from './bli-editor.component';

describe('BliEditorComponent', () => {
  let component: BliEditorComponent;
  let fixture: ComponentFixture<BliEditorComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ BliEditorComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BliEditorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
