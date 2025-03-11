package com.GRACE.StudentFileUpload;

import com.GRACE.StudentFileUpload.CONFIG.StudentProducer;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "http://localhost:4200") // Allow requests from Angular frontend
@RequiredArgsConstructor
public class StudentController {

    private final StudentProducer studentProducer;

    // Method to handle file upload and process the file
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            InputStream inputStream = file.getInputStream();
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0); // Assuming you are processing the first sheet

            List<Student> students = new ArrayList<>();
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header row
                Student student = new Student();
                student.setId(getCellValueAsLong(row.getCell(0))); // Assuming student ID is in the first column
                student.setFirstName(getCellValue(row.getCell(1))); // Assuming first name is in the second column
                student.setLastName(getCellValue(row.getCell(2))); // Assuming last name is in the third column
                student.setDateOfBirth(parseDate(getCellValue(row.getCell(3)))); // Assuming date of birth is in the fourth column
                students.add(student);
            }

            // Send student records to the RabbitMQ queue (using StudentProducer)
            students.forEach(studentProducer::sendToQueue);

            return ResponseEntity.ok().body("File processed successfully. " + students.size() + " students sent to queue.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading file: " + e.getMessage());
        }
    }

    // Utility methods for cell value extraction and date parsing
    private String getCellValue(Cell cell) {
        if (cell == null) {
            return ""; // Return empty string if cell is null
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toString(); // Convert date to string
                }
                return String.valueOf((long) cell.getNumericCellValue()); // Convert numbers to String (remove decimals)
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";
            default:
                return "UNKNOWN";
        }
    }

    // Method to parse the date string into a LocalDate object
    private LocalDate parseDate(String dateStr) {
        if (dateStr.isEmpty()) {
            return null; // If the string is empty, return null
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // Adjust to the format used in your Excel file
            return LocalDate.parse(dateStr, formatter);
        } catch (Exception e) {
            System.out.println("Error parsing date: " + dateStr); // Log the error if date parsing fails
            return null;
        }
    }

    // Method to convert cell value to Long (for StudentID)
    private Long getCellValueAsLong(Cell cell) {
        if (cell == null) {
            return null; // Return null if the cell is empty
        }

        switch (cell.getCellType()) {
            case NUMERIC:
                return (long) cell.getNumericCellValue();
            case STRING:
                try {
                    return Long.parseLong(cell.getStringCellValue());
                } catch (NumberFormatException e) {
                    return null; // If the value is not a valid Long, return null
                }
            default:
                return null; // Return null for other cases
        }
    }
}
