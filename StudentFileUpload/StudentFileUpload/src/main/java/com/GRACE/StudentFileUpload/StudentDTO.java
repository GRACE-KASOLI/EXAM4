package com.GRACE.StudentFileUpload;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class StudentDTO implements Serializable {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        @JsonProperty("Student ID")
        private Long StudentID;

        @JsonProperty("First Name")
        private String FirstName;

        @JsonProperty("Last Name")
        private String LastName;

        @JsonProperty("Date of Birth")
        private LocalDate DateOfBirth;

        @JsonProperty("Email Address")
        private String EmailAddress;

        @JsonProperty("Phone Number")
        private String PhoneNumber;

        @JsonProperty("Course Enrolled")
        private String CourseEnrolled;

        @JsonProperty("Registration Date")
        private LocalDate RegistrationDate;
    }

