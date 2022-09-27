package com.cst438.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cst438.domain.Student;
import com.cst438.domain.StudentRepository;

@RestController
public class StudentController {
	
	@Autowired
	StudentRepository studentRepository;

	@PostMapping("/student")
	public Student addStudent(@RequestBody Student newStudent) {
		Student student = studentRepository.findByEmail(newStudent.getEmail());
		if(student == null) {
			System.out.println("adding new student...");
			student = createStudent(newStudent.getName(), newStudent.getEmail());
			student = studentRepository.save(student);
			return student;
		}else {
			System.out.println("student already exist...");
			throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "This student exists already" );
		}
	}
	
	@PutMapping("/student/{id}")
	public void updateStatus(@PathVariable int id, @RequestParam("status_code") int status_code, 
			@RequestParam("msg") String status_message) {
		Student student = studentRepository.findById(id).get();
		
		if(student.getStatusCode() == 0 && status_code != 0) {
			System.out.println("student has no hold, applying a hold...");
			student.setStatusCode(status_code);
			student.setStatus(status_message);
			student = studentRepository.save(student);
		}else if(student.getStatusCode() != 0 && status_code == 0) {
			System.out.println("Student has a hold, releasing the hold...");
			student.setStatusCode(status_code);
			student.setStatus(status_message);
			student = studentRepository.save(student);
		}
	}
	
	private Student createStudent(String name, String email) {
		Student student = new Student();
		student.setName(name);
		student.setEmail(email);
		student.setStatusCode(0);
		student.setStatus("completely new student");
		
		return student;
	}
	
}
