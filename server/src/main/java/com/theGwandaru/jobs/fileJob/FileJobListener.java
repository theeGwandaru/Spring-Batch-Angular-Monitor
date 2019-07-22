package com.theGwandaru.jobs.fileJob;

import com.theGwandaru.domain.JobProgressMessage;
import com.theGwandaru.domain.Person;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class FileJobListener implements ItemWriteListener<Person>{

    private SimpMessagingTemplate simpMessagingTemplate;
    private String fileName;
    private AtomicInteger runningWriteCount = new AtomicInteger(0);

    @Override
    public void beforeWrite(List<? extends Person> items) {

    }

    @Override
    public void afterWrite(List<? extends Person> items) {
        int runningWriteCount = this.runningWriteCount.addAndGet(items.size());
        JobProgressMessage jobProgressMessage = new JobProgressMessage();
        jobProgressMessage.setStatus("RUNNING");
        jobProgressMessage.setWriteCount(runningWriteCount);
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
}
