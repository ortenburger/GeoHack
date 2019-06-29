import { TestBed } from '@angular/core/testing';

import { GeoElementService } from './geo-element.service';

describe('GeoElementService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: GeoElementService = TestBed.get(GeoElementService);
    expect(service).toBeTruthy();
  });
});
