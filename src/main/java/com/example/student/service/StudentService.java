package com.example.student.service;

import com.example.student.dto.FilterStudentRequest;
import com.example.student.model.Student;
import com.example.student.repository.StudentRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public List<Student> getAllStudents(FilterStudentRequest filterStudentRequest) {
        Pageable pageable = getPageable(filterStudentRequest);
        return findAllStudents(pageable);
    }

    @Override
    public Student getStudentById(Long studentId) {
        return studentRepository.findById(studentId).orElseThrow(
                () -> new IllegalStateException(getStudentNotFoundMessage(studentId)));
    }

    @Transactional
    @Override
    public void deleteStudentById(Long studentId) {
        boolean exists = studentRepository.existsById(studentId);
        if (exists) {
            studentRepository.deleteById(studentId);
        }else{
            throw new IllegalStateException(getStudentNotFoundMessage(studentId));
        }
    }

    @Transactional
    @Override
    public Student saveStudent(Student student, MultipartFile image) throws IOException {
        validateInputField(student);
        verifyEmailNotTaken(student);

        if (image != null && !image.isEmpty()) {
            saveImageAndFilePath(student, image);
        }
        return studentRepository.save(student);
    }

    @Transactional
    @Override
    public Student updateStudentById(Long studentId, Student student, MultipartFile image) throws IOException {
        Student existingStudent = studentRepository.findById(studentId).orElseThrow(
                () -> new IllegalStateException(getStudentNotFoundMessage(studentId)));
        String name = student.getName();
        LocalDate dob = student.getDob();
        String email = student.getEmail();

        validateInputField(student);
        verifyEmailNotTaken(student);

        if (name != null && !name.equals(existingStudent.getName())) {
            existingStudent.setName(name);
        }

        if (email != null && !email.equals(existingStudent.getEmail())) {
            existingStudent.setEmail(email);
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

    @Override
    public List<Student> searchStudents(FilterStudentRequest filterStudentRequest) {
        Pageable pageable = getPageable(filterStudentRequest);
        String name = filterStudentRequest.getName();
        String email = filterStudentRequest.getEmail();

        if (name != null && !name.isEmpty() && email != null && !email.isEmpty()) {
            return searchByNameAndEmail(name, email, pageable);
        } else if (name != null && !name.isEmpty()) {
            return searchByName(name, pageable);
        } else if (email != null && !email.isEmpty()) {
            return searchByEmail(email, pageable);
        } else {
            return findAllStudents(pageable);
        }
    }


    // --- Helper Methods ---

    private Pageable getPageable(FilterStudentRequest filterStudentRequest){
        if (filterStudentRequest == null) {
            return getDefaultPageable();
        }

        int page = (filterStudentRequest.getPage() < 0) ? 0 : filterStudentRequest.getPage();
        int size = (filterStudentRequest.getSize() <= 0) ? 10 : filterStudentRequest.getSize();

        String sortField = filterStudentRequest.getSortField();
        String sortOrder = filterStudentRequest.getSortOrder();

        Sort sort = isNotEmpty(sortField)  ? Sort.by(getSortDirection(sortOrder), sortField) : Sort.unsorted();

        return PageRequest.of(page, size, sort);
    }

    private void validateInputField(Student student) {
        if (student.getEmail() == null || student.getEmail().isEmpty()) {
            handleInvalidException("email");
        }
        if (student.getName() == null || student.getName().isEmpty()) {
            handleInvalidException("name");
        }
        if (student.getDob() == null) {
            handleInvalidException("Date of birth");
        }
    }

    private void handleInvalidException(String inputFieldName) {
        throw new ValidationException(String.format("The field '%s' is required", inputFieldName));
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

    private void verifyEmailNotTaken(Student student) {
        String email = student.getEmail();
        if (studentRepository.existsByEmail(email)) {
            throw new ValidationException("Email already taken");
        }
    }

    private String getStudentNotFoundMessage(Long studentId) {
        return String.format("Student with ID %d not found",studentId);
    }

    private List<Student> searchByNameAndEmail(String name, String email, Pageable pageable) {
        return studentRepository.findByNameContainingIgnoreCaseAndEmailContainingIgnoreCase(
                name, email, pageable).getContent();
    }

    private List<Student> searchByName(String name, Pageable pageable) {
        return studentRepository.findByNameContainingIgnoreCase(name, pageable).getContent();
    }

    private List<Student> searchByEmail(String email, Pageable pageable) {
        return studentRepository.findByEmailContainingIgnoreCase(email, pageable).getContent();
    }

    private List<Student> findAllStudents(Pageable pageable) {
        return studentRepository.findAll(pageable).getContent();
    }

    private Sort.Direction getSortDirection(String sortOrder) {
        return (sortOrder != null && sortOrder.equalsIgnoreCase("asc"))
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
    }

    private Pageable getDefaultPageable() {
        return PageRequest.of(0, 10, Sort.unsorted());
    }

    private boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
