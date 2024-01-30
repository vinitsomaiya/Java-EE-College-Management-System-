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

import static acmecollege.utility.MyConstants.CLUB_MEMBERSHIP_RESOURCE_NAME;
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
import acmecollege.entity.ClubMembership;
import acmecollege.entity.Course;
import acmecollege.entity.CourseRegistration;
import acmecollege.entity.Professor;
import acmecollege.entity.SecurityUser;
import acmecollege.entity.Student;

@Path(CLUB_MEMBERSHIP_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ClubMembershipResource {
	private static final Logger LOG = LogManager.getLogger();

    @EJB
    protected ACMECollegeService service;

    @Inject
    protected SecurityContext sc;
    
    @GET
    public Response getClubMemberships() {
        LOG.debug("Retrieving all ...");
        List<ClubMembership> ClubMemberships = service.getAllClubMemberships();
        LOG.debug("Student clubs found = {}", ClubMemberships);
        Response response = Response.ok(ClubMemberships).build();
        return response;
    }
    
    @GET
    @RolesAllowed({ADMIN_ROLE,USER_ROLE})
    @Path("/{ClubMembershipId}")
    public Response getClubMembershipById(@PathParam("ClubMembershipId") int ClubMembershipId) {
        LOG.debug("Retrieving ClubMembership with id = {}", ClubMembershipId);
        ClubMembership ClubMembership = service.getClubMembershipById(ClubMembershipId);
        Response response = Response.ok(ClubMembership).build();
        return response;
    }
    
    
    @DELETE
    @RolesAllowed({ADMIN_ROLE})
    @Path("/{membershipId}")
    public Response deleteClubMembershipById(@PathParam("membershipId") int membershipId) {
      service.deleteClubMembershipById(membershipId);
        return Response.ok().build();
    }

    
    @POST
    @RolesAllowed({ADMIN_ROLE})
    public Response addClubMembership(ClubMembership newClubMembership) {
        LOG.debug("Adding a new student club = {}", newClubMembership);
            ClubMembership tempClubMembership = service.persistClubMembership(newClubMembership);
            return Response.ok(tempClubMembership).build();
        
    }
}
