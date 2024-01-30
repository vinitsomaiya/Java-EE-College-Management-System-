package acmecollege.rest.resource;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
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

import acmecollege.ejb.ACMECollegeService;
import acmecollege.entity.MembershipCard;

import static acmecollege.utility.MyConstants.ADMIN_ROLE;
import static acmecollege.utility.MyConstants.USER_ROLE;

import java.util.List;

/**
 * File:  MembershipCard.java Course materials (23S) CST 8277
 *
 * @author Harry Dandiwal
 * 
 *   041040008, Harry, Dandiwal
 * 
 */
@Path("/membershipcards")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MembershipCardResource {

	@EJB
    protected ACMECollegeService service;

    @POST
    @Transactional
    @RolesAllowed({ADMIN_ROLE})
    public Response createMembershipCard(MembershipCard membershipCard) {
    	service.createMembershipCard(membershipCard);
        return Response.status(Response.Status.CREATED).entity(membershipCard).build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    public Response getMembershipCardById(@PathParam("id") int id) {
        MembershipCard membershipCard = service.getMembershipCardById(id);
        if (membershipCard != null) {
            return Response.ok(membershipCard).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @RolesAllowed({ADMIN_ROLE})
    public Response deleteMembershipCard(@PathParam("id") int id) {
        // Implement logic to delete a membership card from the database
        boolean deleted = service.deleteMembershipCard(id);
        if (deleted) {          
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    public Response getAllMembershipCards() {
        List<MembershipCard> membershipCards = service.getAllMembershipCards();
        return Response.ok(membershipCards).build();
    }
}
