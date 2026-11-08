package com.studentmanagement;

import com.studentmanagement.dto.StudentPatchRequest;
import com.studentmanagement.dto.StudentRequest;
import com.studentmanagement.dto.StudentResponse;
import com.studentmanagement.entity.Student;
import com.studentmanagement.notification.NotificationService;
import com.studentmanagement.service.StudentService;
import com.studentmanagement.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class StudentServiceTest {
    @Mock
    private StudentRepository studentRepository;

    @Mock
    private NotificationService notificationService;

    private StudentService studentService;

    @BeforeEach
    void setUp(){
        Map<String, NotificationService> notificationServices = Map.of("emailNotificationService", notificationService);
        studentService = new StudentService(studentRepository, notificationServices);
        ReflectionTestUtils.setField(studentService, "notificationType", "emailNotificationService"
        );
    }

    @Test
    void getStudentById_shouldReturnStudent_whenStudentExist(){
        //Arrange
        Student student = new Student();
        student.setId(1L);
        student.setName("Cristiano");
        student.setEmail("cristiano@example.com");
        student.setAge(25);

        Mockito.when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        //Act
        StudentResponse response = studentService.getStudentById(1L);

        //Asserts
        assertEquals(1L, response.getId());
        assertEquals("Cristiano", response.getName());
        assertEquals("cristiano@example.com", response.getEmail());
        assertEquals(25, response.getAge());
    }

    @Test
    void getStudentById_shouldThrowException_whenStudentDoesNotExist(){
        Mockito.when(studentRepository.findById(99L)).thenReturn(Optional.empty());
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> studentService.getStudentById(99L));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void createStudent_shouldSaveStudentAndReturnResponse(){
        StudentRequest request = new StudentRequest();
        request.setName("Cristiano");
        request.setEmail("cristiano@example.com");
        request.setAge(25);

        Student savedStudent = new Student();
        savedStudent.setId(1L);
        savedStudent.setName("Cristiano");
        savedStudent.setEmail("cristiano@example.com");
        savedStudent.setAge(25);

        when(studentRepository.save(any(Student.class))).thenReturn(savedStudent);

        StudentResponse response = studentService.createStudent(request);

        assertEquals(1L, response.getId());
        assertEquals("Cristiano", response.getName());
        assertEquals("cristiano@example.com", response.getEmail());
        assertEquals(25, response.getAge());

        verify(studentRepository).save(any(Student.class));
        verify(notificationService).sendNotification(anyString());
    }

    @Test
    void getAllStudents_shouldReturnAllStudents(){
        Student student1 = new Student();
        student1.setId(1L);
        student1.setName("Cristiano");
        student1.setEmail("cristiano@example.com");
        student1.setAge(25);

        Student student2 = new Student();
        student2.setId(2L);
        student2.setName("Messi");
        student2.setEmail("messi@example.com");
        student2.setAge(28);

        when(studentRepository.findAll()).thenReturn(List.of(student1, student2));

        List<StudentResponse> responses = studentService.getAllStudent();

        assertEquals(2, responses.size());
        assertEquals("Cristiano", responses.get(0).getName());
        assertEquals("Messi", responses.get(1).getName());

        verify(studentRepository).findAll();
    }

    @Test
    void updateStudent_shouldUpdateStudent_whenStudentExists() {

        Student existingStudent = new Student();
        existingStudent.setId(1L);
        existingStudent.setName("Cristiano");
        existingStudent.setEmail("old@example.com");
        existingStudent.setAge(25);

        StudentRequest request = new StudentRequest();
        request.setName("Messi");
        request.setEmail("messi@example.com");
        request.setAge(28);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(existingStudent));

        when(studentRepository.save(any(Student.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StudentResponse response = studentService.updateStudent(1L, request);

        assertEquals("Messi", response.getName());
        assertEquals("messi@example.com", response.getEmail());
        assertEquals(28, response.getAge());

        verify(studentRepository).findById(1L);
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    void updateStudent_shouldThrowException_whenStudentDoesNotExist() {

        StudentRequest request = new StudentRequest();
        request.setName("Messi");
        request.setEmail("messi@example.com");
        request.setAge(28);

        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,() -> studentService.updateStudent(99L, request));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

        verify(studentRepository).findById(99L);
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    void patchStudent_shouldUpdateOnlyProvidedFields() {

        Student existingStudent = new Student();
        existingStudent.setId(1L);
        existingStudent.setName("Cristiano");
        existingStudent.setEmail("old@example.com");
        existingStudent.setAge(25);

        StudentPatchRequest request = new StudentPatchRequest();
        request.setEmail("new@example.com");

        when(studentRepository.findById(1L))
                .thenReturn(Optional.of(existingStudent));

        when(studentRepository.save(any(Student.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        StudentResponse response = studentService.patchStudent(1L, request);

        assertEquals("Cristiano", response.getName());
        assertEquals("new@example.com", response.getEmail());
        assertEquals(25, response.getAge());

        verify(studentRepository).findById(1L);
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    void patchStudent_shouldThrowException_whenStudentDoesNotExist() {

        StudentPatchRequest request = new StudentPatchRequest();
        request.setEmail("new@example.com");

        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,() -> studentService.patchStudent(99L, request));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

        verify(studentRepository).findById(99L);
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    void deleteStudent_shouldDeleteStudent_whenStudentExists() {

        Student existingStudent = new Student();
        existingStudent.setId(1L);
        existingStudent.setName("Cristiano");
        existingStudent.setEmail("cristiano@example.com");
        existingStudent.setAge(25);

        when(studentRepository.findById(1L))
                .thenReturn(Optional.of(existingStudent));

        studentService.deleteStudent(1L);

        verify(studentRepository).findById(1L);
        verify(studentRepository).delete(existingStudent);
    }

    @Test
    void deleteStudent_shouldThrowException_whenStudentDoesNotExist() {

        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,() -> studentService.deleteStudent(99L));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

        verify(studentRepository).findById(99L);
        verify(studentRepository, never()).delete(any(Student.class));
    }
}
