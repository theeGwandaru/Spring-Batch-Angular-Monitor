package com.theGwandaru.jobs.fileJob;

import com.theGwandaru.domain.JobProgressMessage;
import com.theGwandaru.domain.Person;
import org.springframework.batch.core.*;
import org.springframework.core.io.FileSystemResource;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class FileJobListener implements ItemWriteListener<Person>, JobExecutionListener{

    private SimpMessagingTemplate simpMessagingTemplate;
    private String fileName;
    private AtomicInteger runningWriteCount = new AtomicInteger(0);
    private JobExecution jobExecution;
    private int recordCount;

    @Override
    public void beforeWrite(List<? extends Person> items) {

    }

    @Override
    public void afterWrite(List<? extends Person> items) {
        double runningWriteCount = this.runningWriteCount.addAndGet(items.size());
        double percentageComplete = (runningWriteCount / recordCount) * 100;
        JobProgressMessage jobProgressMessage = new JobProgressMessage();
        jobProgressMessage.setStatus("RUNNING");
        jobProgressMessage.setWriteCount(runningWriteCount);
        jobProgressMessage.setPercentageComplete(percentageComplete);
        jobProgressMessage.setFileName(fileName);
        simpMessagingTemplate.convertAndSend("/topic/public", jobProgressMessage);
    }

    @Override
    public void onWriteError(Exception exception, List<? extends Person> items) {

    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public SimpMessagingTemplate getSimpMessagingTemplate() {
        return simpMessagingTemplate;
    }

    public void setSimpMessagingTemplate(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        this.jobExecution = jobExecution;
        String absoluteFilePath = jobExecution.getJobParameters().getString("absoluteFileName");
        try {
            recordCount = countLines(absoluteFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterJob(JobExecution jobExecution) {

    }

    //https://stackoverflow.com/questions/453018/number-of-lines-in-a-file-in-java
    public static int countLines(String filename) throws IOException {
        InputStream is = new BufferedInputStream(new FileInputStream(filename));
        try {
            byte[] c = new byte[1024];

            int readChars = is.read(c);
            if (readChars == -1) {
                // bail out if nothing to read
                return 0;
            }

            // make it easy for the optimizer to tune this loop
            int count = 0;
            while (readChars == 1024) {
                for (int i=0; i<1024;) {
                    if (c[i++] == '\n') {
                        ++count;
                    }
                }
                readChars = is.read(c);
            }

            // count remaining characters
            while (readChars != -1) {
                System.out.println(readChars);
                for (int i=0; i<readChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;
                    }
                }
                readChars = is.read(c);
            }

            return count == 0 ? 1 : count;
        } finally {
            is.close();
        }
    }
}
