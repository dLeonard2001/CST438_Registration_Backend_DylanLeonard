package com.cst438;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.cst438.controller.StudentController;
import com.cst438.domain.Student;
import com.cst438.domain.StudentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = { StudentController.class })
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest
class JunitTestStudentController {
	
	static final String URL = "http://localhost:8080";
	
	public static final String test_student_email= "test4@csumb.edu";
	public static final String test_student_name = "dylan";
	public static final int test_student_id = 4;
	public static final int test_status_code_releaseHold = 0;
	public static final int test_status_code_placeHold = 1;
	
	@MockBean
	StudentRepository studentRepository;

	@Autowired
	private MockMvc mvc;

	@Test
	void addStudent() throws Exception {
		MockHttpServletResponse response;

		Student student = new Student();
		
		student.setStudent_id(test_student_id);
		student.setName(test_student_name);
		student.setEmail(test_student_email);
		
		given(studentRepository.save(any(Student.class))).willReturn(student);
		
		response = mvc.perform(
				MockMvcRequestBuilders
			      .post("/student")
			      .content(asJsonString(student))
			      .contentType(MediaType.APPLICATION_JSON)
			      .accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		
		assertEquals(200, response.getStatus());
		
		Student verified = fromJsonString(response.getContentAsString(), Student.class);
		assertNotEquals(0, verified.getStudent_id());
				
		verify(studentRepository).save(any(Student.class));
	}
	
	@Test
	public void updateStatus_placehold() throws Exception {
		MockHttpServletResponse response;
		
		boolean hold;

		Student student = new Student();
		
		student.setStudent_id(test_student_id);
		student.setName(test_student_name);
		student.setStatus(null);
		student.setStatusCode(test_status_code_releaseHold);
		
		given(studentRepository.findById(test_student_id)).willReturn(Optional.of(student));
		given(studentRepository.save(any(Student.class))).willReturn(student);
		
		response = mvc.perform(
				MockMvcRequestBuilders
			      .put("/student/" + test_student_id + "?status_code=1&msg=Student has not paid ")
			      .content(asJsonString(student))
			      .contentType(MediaType.APPLICATION_JSON)
			      .accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		
		assertEquals(200, response.getStatus());
		
		verify(studentRepository, times(1)).findById(test_student_id);
		
		if (student.getStatusCode() != 0) {
			hold = true;
			assertEquals(true, hold, "Success!");
		}
		
	}
	
	@Test
	public void updateStatus_releaseHold() throws Exception {
		MockHttpServletResponse response;
		
		boolean hold;

		Student student = new Student();
		
		student.setStudent_id(test_student_id);
		student.setName(test_student_name);
		student.setStatus(null);
		student.setStatusCode(test_status_code_placeHold);
		
		given(studentRepository.findById(test_student_id)).willReturn(Optional.of(student));
		given(studentRepository.save(any(Student.class))).willReturn(student);
		
		response = mvc.perform(
				MockMvcRequestBuilders
			      .put("/student/" + test_student_id + "?status_code=0&msg=No hold")
			      .content(asJsonString(student))
			      .contentType(MediaType.APPLICATION_JSON)
			      .accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		
		assertEquals(200, response.getStatus());
		
		verify(studentRepository, times(1)).findById(test_student_id);
		
		
		if (student.getStatusCode() == 0) {
			hold = true;
			assertEquals(true, hold, "!Passed");
		}
		
	}
	
	private static String asJsonString(final Object obj) {
		try {

			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static <T> T  fromJsonString(String str, Class<T> valueType ) {
		try {
			
			return new ObjectMapper().readValue(str, valueType);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
