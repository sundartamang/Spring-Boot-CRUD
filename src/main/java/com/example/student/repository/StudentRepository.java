package com.example.student.repository;

import com.example.student.model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long>, PagingAndSortingRepository<Student, Long> {

    /**
     * checks if a student exists with the given email.
     * @param email the email to check
     * @return true if exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * finds students by name or email, ignoring case.
     * @param name the name substring (optional)
     * @param email the email substring (optional)
     * @param pageable pagination info
     * @return paginated list of matching students
     */
    Page<Student> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String name, String email, Pageable pageable);

    /**
     * retrieves all students with pagination.
     * @param pageable pagination info
     * @return paginated list of all students
     */
    Page<Student> findAll(Pageable pageable);
}
