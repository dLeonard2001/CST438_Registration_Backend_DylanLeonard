package com.cst438.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.Student;
import com.cst438.domain.StudentRepository;
import org.springframework.web.client.RestTemplate;

import com.cst438.domain.EnrollmentDTO;


public class GradebookServiceREST extends GradebookService {
	
	@Autowired
	StudentRepository studentRepository;
	
	@Autowired
	CourseRepository courseRepository;

	private RestTemplate restTemplate = new RestTemplate();

	@Value("${gradebook.url}")
	String gradebook_url;
	
	public GradebookServiceREST() {
		System.out.println("REST grade book service");
	}

	@Override
	public void enrollStudent(String student_email, String student_name, int course_id) {
		
		//TODO  complete this method in homework 4
		EnrollmentDTO enrollment = new EnrollmentDTO();
		enrollment.course_id = course_id;
		enrollment.studentEmail = student_email;
		enrollment.studentName = student_name;
		
		System.out.println("Post to gradebook " + enrollment);
		EnrollmentDTO response = restTemplate.postForObject(gradebook_url+"/enrollment", enrollment, EnrollmentDTO.class); 
		System.out.println("Response from gradebook " + response);
		
	}

}
