package com.theGwandaru.controller;

import com.theGwandaru.Service.FileJobsService;
import com.theGwandaru.domain.FileJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/file-jobs")
public class FileJobsController {

    @Autowired
    private FileJobsService fileJobsService;

    @GetMapping
    public ResponseEntity<List> getAvailableFileJobs(){

        List<FileJob> fileJobs = fileJobsService.getAllFileJobs();
        return ResponseEntity.ok(fileJobs);
    }

    @PostMapping("/start")
    public ResponseEntity startFileJob(@RequestBody FileJob fileJob){

        this.fileJobsService.startFileJob(fileJob);
        return ResponseEntity.ok("file job started");
    }
}
