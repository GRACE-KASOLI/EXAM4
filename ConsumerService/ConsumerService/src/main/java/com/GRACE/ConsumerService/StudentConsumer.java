package com.GRACE.ConsumerService;

import com.GRACE.ConsumerService.Student;
import com.GRACE.ConsumerService.StudentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentConsumer {

    private final StudentRepository studentRepository;
    private final ObjectMapper objectMapper;

    private final List<Student> batchList = new ArrayList<>();
    private static final int BATCH_SIZE = 10;  // Process every 10 records

    @RabbitListener(queues = "studentQueue")
    @Transactional
    public void receiveMessage(String message) {
        try {
            Student student = objectMapper.readValue(message, Student.class);
            log.info("Received student record: {}", student);

            batchList.add(student);

            if (batchList.size() >= BATCH_SIZE) {
                processBatch();
            }

        } catch (Exception e) {
            log.error("Error processing message: {}", e.getMessage());
        }
    }

    private void processBatch() {
        log.info("Processing batch of {} students", batchList.size());
        studentRepository.saveAll(batchList);
        batchList.clear();
    }
}

