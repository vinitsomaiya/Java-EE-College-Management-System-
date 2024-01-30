package acmecollege.rest.resource;
/**
 * File:  PersonResource.java Course materials (23S) CST 8277
 *
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * @author (original) Mike Norman
 * 
 * Updated by:  Group NN
 *   studentId, firstName, lastName (as from ACSIS)
 *   studentId, firstName, lastName (as from ACSIS)
 *   studentId, firstName, lastName (as from ACSIS)
 *   studentId, firstName, lastName (as from ACSIS)
 * 
 */

import static acmecollege.utility.MyConstants.COURSE_REGISTRATION_RESOURCE_NAME;
import static acmecollege.utility.MyConstants.ADMIN_ROLE;
import static acmecollege.utility.MyConstants.STUDENT_COURSE_PROFESSOR_RESOURCE_PATH;
import static acmecollege.utility.MyConstants.STUDENT_RESOURCE_NAME;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_ELEMENT;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_PATH;
import static acmecollege.utility.MyConstants.USER_ROLE;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.soteria.WrappingCallerPrincipal;

import acmecollege.ejb.ACMECollegeService;
import acmecollege.entity.Course;
import acmecollege.entity.CourseRegistration;
import acmecollege.entity.CourseRegistrationPK;
import acmecollege.entity.Professor;
import acmecollege.entity.SecurityUser;
import acmecollege.entity.Student;
/**
 * File:  CourseRegistration.java Course materials (23S) CST 8277
 *
 * @author Vedant Goswami
 * 
 * 
 */
@Path(COURSE_REGISTRATION_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)

public class CourseRegistrationResource {
	private static final Logger LOG = LogManager.getLogger();

    @EJB
    protected ACMECollegeService service;

    @Inject
    protected SecurityContext sc;
    
    
    @GET
    public Response getCourseRegistrations() {
        LOG.debug("Retrieving all ...");
        List<CourseRegistration> CourseRegistrations = service.getAllCourseRegistrations();
        LOG.debug("Student clubs found = {}", CourseRegistrations);
        Response response = Response.ok(CourseRegistrations).build();
        return response;
    }

        @GET
	    @RolesAllowed({ADMIN_ROLE,USER_ROLE})
        @Path("/{CourseRegistrationId}/{AnotherId}")
        public Response getCourseRegistrationByIds(
            @PathParam("CourseRegistrationId") int courseRegistrationId,
            @PathParam("AnotherId") int anotherId) {
            
            LOG.debug("Retrieving CourseRegistration with ids = {}, {}", courseRegistrationId, anotherId);

            CourseRegistration courseRegistration = service.getCourseRegistrationByIds(courseRegistrationId, anotherId);

            if (courseRegistration == null) {
                return Response.status(Status.NOT_FOUND).build();
            }

            Response response = Response.ok(courseRegistration).build();
            return response;
        }
    
      @DELETE
     @Path("/{CourseRegistrationId}/{AnotherId}")
	 @RolesAllowed({ADMIN_ROLE,USER_ROLE})
     public Response deleteCourseRegistrationByIds(
         @PathParam("CourseRegistrationId") int courseRegistrationId,
         @PathParam("AnotherId") int anotherId) {
         
         LOG.debug("Deleting course registration with ids = {}, {}", courseRegistrationId, anotherId);
         CourseRegistration courseRegistration = service.deleteCourseRegistrationByIds(courseRegistrationId, anotherId);
         
         if (courseRegistration == null) {
             return Response.status(Status.NOT_FOUND).build();
         }
         
         Response response = Response.ok(courseRegistration).build();
         return response;
     }
        
        @POST
	    @RolesAllowed({ADMIN_ROLE})
        public Response addCourseRegistration(CourseRegistration newCourseRegistration) {
            LOG.debug("Adding a new course registration = {}", newCourseRegistration);
            
            CourseRegistration tempCourseRegistration = service.persistCourseRegistration(newCourseRegistration);
            return Response.ok(tempCourseRegistration).build();
        }
}
