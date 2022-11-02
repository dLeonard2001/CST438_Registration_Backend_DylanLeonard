package com.cst438;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.TimeUnit;
import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;
import com.cst438.domain.Student;
import com.cst438.domain.StudentRepository;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;



@SpringBootTest
public class EndToEndStudentTest {
	
	public static final String CHROME_DRIVER_FILE_LOCATION = "C:\\Users\\leona\\Desktop\\chromedriver.exe";

	public static final String URL = "http://localhost:3000";

	public static final String TEST_USER_EMAIL = "test@frontend.edu";

	public static final int TEST_COURSE_ID = 40443; 

	public static final String TEST_SEMESTER = "2021 Fall";
	
	public static final String TEST_STUDENT_NAME = "test";
	
	public static final int TEST_STUDENT_ID = 5; 

	public static final int SLEEP_DURATION = 1000; // 1 second.
	
	@Autowired
	EnrollmentRepository enrollmentRepository;
	
	@Autowired
	StudentRepository studentRepository;
	
	@Autowired
	CourseRepository courseRepository;
	
	// As an Admin, I can add a new student using our registration service
	
	@Test
	public void addStudentTest() throws Exception {
		
		// Delete
		Student student = null;
		
		do {
			
			//s = studentRepository.findBy(TEST_USER_EMAIL);
			student = studentRepository.findByEmail(TEST_USER_EMAIL);
			
			if (student != null) {
				studentRepository.delete(student);
			}
			
		} while (student != null);
		
		// set driver location and start
		
		System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
		WebDriver driver = new ChromeDriver();
		// Puts an Implicit wait for 10 seconds before throwing exception
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		
		try {
			driver.get(URL);
			Thread.sleep(SLEEP_DURATION);
			
			// Find and click "Add Student" button
			//driver.findElement(By.xpath("//a")).click();
			driver.findElement(By.xpath("//button[@id='add']")).click();
			Thread.sleep(SLEEP_DURATION);
			
			// Student name and email text fields
			driver.findElement(By.xpath("//input[@name='name']")).sendKeys(TEST_STUDENT_NAME);
			driver.findElement(By.xpath("//input[@name='email']")).sendKeys(TEST_USER_EMAIL);
			
			
			// "Add" button
			driver.findElement(By.xpath("//button[@id='Add']")).click();
			Thread.sleep(SLEEP_DURATION);
			
			// --- Verify new info ---
			Student new_student = studentRepository.findByEmail(TEST_USER_EMAIL);
			assertNotNull(new_student, "STUDENT WITH THIS EMAIL: " + TEST_USER_EMAIL + "DOES NOT EXIST!");
			
			// verify toast
			String toast_text = driver.findElement(By.cssSelector(".Toastify__toast-body div:nth-child(2)")).getText();
			assertEquals(toast_text, "New Student Added");
			
			
		} catch (Exception ex) {
			throw ex;
		} finally {
			
			// clean database
			Student student_to_clean = studentRepository.findByEmail(TEST_USER_EMAIL);
			if (student != null) {
				studentRepository.delete(student_to_clean);
			}
			
			driver.quit();
		}
	}
}