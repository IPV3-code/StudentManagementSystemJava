package com.studentmanagement.service;

import com.studentmanagement.dto.StudentPatchRequest;
import com.studentmanagement.dto.StudentRequest;
import com.studentmanagement.dto.StudentResponse;
import com.studentmanagement.entity.Student;
import com.studentmanagement.repository.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class StudentService {
    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository){
        this.studentRepository = studentRepository;
    }

    public List<StudentResponse> getAllStudent(){
        return studentRepository.findAll().stream().map(this::convertToResponse).toList();
    }

    //REST - Uniform pri
    public StudentResponse getStudentById(Long id){
        Student student = studentRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student Not Found with ID : " + id));
        return convertToResponse(student);
    }

    public StudentResponse createStudent(StudentRequest request){
        Student student = convertToEntity(request);
        Student savedStudent = studentRepository.save(student);

        return convertToResponse(savedStudent);
    }

    //SRP, DIP

    public StudentResponse updateStudent(Long id, StudentRequest request){
        Student existingStudent = studentRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student Not Found with ID : " + id));
        existingStudent.setName(request.getName());
        existingStudent.setEmail(request.getEmail());
        existingStudent.setAge(request.getAge());

        Student updatedStudent = studentRepository.save(existingStudent);

        return convertToResponse(updatedStudent);
    }

    public StudentResponse patchStudent(Long id, StudentPatchRequest request){
        Student existingStudent = studentRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student Not Found with ID : " + id));

        if(request.getName() != null){
            existingStudent.setName(request.getName());
        }

        if(request.getEmail() != null){
            existingStudent.setEmail(request.getEmail());
        }

        if(request.getAge() != null){
            existingStudent.setAge(request.getAge());
        }
        Student updatedStudent = studentRepository.save(existingStudent);
        return convertToResponse(updatedStudent);

    }

    public void deleteStudent(Long id){
        Student existingStudent = studentRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student Not Found with ID : " + id));
        studentRepository.delete(existingStudent);
    }

    private Student convertToEntity(StudentRequest request){
        Student student = new Student();

        student.setName(request.getName());
        student.setEmail(request.getEmail());
        student.setAge(request.getAge());

        return student;
    }

    private StudentResponse convertToResponse(Student student){
        return new StudentResponse(student.getId(), student.getName(), student.getEmail(), student.getAge());
    }
}
