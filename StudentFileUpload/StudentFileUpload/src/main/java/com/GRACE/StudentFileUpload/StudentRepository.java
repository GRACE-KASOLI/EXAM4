package com.GRACE.StudentFileUpload;

import com.GRACE.StudentFileUpload.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    // Additional query methods can be added if needed
}
