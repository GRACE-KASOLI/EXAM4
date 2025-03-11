package com.GRACE.ConsumerService.Batch;

import com.GRACE.ConsumerService.Student;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RabbitMqItemReader implements ItemReader<Student> {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private int currentIndex = 0;
    private List<Student> currentBatch;

    @Override
    public Student read() throws Exception {
        // If no records are available or the current batch is finished, fetch the next batch
        if (currentBatch == null || currentIndex >= currentBatch.size()) {
            // Assuming a batch of students is retrieved from the queue
            currentBatch = fetchNextBatchFromQueue();
            currentIndex = 0;
        }

        if (currentBatch != null && currentIndex < currentBatch.size()) {
            return currentBatch.get(currentIndex++);
        } else {
            return null;  // No more students to read
        }
    }

    private List<Student> fetchNextBatchFromQueue() {
        // Fetch a batch of student records from the RabbitMQ queue
        // Receiving the message from the RabbitMQ queue
        org.springframework.amqp.core.Message amqpMessage = rabbitTemplate.receive("studentQueue");

        if (amqpMessage != null) {
            // Convert AMQP Message to Spring Messaging Message
            Message<?> message = new GenericMessage<>(amqpMessage.getBody());

            // Assuming the payload is a List<Student> that needs deserialization
            if (message.getPayload() instanceof List) {
                return (List<Student>) message.getPayload();  // Deserialize the payload
            }
        }

        return null;  // Return null if no message was retrieved
    }
}
