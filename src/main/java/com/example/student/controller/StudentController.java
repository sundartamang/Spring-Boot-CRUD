package com.example.student.controller;

import com.example.student.service.StudentService;

public class StudentController {
    private final StudentService studentService;

    StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

}
