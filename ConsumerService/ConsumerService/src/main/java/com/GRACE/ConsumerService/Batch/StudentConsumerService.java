package com.GRACE.ConsumerService.Batch;

import com.GRACE.ConsumerService.Student;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class StudentConsumerService {

    @Autowired
    private BatchProcessingService batchProcessingService;

    @RabbitListener(queues = "studentQueue")
    public void processBatch(List<Student> studentBatch) {
        try {
            // Trigger the batch job to process the students
            batchProcessingService.startBatchJob();
        } catch (Exception e) {
            // Handle error
            System.err.println("Error processing batch: " + e.getMessage());
        }
    }
}

