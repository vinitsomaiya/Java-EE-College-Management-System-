package acmecollege.rest.resource;

import static acmecollege.utility.MyConstants.ADMIN_ROLE;
import static acmecollege.utility.MyConstants.USER_ROLE;
import static acmecollege.utility.MyConstants.PROFESSOR_SUBRESOURCE_NAME;

import acmecollege.ejb.ACMECollegeService;
import acmecollege.entity.Professor;
import acmecollege.entity.SecurityUser;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.soteria.WrappingCallerPrincipal;

import java.util.List;
/**
 * File:  ProfessorResource.java Course materials (23S) CST 8277
 *
 * @author Harry Dandiwal
 * 
 *   041040008, Harry, Dandiwal
 * 
 */
@Path(PROFESSOR_SUBRESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProfessorResource {

    @EJB
    protected ACMECollegeService service;
    
    @Inject
    protected SecurityContext sc;

    @GET
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    public Response getProfessors() {
        List<Professor> professors = service.getAllProfessors();
        return Response.ok(professors).build();
    }

    @GET
    @Path("{id}")
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    public Response getProfessorById(@PathParam("id") int id) {
        Professor professor = null;
            professor = service.getProfessorById(id);
            return Response.status(professor == null ? Status.NOT_FOUND : Status.OK).entity(professor).build();
    }

    @POST
    @RolesAllowed({ADMIN_ROLE})
    public Response addProfessor(Professor newProfessor) {
        Professor newProfessorWithIdTimestamps = service.persistProfessor(newProfessor);
        return Response.ok(newProfessorWithIdTimestamps).build();
    }

    @DELETE
    @Path("{id}")
    @RolesAllowed({ADMIN_ROLE})
    public Response deleteProfessor(@PathParam("id") int id) {
        boolean deleted = service.deleteProfessor(id);
        if (deleted) {
            return Response.ok().build();
        }
        return Response.status(Status.NOT_FOUND).build();
    }
    
    
}
