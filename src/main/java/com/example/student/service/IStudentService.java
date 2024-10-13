package com.example.student.service;

import com.example.student.dto.FilterStudentRequest;
import com.example.student.model.Student;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IStudentService {
    List<Student> getAllStudents(FilterStudentRequest filterStudentRequest);
    List<Student> searchStudents(FilterStudentRequest filterStudentRequest);
    Student getStudentById(Long studentId);
    void deleteStudentById(Long StudentId);
    Student saveStudent(Student student, MultipartFile file) throws IOException;
    Student updateStudentById(Long studentId, Student student, MultipartFile file) throws IOException;
}
