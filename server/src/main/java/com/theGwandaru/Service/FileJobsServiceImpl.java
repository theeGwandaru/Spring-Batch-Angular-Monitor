package com.theGwandaru.Service;

import com.theGwandaru.domain.FileJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class FileJobsServiceImpl implements FileJobsService {

    @Value("${fileJobsInputPath}")
    private String fileJobsInputPathString;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job springFileJob;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<FileJob> getAllFileJobs() {
        List<FileJob> fileJobs = new ArrayList<>();
        Path fileJobsInputPath = Paths.get(fileJobsInputPathString);
        for(File file:  fileJobsInputPath.toFile().listFiles()){
            if(file.isFile()){
                FileJob fileJob = new FileJob();
                fileJob.setFileName(file.getName());
                fileJob.setAbsolutePath(file.getAbsolutePath());
                fileJobs.add(fileJob);
            }
        }
        return fileJobs;
    }

    @Override
    public void startFileJob(FileJob fileJob) {
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder()
                .addString("absoluteFileName",fileJob.getAbsolutePath())
                .addDate("time", new Date());

        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try{

                    JobExecution jobExecution = jobLauncher.run(springFileJob, jobParametersBuilder.toJobParameters());
                    logger.info("Api Processed Files Loaded Successfully");

                }
                catch (Exception e){
                    logger.error(e.getMessage(), (Object[]) e.getStackTrace());

                }
            }
        });
    }
}
