package acmecollege.rest.resource;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import static acmecollege.utility.MyConstants.ADMIN_ROLE;
import static acmecollege.utility.MyConstants.USER_ROLE;
import javax.ws.rs.core.Response.Status;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import acmecollege.ejb.ACMECollegeService;
import acmecollege.entity.Course;
/**
 * File:  ProfessorResource.java Course materials (23S) CST 8277
 *
 * @author Jeetvishnubh Patel
 * 
 *  Updated by : 041040008, Harry, Dandiwal
 * 
 */
@Path("course")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CourseResource {

	    private static final Logger LOG = LogManager.getLogger();

	    @EJB
	    protected ACMECollegeService service;

	    @Inject
	    protected SecurityContext sc;
	    
	    @GET
	    public Response getCourses() {
	        LOG.debug("Retrieving all ...");
	        List<Course> courses = service.getAllCourses();
	        LOG.debug("Student clubs found = {}", courses);
	        Response response = Response.ok(courses).build();
	        return response;
	    }
	    
	    @GET
	    @RolesAllowed({ADMIN_ROLE,USER_ROLE})
	    @Path("/{courseId}")
	    public Response getCourseById(@PathParam("courseId") int courseId) {
	        LOG.debug("Retrieving Course with id = {}", courseId);
	        Course course = service.getCourseById(courseId);
	        Response response = Response.ok(course).build();
	        return response;
	    }
	    
	    
	    @RolesAllowed({ADMIN_ROLE})
	    @POST
	    public Response addCourse(Course newCourse) {
	        LOG.debug("Adding a new student club = {}", newCourse);
	            Course tempCourse = service.persistCourse(newCourse);
	            return Response.ok(tempCourse).build();
	        
	    }
	    
	    @DELETE
	    @RolesAllowed({ADMIN_ROLE})
	    @Path("/{courseId}")
	    public Response deleteCourseById(@PathParam("courseId") int cId) {
	        LOG.debug("Deleting student club with id = {}", cId);
	        Course c = service.deleteCourseById(cId);
	        if (c != null) {
	            return Response.ok().build();
	        }
	        return Response.status(Status.NOT_FOUND).build();
	    }
}
