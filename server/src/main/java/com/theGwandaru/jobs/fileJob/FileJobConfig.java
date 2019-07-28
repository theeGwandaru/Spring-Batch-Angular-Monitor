package com.theGwandaru.jobs.fileJob;

import com.theGwandaru.domain.JobProgressMessage;
import com.theGwandaru.domain.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.validation.BindException;

import java.util.List;

@Configuration
@EnableBatchProcessing
public class FileJobConfig {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Bean
    public Job FileJob() {
        return this.jobBuilderFactory.get("FileJob")
                .listener(jobExecutionListener())
                .flow(getFileJobStep())
                .build()
                .build();

    }

    private Step getFileJobStep() {
        return stepBuilderFactory.get("fileJobStep")
                .<Person, Person>chunk(1000)
                .reader(getFlatFileItemReader(null))
                .processor(getItemProcessor())
                .writer(getItemWriter())
                .listener(getItemWriteListener(null))
                .build();
    }

    @Bean(destroyMethod = "")
    @StepScope
    public FlatFileItemReader<Person> getFlatFileItemReader(@Value(("#{jobParameters['absoluteFileName']}")) String absoluteFileName) {
        FlatFileItemReader<Person> reader = new FlatFileItemReader<Person>();
        reader.setResource(new FileSystemResource(absoluteFileName));

        DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();
        delimitedLineTokenizer.setNames("ssn", "firstName", "lastName", "gender", "skill");

        DefaultLineMapper<Person> defaultLineMapper = new DefaultLineMapper<Person>();
        defaultLineMapper.setLineTokenizer(delimitedLineTokenizer);
        defaultLineMapper.setFieldSetMapper(new FieldSetMapper<Person>() {
            @Override
            public Person mapFieldSet(FieldSet fieldSet) throws BindException {
                Person person = new Person();
                person.setSsn(fieldSet.readRawString("ssn"));
                person.setFirstName(fieldSet.readRawString("firstName"));
                person.setLastName(fieldSet.readRawString("lastName"));
                person.setGender(fieldSet.readRawString("gender"));
                person.setSkill(fieldSet.readRawString("skill"));
                return person;
            }
        });
        reader.setLineMapper(defaultLineMapper);
        return reader;
    }

    @Bean
    @StepScope
    public ItemProcessor<Person, Person> getItemProcessor(){
        return new ItemProcessor<Person, Person>() {
            @Override
            public Person process(Person person) throws Exception {
                return person;
            }
        };
    }

    @Bean
    @StepScope
    public ItemWriter<Person> getItemWriter(){
        return new ItemWriter<Person>() {
            @Override
            public void write(List<? extends Person> persons) throws Exception {
                for(Person person : persons){
                    logger.info("writing " + person.toString());
                }
            }
        };
    }

    @Bean
    @JobScope
    public ItemWriteListener<Person> getItemWriteListener(@Value(("#{jobParameters['absoluteFileName']}")) String absoluteFileName){
       return fileJobListener(null);
    }

    @Bean
    @JobScope
    public JobExecutionListener jobExecutionListener(){
        return fileJobListener(null);
    }

    @Bean
    @JobScope
    public FileJobListener fileJobListener(@Value(("#{jobParameters['absoluteFileName']}")) String absoluteFileName){
        FileJobListener fileJobListener =  new FileJobListener();
        fileJobListener.setFileName(absoluteFileName);
        fileJobListener.setSimpMessagingTemplate(simpMessagingTemplate);
        return fileJobListener;
    }
}
