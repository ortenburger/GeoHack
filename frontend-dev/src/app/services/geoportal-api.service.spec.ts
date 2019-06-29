import { TestBed, inject } from '@angular/core/testing';

import { GeoportalApiService } from './geoportal-api.service';

describe('GeoportalApiService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [GeoportalApiService]
    });
  });

  it('should be created', inject([GeoportalApiService], (service: GeoportalApiService) => {
    expect(service).toBeTruthy();
  }));
});
