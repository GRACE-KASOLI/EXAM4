package com.GRACE.StudentFileUpload.CONFIG;


import com.GRACE.StudentFileUpload.Student;
import com.GRACE.StudentFileUpload.StudentDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StudentProducer {

@Autowired
    private final RabbitTemplate rabbitTemplate;

    @Value("${student.queue.name}")
    private String queueName;

    public StudentProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendStudentToQueue(StudentDTO student) {
        rabbitTemplate.convertAndSend(queueName, student);
    }
    public void sendToQueue(Student student) {
        rabbitTemplate.convertAndSend(queueName, student);
    }
}

