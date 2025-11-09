package com.example.WebPerformance.resource;

import com.example.WebPerformance.dto.CategoryDto;
import com.example.WebPerformance.dto.PageDto;
import com.example.WebPerformance.entities.Category;
import jakarta.persistence.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Path("/categories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CategoryResource {

    @PersistenceContext
    private EntityManager em;

    // GET /categories?page=&size=
    @GET
    public Response list(@QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("50") int size) {
        TypedQuery<Category> query = em.createQuery("SELECT c FROM Category c ORDER BY c.id", Category.class);
        query.setFirstResult(page * size);
        query.setMaxResults(size);

        List<CategoryDto> dtos = query.getResultList().stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        long total = em.createQuery("SELECT COUNT(c) FROM Category c", Long.class).getSingleResult();

        return Response.ok(new PageDto<>(dtos, page, size, total)).build();
    }

    // GET /categories/{id}
    @GET
    @Path("{id}")
    public Response get(@PathParam("id") Long id) {
        Category cat = em.find(Category.class, id);
        if (cat == null)
            return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(toDto(cat)).build();
    }

    // POST /categories
    @POST
    public Response create(CategoryDto dto, @Context UriInfo uriInfo) {
        Category cat = Category.builder()
                .code(dto.getCode())
                .name(dto.getName())
                .updatedAt(LocalDateTime.now())
                .build();

        em.persist(cat);
        em.flush();

        URI location = uriInfo.getAbsolutePathBuilder().path(cat.getId().toString()).build();
        return Response.created(location).entity(toDto(cat)).build();
    }

    // PUT /categories/{id}
    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") Long id, CategoryDto dto) {
        Category cat = em.find(Category.class, id);
        if (cat == null)
            return Response.status(Response.Status.NOT_FOUND).build();

        cat.setCode(dto.getCode());
        cat.setName(dto.getName());
        cat.setUpdatedAt(LocalDateTime.now());

        em.merge(cat);
        return Response.ok(toDto(cat)).build();
    }

    // DELETE /categories/{id}
    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") Long id) {
        Category cat = em.find(Category.class, id);
        if (cat == null)
            return Response.status(Response.Status.NOT_FOUND).build();

        em.remove(cat);
        return Response.noContent().build();
    }

    private CategoryDto toDto(Category c) {
        return CategoryDto.builder()
                .id(c.getId())
                .code(c.getCode())
                .name(c.getName())
                .build();
    }
}