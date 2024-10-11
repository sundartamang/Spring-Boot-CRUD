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

    public Student(Long id, String name, String email, LocalDate dob, byte[] photo) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.dob = dob;
        this.photo = photo != null ? this.convertImageToBase64(photo) : null;
    }

    public void setId(Long id){
        this.id = id;
    }

    public Long getId(){
        return id;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public String getEmail(){
        return  email;
    }

    public void setDob(LocalDate dob){
        this.dob = dob;
    }

    public LocalDate getDob(){
        return dob;
    }

    public byte[] getPhoto() {
        return Base64.getDecoder().decode(photo);
    }

    public void setPhoto(byte[] photo) {
        this.photo = this.convertImageToBase64(photo);
    }

    public Integer getAge(){
        return Period.between(this.dob, LocalDate.now()).getYears();
    }

    private String convertImageToBase64(byte[] photo){
        return Base64.getEncoder().encodeToString(photo);
    }
}
