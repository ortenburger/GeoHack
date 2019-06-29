import { TestBed } from '@angular/core/testing';

import { OverpassService } from './overpass.service';

describe('OverpassService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: OverpassService = TestBed.get(OverpassService);
    expect(service).toBeTruthy();
  });
});
