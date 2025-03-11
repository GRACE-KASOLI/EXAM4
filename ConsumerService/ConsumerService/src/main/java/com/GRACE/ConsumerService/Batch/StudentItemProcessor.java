package com.GRACE.ConsumerService.Batch;

import com.GRACE.ConsumerService.Student;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class StudentItemProcessor implements ItemProcessor<Student, Student> {

    @Override
    public Student process(Student student) throws Exception {
        // Example validation or transformation
        if (student.getFirstName() == null || student.getFirstName().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }

        // You can apply any other transformations here
        return student;
    }
}

