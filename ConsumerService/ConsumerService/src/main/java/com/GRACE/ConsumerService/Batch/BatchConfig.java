package com.GRACE.ConsumerService.Batch;

import com.GRACE.ConsumerService.Student;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Bean
    public ItemReader<Student> studentReader() {
        // Custom reader that consumes student records from RabbitMQ
        return new RabbitMqItemReader();  // Custom implementation
    }

    @Bean
    public ItemProcessor<Student, Student> studentProcessor() {
        return new StudentItemProcessor();
    }

    @Bean
    public ItemWriter<Student> studentWriter(JdbcTemplate jdbcTemplate) {
        JdbcBatchItemWriter<Student> writer = new JdbcBatchItemWriter<>();
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        writer.setSql("INSERT INTO students (id, first_name, last_name) VALUES (:id, :firstName, :lastName)");
        writer.setDataSource(jdbcTemplate.getDataSource());
        return writer;
    }

    @Bean
    public Step step1(StepBuilderFactory stepBuilderFactory) {  // Added StepBuilderFactory as parameter
        return stepBuilderFactory.get("step1")
                .<Student, Student>chunk(100)  // Process in chunks of 100 students
                .reader(studentReader())
                .processor(studentProcessor())
                .writer(studentWriter(null))
                .build();
    }

    @Bean
    public Job studentJob(JobBuilderFactory jobBuilderFactory, Step step1) {  // Added Step as parameter
        return jobBuilderFactory.get("studentJob")
                .start(step1)
                .build();
    }
}
