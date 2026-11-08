package com.studentmanagement.repository;

import com.studentmanagement.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

//DIP
public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> id(Long id);
}
