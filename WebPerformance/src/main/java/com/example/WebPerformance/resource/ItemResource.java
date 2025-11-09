package com.example.WebPerformance.resource;

import com.example.WebPerformance.dto.*;
import com.example.WebPerformance.entities.*;
import jakarta.persistence.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Path("/items")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ItemResource {

    @PersistenceContext
    private EntityManager em;

    // GET /items?page=&size=
    @GET
    public Response list(@QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("50") int size) {
        return listItems(page, size, null);
    }

    // GET /items?categoryId=&page=&size=
    @GET
    @Path("filter")
    public Response filterByCategory(@QueryParam("categoryId") Long categoryId,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("50") int size) {
        if (categoryId == null)
            return Response.status(Response.Status.BAD_REQUEST).build();
        return listItems(page, size, categoryId);
    }

    private Response listItems(int page, int size, Long categoryId) {
        String jpql = "SELECT i FROM Item i " +
                (categoryId != null ? "WHERE i.category.id = :catId " : "") +
                "ORDER BY i.id";

        TypedQuery<Item> query = em.createQuery(jpql, Item.class);
        if (categoryId != null)
            query.setParameter("catId", categoryId);

        query.setFirstResult(page * size);
        query.setMaxResults(size);

        List<ItemDto> dtos = query.getResultList().stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        String countJpql = "SELECT COUNT(i) FROM Item i " +
                (categoryId != null ? "WHERE i.category.id = :catId" : "");
        TypedQuery<Long> countQuery = em.createQuery(countJpql, Long.class);
        if (categoryId != null)
            countQuery.setParameter("catId", categoryId);
        long total = countQuery.getSingleResult();

        return Response.ok(new PageDto<>(dtos, page, size, total)).build();
    }

    // GET /items/{id}
    @GET
    @Path("{id}")
    public Response get(@PathParam("id") Long id) {
        Item item = em.find(Item.class, id);
        if (item == null)
            return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(toDto(item)).build();
    }

    // POST /items
    @POST
    public Response create(ItemDto dto, @Context UriInfo uriInfo) {
        Category cat = em.find(Category.class, dto.getCategoryId());
        if (cat == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid categoryId").build();

        Item item = Item.builder()
                .sku(dto.getSku())
                .name(dto.getName())
                .price(dto.getPrice())
                .stock(dto.getStock())
                .description(dto.getDescription())
                .category(cat)
                .updatedAt(LocalDateTime.now())
                .build();

        em.persist(item);
        em.flush();

        URI location = uriInfo.getAbsolutePathBuilder().path(item.getId().toString()).build();
        return Response.created(location).entity(toDto(item)).build();
    }

    // PUT /items/{id}
    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") Long id, ItemDto dto) {
        Item item = em.find(Item.class, id);
        if (item == null)
            return Response.status(Response.Status.NOT_FOUND).build();

        Category cat = em.find(Category.class, dto.getCategoryId());
        if (cat == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid categoryId").build();

        item.setSku(dto.getSku());
        item.setName(dto.getName());
        item.setPrice(dto.getPrice());
        item.setStock(dto.getStock());
        item.setDescription(dto.getDescription());
        item.setCategory(cat);
        item.setUpdatedAt(LocalDateTime.now());

        em.merge(item);
        return Response.ok(toDto(item)).build();
    }

    // DELETE /items/{id}
    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") Long id) {
        Item item = em.find(Item.class, id);
        if (item == null)
            return Response.status(Response.Status.NOT_FOUND).build();
        em.remove(item);
        return Response.noContent().build();
    }

    private ItemDto toDto(Item i) {
        return ItemDto.builder()
                .id(i.getId())
                .sku(i.getSku())
                .name(i.getName())
                .price(i.getPrice())
                .stock(i.getStock())
                .description(i.getDescription())
                .categoryId(i.getCategory().getId())
                .build();
    }
}