package com.example.student.service;

import com.example.student.model.Student;
import com.example.student.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

@Service
public class StudentService implements IStudentService {
    private final String uploadDir = "src/main/resources/static/upload/";
    private final StudentRepository studentRepository;

    StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Override
    public Student getStudentById(Long studentId) {
        return studentRepository.findById(studentId).orElseThrow(() -> new IllegalStateException(getStudentNotFoundMessage(studentId)));
    }

    @Override
    public void deleteStudentById(Long studentId) {
        boolean exists = studentRepository.existsById(studentId);
        if (exists) {
            studentRepository.deleteById(studentId);
        }else{
            throw new IllegalStateException(getStudentNotFoundMessage(studentId));
        }
    }

    @Override
    public Student saveStudent(Student student, MultipartFile image) throws IOException {
        String email = student.getEmail();
        verifyEmailNotTaken(email);
        saveImageAndFilePath(student, image);
        return studentRepository.save(student);
    }

    @Override
    public Student updateStudentById(Long studentId, Student student, MultipartFile image) throws IOException {
        Student existingStudent = studentRepository.findById(studentId).orElseThrow(() -> new IllegalStateException(getStudentNotFoundMessage(studentId)));
        String email = student.getEmail();
        String name = student.getName();
        LocalDate dob = student.getDob();

        verifyEmailNotTaken(email);

        if (name != null && !name.equals(existingStudent.getName())) {
            existingStudent.setName(name);
        }

        if (dob != null && !dob.equals(existingStudent.getDob())) {
            existingStudent.setDob(dob);
        }

        if(image != null && !image.isEmpty()) {
            deleteImageFromFileSystem(existingStudent.getImagePath());
            saveImageAndFilePath(existingStudent, image);
        }
        return studentRepository.save(existingStudent);
    }


    private void saveImageAndFilePath(Student student, MultipartFile image) throws IOException {
        if (!image.isEmpty()) {
            String fileName = saveImageInFileSystem(image);
            student.setImagePath(fileName);
        }
    }

    private String saveImageInFileSystem(MultipartFile image) throws IOException {
        String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);
        Files.createDirectories(filePath.getParent());
        Files.copy(image.getInputStream(), filePath);
        return fileName;
    }

    private void deleteImageFromFileSystem(String imagePath) throws IOException {
        if (imagePath != null && !imagePath.isEmpty()) {
            Path path = Paths.get(uploadDir, imagePath);
            Files.deleteIfExists(path);
        }
    }

    private void verifyEmailNotTaken(String email) {
        if (studentRepository.existsByEmail(email)) {
            throw new IllegalStateException("Email already taken");
        }
    }

    private String getStudentNotFoundMessage(Long studentId) {
        return String.format("Student with ID %d not found",studentId);
    }
}
