import { TestBed } from '@angular/core/testing';

import { BliService } from './bli.service';

describe('BliService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: BliService = TestBed.get(BliService);
    expect(service).toBeTruthy();
  });
});
