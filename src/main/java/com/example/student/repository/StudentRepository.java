package com.example.student.repository;

import com.example.student.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    /**
     * check if email already exists
     * @param email student email
     * @return true if student exists with given email, otherwise false
     */
    boolean existsByEmail(String email);
}
