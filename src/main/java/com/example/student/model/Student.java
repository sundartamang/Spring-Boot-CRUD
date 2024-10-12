package com.example.student.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.Period;
import java.util.Base64;

@Entity
@Table(name = "student")
public class Student {

    @Id
    @SequenceGenerator(name = "student_sequence", sequenceName = "student_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "student_sequence")
    private Long id;

    @NotNull
    @Size(min = 1, message = "Name is required")
    private String name;

    @NotNull
    @Email(message = "Must be a well-formed email address")
    private String email;

    @NotNull
    private LocalDate dob;

    @Lob
    private String photo;

    public Student() {
    }

    public Student(String name, String email, LocalDate dob, String photo) {
        this.name = name;
        this.email = email;
        this.dob = dob;
        this.photo = photo;
    }

    public Student(String name, String email, LocalDate dob) {
        this.name = name;
        this.email = email;
        this.dob = dob;
    }

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email){
        this.email = email;
    }
    public LocalDate getDob(){
        return dob;
    }

    public void setDob(LocalDate dob){
        this.dob = dob;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Integer getAge(){
        return Period.between(this.dob, LocalDate.now()).getYears();
    }

}
