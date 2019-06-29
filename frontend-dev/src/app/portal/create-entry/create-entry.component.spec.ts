import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateEntryComponent } from './create-entry.component';

describe('CreateEntryComponent', () => {
  let component: CreateEntryComponent;
  let fixture: ComponentFixture<CreateEntryComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CreateEntryComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CreateEntryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
