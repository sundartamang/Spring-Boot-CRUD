package com.example.student.service;

import com.example.student.model.Student;
import com.example.student.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    @Autowired
    StudentService(StudentRepository studentRepository){
        this.studentRepository = studentRepository;
    }

    /**
    * Retrieve the list of students
    * @return List of students
     */
    public List<Student> getStudentList(){
        return studentRepository.findAll();
    }

    /**
     * get student details by id
     * @param studentId Student Id
     * @return student details
     * throw IllegalStateException if student not found
     */
    public Student getStudentDetailsById(Long studentId) {
        return studentRepository.findById(studentId).orElseThrow(() -> new IllegalStateException(getStudentNotFoundMessage(studentId)));
    }

    /**
     * add new student and set photo
     * @param student student object
     * @param photo student photo
     */
    public void addStudent(Student student, MultipartFile photo){
        verifyEmailNotTaken(student.getEmail());
        setStudentPhotoAndHandleException(student, photo);
        studentRepository.save(student);
    }

    /**
     * update student details
     * @param studentId student id
     * @param name student name
     * @param email student email
     * @param dob student dob
     * @param photo student photo
     */
    public Student updateStudent(Long studentId, String name, String email, LocalDate dob, MultipartFile photo){
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new IllegalStateException(getStudentNotFoundMessage(studentId)));

        if(name != null && !name.isEmpty() && !Objects.equals(student.getName(), name)){
            student.setName(name);
        }

        if(email != null && !email.isEmpty() && !Objects.equals(student.getEmail(), email)){
            verifyEmailNotTaken(email);
            student.setEmail(email);
        }

        if(dob != null){
            student.setDob(dob);
        }

        setStudentPhotoAndHandleException(student, photo);
        return studentRepository.save(student);
    }

    /**
     * delete student base on id
     * @param studentId student id
     */
    public void deleteStudent(Long studentId){
        boolean exists = studentRepository.existsById(studentId);
        if (!exists) {
            throw new IllegalStateException(getStudentNotFoundMessage(studentId));
        }
        studentRepository.deleteById(studentId);
    }

    /**
     * searches for students by name or email with pagination and sorting.
     * @param name the name to filter by (optional)
     * @param email the email to filter by (optional)
     * @param page the page number (0-indexed)
     * @param size the number of records per page
     * @param sortField the field to sort by
     * @param sortDirection the direction of sorting (asc/desc)
     * @return a paginated and sorted list of students
     */
    public Page<Student> getStudentsBasedOnFilters(String name, String email, int page, int size, String sortField, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
        Pageable pageable = PageRequest.of(page, size, sort);

        if (name != null && email != null) {
            return studentRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(name, email, pageable);
        }
        return studentRepository.findAll(pageable);
    }

    /**
     * set student object if not null
     * @param student student object
     * @param photo student photo
     */
    private void setStudentPhotoAndHandleException(Student student, MultipartFile photo) {
        if(photo != null && !photo.isEmpty()){
            try{
                byte[] photoBytes = photo.getBytes();
                String encodedPhoto = Base64.getEncoder().encodeToString(photoBytes);
                student.setPhoto(encodedPhoto);
            }catch (IOException e){
                throw new IllegalStateException("Could not upload photo: " + e.getMessage());
            }
        }
    }

    /**
     * throw exception if email is already taken
     * @param email student email
     */
    private void verifyEmailNotTaken(String email) {
        if (studentRepository.existsByEmail(email)) {
            throw new IllegalStateException("Email already taken");
        }
    }

    /**
     * get student not found message
     * @param studentId student id
     * @return Error message
     */
    private String getStudentNotFoundMessage(Long studentId) {
        return String.format("Student with ID %d not found",studentId);
    }

}
