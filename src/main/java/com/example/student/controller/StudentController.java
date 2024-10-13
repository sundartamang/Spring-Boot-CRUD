package com.example.student.controller;

import com.example.student.model.Student;
import com.example.student.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(path = "api/v1/students")
public class StudentController {
    @Autowired
    private final StudentService studentService;

    StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    @GetMapping("/{studentId}")
    public ResponseEntity<Student> getStudentDetails(@PathVariable("studentId") Long studentId) {
        Student student = studentService.getStudentById(studentId);
        return new ResponseEntity<Student>(student,HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<String> addNewStudent(
            @ModelAttribute("student") Student student,
            @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {
        studentService.saveStudent(student, image);
        return ResponseEntity.status(HttpStatus.CREATED).body("Student added successfully");
    }

    @DeleteMapping("/{studentId}")
    public ResponseEntity<String> deleteStudent(@PathVariable("studentId") Long studentId) {
        studentService.deleteStudentById(studentId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Student deleted successfully");
    }

    @PutMapping("/{studentId}")
    public ResponseEntity<Student> updateStudent(
            @PathVariable("studentId") Long studentId,
            @ModelAttribute("student") Student student,
            @RequestPart(value = "image", required = false) MultipartFile image) throws  IOException {
        studentService.updateStudentById(studentId, student, image);
        return ResponseEntity.status(HttpStatus.OK).body(student);
    }


}
