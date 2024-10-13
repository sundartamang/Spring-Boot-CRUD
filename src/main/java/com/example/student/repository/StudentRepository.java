package com.example.student.repository;

import com.example.student.model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
    boolean existsByEmail(String email);

    Page<Student> findByNameContainingIgnoreCase(String name, Pageable pageable);;

    Page<Student> findByEmailContainingIgnoreCase(String email, Pageable pageable);

    Page<Student> findByNameContainingIgnoreCaseAndEmailContainingIgnoreCase(String name, String email, Pageable pageable);
}
