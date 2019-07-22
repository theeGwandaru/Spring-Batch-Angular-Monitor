package com.theGwandaru.Service;

import com.theGwandaru.domain.FileJob;

import java.util.List;

public interface FileJobsService {
    List<FileJob> getAllFileJobs();

    void startFileJob(FileJob fileJob);
}
