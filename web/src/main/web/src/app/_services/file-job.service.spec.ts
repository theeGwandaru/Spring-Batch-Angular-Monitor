import { TestBed, inject } from '@angular/core/testing';

import { FileJobService } from './file-job.service';

describe('FileJobService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [FileJobService]
    });
  });

  it('should be created', inject([FileJobService], (service: FileJobService) => {
    expect(service).toBeTruthy();
  }));
});
