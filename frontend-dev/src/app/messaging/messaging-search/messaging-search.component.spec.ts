import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MessagingSearchComponent } from './messaging-search.component';

describe('MessagingSearchComponent', () => {
  let component: MessagingSearchComponent;
  let fixture: ComponentFixture<MessagingSearchComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MessagingSearchComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MessagingSearchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
