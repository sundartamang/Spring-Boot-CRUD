package com.example.student.controller;

import com.example.student.model.Student;
import com.example.student.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(path = "api/v1/students")
public class StudentController {

    private final StudentService studentService;

    @Autowired
    StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    /**
     * Retrieve students with pagination, sorting, and search capability.
     * @param name student name
     * @param email student email
     * @param page page number
     * @param size total size per page
     * @param sortField sort field name
     * @param sortDirection direction of sorting, either "asc" or "desc" (default is "asc")
     * @return list of students with status 200
     */
    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        var studentsPage = studentService.getStudentsBasedOnFilters(name, email, page, size, sortField, sortDirection);
        if (studentsPage.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(studentsPage.getContent());
    }

    /**
     * get student details base on id
     * @param studentId student id
     * @return student details with status 200
     */
    @GetMapping("/{studentId}")
    public ResponseEntity<Student> getStudentDetails(@PathVariable("studentId") Long studentId) {
        Student student = studentService.getStudentDetailsById(studentId);
        return new ResponseEntity<>(student, HttpStatus.OK);
    }

    /**
     * add a new student
     * @param dob student date of birth
     * @param email student email
     * @param name student name
     * @param photo student photo
     * @return a message with HTTP status 201
     * @throws IOException if any error occur
     */
    @PostMapping
    public ResponseEntity<String> addNewStudent(
            @RequestParam("dob") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dob,
            @RequestParam("email") String email,
            @RequestParam("name") String name,
            @RequestParam(value = "photo", required = false) MultipartFile photo
    ) throws IOException {
        studentService.addStudent(new Student(name, email, dob), photo);
        return ResponseEntity.status(HttpStatus.CREATED).body("Student added successfully");
    }

    /**
     *  update student info
     * @param dob student date of birth
     * @param email student email
     * @param name student name
     * @param photo student photo
     * @return updated student information with HTTP status 200
     */
    @PutMapping(path = "{studentId}")
    public ResponseEntity<Student> updateStudent(
            @PathVariable("studentId") Long studentId,
            @RequestParam("dob") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dob,
            @RequestParam("email") String email,
            @RequestParam("name") String name,
            @RequestPart(value = "photo", required = false) MultipartFile photo
    ){
       Student student = studentService.updateStudent(studentId, name, email, dob, photo);
       return new ResponseEntity<Student>(student, HttpStatus.OK);
    }

    /**
     * delete student base on id
     * @param studentId student id
     * @return void
     */
    @DeleteMapping(path = "{studentId}")
    public ResponseEntity<Void> deleteStudent(@PathVariable("studentId") Long studentId) {
        studentService.deleteStudent(studentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
