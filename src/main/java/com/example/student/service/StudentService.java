package com.example.student.service;

import com.example.student.model.Student;
import com.example.student.repository.StudentRepository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public class StudentService implements IStudentService {
    private final StudentRepository studentRepository;

    StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public List<Student> getAllStudents() {
        return List.of();
    }

    @Override
    public Student getStudentById(Integer id) {
        return null;
    }

    @Override
    public void deleteStudentById(Integer id) {

    }

    @Override
    public Student SaveStudent(Student student, MultipartFile file) throws IOException {
        return null;
    }

    @Override
    public Student updateStudentById(Long studentId, Student student, MultipartFile file) throws IOException {
        return null;
    }
}
