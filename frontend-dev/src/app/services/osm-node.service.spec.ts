import { TestBed } from '@angular/core/testing';

import { OsmNodeService } from './osm-node.service';

describe('OsmNodeService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: OsmNodeService = TestBed.get(OsmNodeService);
    expect(service).toBeTruthy();
  });
});
