/**
 * File:  TestACMECollegeSystem.java
 * Course materials (23S) CST 8277
 * Teddy Yap
 * (Original Author) Mike Norman
 *
 * @date 2020 10
 *
 * (Modified) @author Harry Dandiwal, Jeet Patel, Vinit Somaiya, Vedant Goswami
 */
package acmecollege;

import static acmecollege.utility.MyConstants.APPLICATION_API_VERSION;
import static acmecollege.utility.MyConstants.APPLICATION_CONTEXT_ROOT;
import static acmecollege.utility.MyConstants.DEFAULT_ADMIN_USER;
import static acmecollege.utility.MyConstants.DEFAULT_ADMIN_USER_PASSWORD;
import static acmecollege.utility.MyConstants.DEFAULT_USER;
import static acmecollege.utility.MyConstants.DEFAULT_USER_PASSWORD;
import static acmecollege.utility.MyConstants.PROFESSOR_SUBRESOURCE_NAME;
import static acmecollege.utility.MyConstants.STUDENT_RESOURCE_NAME;
import static acmecollege.utility.MyConstants.STUDENT_CLUB_RESOURCE_NAME;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.logging.LoggingFeature;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import acmecollege.entity.ClubMembership;
import acmecollege.entity.Course;
import acmecollege.entity.CourseRegistration;
import acmecollege.entity.CourseRegistrationPK;
import acmecollege.entity.Professor;
import acmecollege.entity.Student;
import acmecollege.entity.StudentClub;

@SuppressWarnings("unused")

@TestMethodOrder(MethodOrderer.MethodName.class)
public class TestACMECollegeSystem {
    private static final Class<?> _thisClaz = MethodHandles.lookup().lookupClass();
    private static final Logger logger = LogManager.getLogger(_thisClaz);

    static final String HTTP_SCHEMA = "http";
    static final String HOST = "localhost";
    static final int PORT = 8080;

    // Test fixture(s)
    static URI uri;
    static HttpAuthenticationFeature adminAuth;
    static HttpAuthenticationFeature userAuth;
    
    Professor newProfessor;
    
   

    @BeforeAll
    public static void oneTimeSetUp() throws Exception {
        logger.debug("oneTimeSetUp");
        uri = UriBuilder
            .fromUri(APPLICATION_CONTEXT_ROOT + APPLICATION_API_VERSION)
            .scheme(HTTP_SCHEMA)
            .host(HOST)
            .port(PORT)
            .build();
        adminAuth = HttpAuthenticationFeature.basic(DEFAULT_ADMIN_USER, DEFAULT_ADMIN_USER_PASSWORD);
        userAuth = HttpAuthenticationFeature.basic(DEFAULT_USER, DEFAULT_USER_PASSWORD);
    }

    protected WebTarget webTarget;
    @BeforeEach
    public void setUp() {
        Client client = ClientBuilder.newClient(
            new ClientConfig().register(MyObjectMapperProvider.class).register(new LoggingFeature()));
        webTarget = client.target(uri);
    }
    
    @AfterEach
    public void cleanup() {
        if (newProfessor != null) {
        	// Delete the professor using its ID
            Response deleteResponse = webTarget
                .register(adminAuth)
                .path(PROFESSOR_SUBRESOURCE_NAME)
                .path(Integer.toString(newProfessor.getId()))
                .request()
                .delete();
        }
    }


    @Test
    @Order(1)
    public void test01_all_students_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            //.register(userAuth)
            .register(adminAuth)
            .path(STUDENT_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        List<Student> students = response.readEntity(new GenericType<List<Student>>(){});
        assertThat(students, is(not(empty())));
        //assertThat(students, hasSize(1));
    }   
    
    
    @Test
    @Order(2)
    public void test02_create_student_with_adminrole() {
    	
    	Student student = new Student();
        student.setFullName("John", "Doe");
        //student.setId(101);

        Response response = webTarget
            .register(adminAuth)
            .path(STUDENT_RESOURCE_NAME)
            .request()
            .post(Entity.json(student));

        assertThat(response.getStatus(), is(200));
        
        Student createdStudent = response.readEntity(Student.class);
        assertNotNull(createdStudent.getId());
        assertThat(createdStudent.getFirstName(), is("John"));
        assertThat(createdStudent.getLastName(), is("Doe"));     
        
    	// Delete the student using its ID
        Response deleteResponse = webTarget
            .register(adminAuth)
            .path(STUDENT_RESOURCE_NAME)
            .path(Integer.toString(createdStudent.getId()))
            .request()
            .delete();
    }
    
    @Test
    @Order(3)
    public void test03_create_student_with_user() {
    	Student student = new Student();
        student.setFullName("Jane", "Smith");

        Response response = webTarget
            .register(userAuth)
            .path(STUDENT_RESOURCE_NAME)
            .request()
            .post(Entity.json(student));

        assertThat(response.getStatus(), is(403));
    }
    
    @Test
    @Order(4)
    public void test04_create_student_with_invalid_data() {
    	Student student = new Student();

        Response response = webTarget
            .register(adminAuth)
            .path(STUDENT_RESOURCE_NAME)
            .request()
            .post(Entity.json(student));

        assertThat(response.getStatus(), is(500));
    }

    @Test
    @Order(5)
    public void test05_delete_student_with_adminrole() {
        // create a student to be deleted
    	Student student = new Student();
        student.setFullName("Test", "Test");      

        Response createResponse = webTarget
            .register(adminAuth)
            .path(STUDENT_RESOURCE_NAME)
            .request()
            .post(Entity.json(student));

        assertThat(createResponse.getStatus(), is(200));
        Student createdStudent = createResponse.readEntity(Student.class);
        assertNotNull(createdStudent.getId());

        // delete the created student
        Response deleteResponse = webTarget
            .register(adminAuth)
            .path(STUDENT_RESOURCE_NAME + "/" + createdStudent.getId())
            .request()
            .delete();

        assertThat(deleteResponse.getStatus(), is(200));

        // Verify that the student has been deleted by trying to get the student by ID
        Response getResponse = webTarget
            .register(adminAuth)
            .path(STUDENT_RESOURCE_NAME + "/" + createdStudent.getId())
            .request()
            .get();

        assertThat(getResponse.getStatus(), is(404));
    }
    
    //Course Resource 
    @Test
    @Order(6)
    public void test06_GetAllCourses_with_Admin_Role()throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            //.register(userAuth)
            .register(adminAuth)
            .path("course")
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        //  List<Course> courses = response.readEntity(new GenericType<List<Course>>(){});
//        assertThat(courses, is(not(empty())));
//        assertThat(courses, hasSize(2));
    }
    
    @Test
    @Order(7)
    public void test07_GetAllCourses_with_User_Role()throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            //.register(userAuth)
            .register(adminAuth)
            .path("course")
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
    }
    
    @Test
    @Order(8)
    public void test08_GetCourseById_With_AdminRole()throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            //.register(userAuth)
            .register(adminAuth)
            .path("course/1")
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
    }
    
    @Test
    @Order(9)
    public void test09_GetCourseById_With_UserRole()throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(userAuth)
            //.register(adminAuth)
            .path("course/1")
            .request()
            .get();
    }
    
    //Professor Resource

    @Test
    @Order(10)
    public void test10_all_professors_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path(PROFESSOR_SUBRESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        List<Professor> professors = response.readEntity(new GenericType<List<Professor>>(){});
        assertThat(professors, is(not(empty())));
        //assertThat(professors, hasSize(1));
    }

    @Test
    @Order(11)
    public void test11_get_professor_by_id_with_adminrole() throws JsonMappingException, JsonProcessingException {
        int professorId = 1; // Assuming professor with ID 1 exists
        Response response = webTarget
            .register(adminAuth)
            .path(PROFESSOR_SUBRESOURCE_NAME)
            .path(String.valueOf(professorId))
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        Professor professor = response.readEntity(Professor.class);
        assertThat(professor, is(notNullValue()));
        assertThat(professor.getId(), is(professorId));
    }
    
    @Test
    @Order(12)
    public void test12_getAllProfessorsWithInvalidData() {
        Response response = webTarget
            .register(adminAuth)
            .path("invalid-path")
            .request()
            .get();

        assertThat(response.getStatus(), is(404));
    }
    
    @Test
    @Order(13)
    public void test13_getProfessorByIdWithInvalidData() {
        int invalidProfessorId = 7788; 
        Response response = webTarget
            .register(adminAuth)
            .path(PROFESSOR_SUBRESOURCE_NAME)
            .path(Integer.toString(invalidProfessorId))
            .request()
            .get();

        assertThat(response.getStatus(), is(404));
    }
    
    @Test
    @Order(14)
    public void test14_createProfessorWithAdmin() {
        newProfessor = new Professor();
        newProfessor.setFirstName("Jane");
        newProfessor.setLastName("Smith");
        newProfessor.setDepartment("Mathematics");      

        Response response = webTarget
            .register(adminAuth)
            .path(PROFESSOR_SUBRESOURCE_NAME)
            .request()
            .post(Entity.json(newProfessor));

        assertThat(response.getStatus(), is(200));
    }
    
    @Test
    @Order(15)
    public void test15_deleteProfessor() {
    	// Create a new professor
        newProfessor = new Professor();
        newProfessor.setFirstName("John");
        newProfessor.setLastName("Doe");
        newProfessor.setDepartment("Computer Science");

        Response createResponse = webTarget
            .register(adminAuth)
            .path(PROFESSOR_SUBRESOURCE_NAME)
            .request()
            .post(Entity.json(newProfessor));

        assertThat(createResponse.getStatus(), is(200));

        // Get the ID of the created professor from the response
        newProfessor = createResponse.readEntity(Professor.class);
        int professorId = newProfessor.getId();

        // Delete the professor using its ID
        Response deleteResponse = webTarget
            .register(adminAuth)
            .path(PROFESSOR_SUBRESOURCE_NAME)
            .path(Integer.toString(professorId))
            .request()
            .delete();

        assertThat(deleteResponse.getStatus(), is(200));
    }
    
    @Test
    @Order(16)
    public void test16_deleteProfessorWithInvalidData() {
        int invalidProfessorId = 999; // 
        Response response = webTarget
            .register(adminAuth)
            .path(PROFESSOR_SUBRESOURCE_NAME)
            .path(Integer.toString(invalidProfessorId))
            .request()
            .delete();

        assertThat(response.getStatus(), is(404));
    }
    
    
    
    @Test
    @Order(17)
    public void test17_createProfessorWithUser() {
        newProfessor = new Professor();
        newProfessor.setFirstName("Jake");
        newProfessor.setLastName("Johnson");
        newProfessor.setDepartment("Physics");

        Response response = webTarget
            .register(userAuth)
            .path(PROFESSOR_SUBRESOURCE_NAME)
            .request()
            .post(Entity.json(newProfessor));

        assertThat(response.getStatus(), is(403));
    }


    @Test
    @Order(18)
    public void test18_createProfessorWithInvalidDataAdmin() {
        newProfessor = new Professor();
        newProfessor.setFirstName("John");
        newProfessor.setLastName(null);
        newProfessor.setDepartment("CS");

        Response response = webTarget
            .register(adminAuth)
            .path(PROFESSOR_SUBRESOURCE_NAME)
            .request()
            .post(Entity.json(newProfessor));

        assertThat(response.getStatus(), is(500));
    }

    @Test
    @Order(19)
    public void test19_dataReturnedIsJson() {
        Response response = webTarget
            .register(adminAuth)
            .path(PROFESSOR_SUBRESOURCE_NAME)
            .request()
            //.accept(MediaType.APPLICATION_JSON)
            .get();

        assertThat(response.getStatus(), is(200));
        assertThat(response.getHeaderString("Content-Type"), is(MediaType.APPLICATION_JSON));
    }
    
    //Student Club
    @Test
    @Order(20)
    public void test20_get_studentclub_by_id_with_adminrole() throws JsonMappingException, JsonProcessingException {
        int sid = 1; // Assuming professor with ID 1 exists
        Response response = webTarget
            .register(adminAuth)
            .path(STUDENT_CLUB_RESOURCE_NAME)
            .path(String.valueOf(sid))
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        StudentClub sc = response.readEntity(StudentClub.class);
        assertThat(sc, is(notNullValue()));
        assertThat(sc.getId(), is(sid));
    }
    
    @Test
    @Order(21)
    public void test21_getStudentClubByIdWithInvalidData() {
        int invalidsid = 7788; 
        Response response = webTarget
            .register(adminAuth)
            .path(STUDENT_CLUB_RESOURCE_NAME)
            .path(Integer.toString(invalidsid))
            .request()
            .get();

        assertThat(response.getStatus(), is(500));
    }
    
    @Test
    @Order(21)
    public void test021_AddCourse_withUserRole()throws JsonMappingException, JsonProcessingException {
      
    	String jsonPayload = "{"
                + "\"courseCode\": \"CST8101\","
                + "\"courseTitle\": \"Computer Essentials\","
                + "\"year\": 2022,"
                + "\"semester\": \"WINTER\","
                + "\"creditUnits\": 3,"
                + "\"online\": 0"
                + "}";
    	Response response = webTarget
            .register(userAuth)
            .path("course")
            .request()
            .post(Entity.json(jsonPayload));
        assertThat(response.getStatus(), is(403));
       
    }
    
    @Test
    @Order(22)
    public void test22_AddCourse_withInvalidDataAdminRole()throws JsonMappingException, JsonProcessingException {
    	Course course = new Course();
    	Response response = webTarget
            .register(adminAuth)
            .path("course")
            .request()
            .post(Entity.json(course));
        assertThat(response.getStatus(), is(500));
         
  
    }
    
    @Test
    @Order(3)
    public void test23_AddCourse_withAdminRole()throws JsonMappingException, JsonProcessingException {
    	String jsonPayload = "{"
                + "\"courseCode\": \"CST8201\","
                + "\"courseTitle\": \"Business Analysis\","
                + "\"year\": 2022,"
                + "\"semester\": \"WINTER\","
                + "\"creditUnits\": 3,"
                + "\"online\": 0"
                + "}";
    	Response response = webTarget
             .register(adminAuth)
            .path("course")
            .request()
            .post(Entity.json(jsonPayload));
        assertThat(response.getStatus(), is(200));
        Course course = response.readEntity(Course.class);
        assertThat(course, is(notNullValue())); 
        
        //cleanup
        webTarget
  	  	.register(adminAuth)
         .path("course/"+course.getId())
         .request()
         .delete(); 
    }

    
    @Test
    @Order(24)
    public void test24_DeleteCourse_withAdminRole()throws JsonMappingException, JsonProcessingException {
    	String jsonPayload = "{"
                + "\"courseCode\": \"CST8201\","
                + "\"courseTitle\": \"Business Analysis\","
                + "\"year\": 2022,"
                + "\"semester\": \"WINTER\","
                + "\"creditUnits\": 3,"
                + "\"online\": 0"
                + "}";
    	Response response = webTarget
             .register(adminAuth)
            .path("course")
            .request()
            .post(Entity.json(jsonPayload));
    	Course course = response.readEntity(Course.class);
    	
           Response responseDelete = webTarget
        	  .register(adminAuth)
               .path("course/"+course.getId())
               .request()
               .delete();

           assertThat(response.getStatus(), is(200)); 
    }
 
    @Test
    @Order(25)
    public void test25_DeleteCourse_withUserRole()throws JsonMappingException, JsonProcessingException {
    	   int id = 3; // 
           Response response = webTarget
        	  .register(userAuth)
               .path("course/"+id)
               .request()
               .delete();

           assertThat(response.getStatus(), is(403));
  
    }
    
    @Test
    @Order(26)
    public void test26_DeleteCourse_withInvalidPathAdmin()throws JsonMappingException, JsonProcessingException {
    	   // 
           Response response = webTarget
        	  .register(userAuth)
               .path("course/4/4")
               .request()
               .delete();

           assertThat(response.getStatus(), is(404));
  
    }
    
    //course registration
    @Test
    @Order(27)
    public void test27_getAllCourseRegistrationsWithAdminRole() {
        Response response = webTarget
            .register(adminAuth)
            .path("courseregistration")
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
    }

    @Test
    @Order(28)
    public void test28_getAllCourseRegistrationsWithUserRole() {
        Response response = webTarget
            .register(userAuth)
            .path("courseregistration")
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
    }
    
    @Test
    @Order(29)
    public void test29_createCourseRegistrationWithUser() {
        CourseRegistration newCourseRegistration = new CourseRegistration();

        Student student = new Student();
        student.setFirstName("Michael");
        student.setLastName("Smith");
        newCourseRegistration.setStudent(student);

        Course course = new Course();
        course.setCourseCode("CST8101");
        course.setCourseTitle("Computer Essentials");
        course.setYear(2022);
        course.setSemester("WINTER");
        course.setCreditUnits(3);
        course.setOnline((byte) 0);
        newCourseRegistration.setCourse(course);

        Professor professor = new Professor();
        professor.setFirstName("Jane");
        professor.setLastName("Smith");
        professor.setDepartment("Computer Science");
        professor.setHighestEducationalDegree("PhD");
        professor.setSpecialization("Artificial Intelligence");
        newCourseRegistration.setProfessor(professor);

        // Set numeric and letter grades
        newCourseRegistration.setNumericGrade(90);
        newCourseRegistration.setLetterGrade("A");

        Response response = webTarget
            .register(userAuth)
            .path("courseregistration")
            .request()
            .post(Entity.json(newCourseRegistration));

        assertThat(response.getStatus(), is(403));
    }

   

    @Test
    @Order(31)
     public void test30_getCourseRegistrationByIdWithInvalidDataUserRole() {
         int courseRegistrationId = 1; // Assuming CourseRegistration with ID 1 exists

         Response response = webTarget
             .register(userAuth)
             .path("courseregistration/" + courseRegistrationId)
             .request()
             .get();

         assertThat(response.getStatus(), is(404));
     }

     @Test
     @Order(31)
     public void test31_getCourseRegistrationByIdWithInvalidId() {
         int invalidCourseRegistrationId = 999; // Invalid ID

         Response response = webTarget
             .register(adminAuth)
             .path("courseregistration/" + invalidCourseRegistrationId)
             .request()
             .get();

         assertThat(response.getStatus(), is(404));
     }

     @Test
     @Order(32)
     public void test32_dataReturnedIsJsonForCourseRegistration() {
         Response response = webTarget
             .register(adminAuth)
             .path("courseregistration")
             .request()
             .get();

         assertThat(response.getStatus(), is(200));
         assertThat(response.getHeaderString("Content-Type"), is(MediaType.APPLICATION_JSON));
     }
     
     @Test
     @Order(33)
     public void test33_getAllClubMembershipWithAdminRole() {
         Response response = webTarget
             .register(adminAuth)
             .path("clubmembership")
             .request()
             .get();
         assertThat(response.getStatus(), is(200));
     }
     
     @Test
     @Order(34)
     public void test34_getAllClubMembershipWithUserRole() {
         Response response = webTarget
             .register(userAuth)
             .path("clubmembership")
             .request()
             .get();
         assertThat(response.getStatus(), is(200));
     }
     

     @Test
     @Order(35)
     public void test35_getAllClubMembershipWithInvalidEndpoint() {
         Response response = webTarget
             .register(adminAuth)
             .path("invalid_endpoint")
             .request()
             .get();
         assertThat(response.getStatus(), is(404)); 
     }
     
     @Test
     @Order(36)
     public void test36_deleteClubMembershipById() {
         int clubMembershipId = 1; 

         Response response = webTarget
             .register(adminAuth)
             .path("clubmembership" + "/" + clubMembershipId)
             .request()
             .delete();
         assertThat(response.getStatus(), is(200));
     }

     
     @Test
     @Order(37)
     public void test37_getClubMembershipByIdWithUserRole() {
         int clubmembershipId = 1; 

         Response response = webTarget
            .register(userAuth)
             .path("clubmembership/" + clubmembershipId)
            .request()
             .get();

         assertThat(response.getStatus(), is(200));
     }
     
     @Test
     @Order(38)
     public void test38_getClubMembershipByIdWithAdminRole() {
         int clubmembershipId = 1; 

         Response response = webTarget
             .register(adminAuth)
             .path("clubmembership/" + clubmembershipId)
             .request()
             .get();

         assertThat(response.getStatus(), is(200));
     }       
     
     @Test
     @Order(40)
     public void test40_getCourseRegistrationsWithInvalidEndpoint() {
    	 Response response = webTarget
                 .register(adminAuth)
                 .path("cr")
                 .request()
                 .get();
             assertThat(response.getStatus(), is(404)); 
         }
  
}