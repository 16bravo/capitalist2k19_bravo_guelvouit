/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.isis20;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import generated.World;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;

/**
 * REST Web Service
 *
 * @author cbrav
 */
@Path("generic")
public class GenericResource {

    @Context
    private UriInfo context;
    private int ref;
    private Services services;
    
    final GsonBuilder builder = new GsonBuilder();
    final Gson gson = builder.create();

    /**
     * Creates a new instance of GenericResource
     */
    public GenericResource() {
        services = new Services();
        ref = 1;
    }

    /**
     * Retrieves representation of an instance of com.mycompany.isis20.GenericResource
     * @return an instance of world
     */
    @GET
    @Produces(MediaType.APPLICATION_XML)
    
    public World getXml() {
       return services.readWorldFromXml();
    }
    
    /**
     * Retrieves representation of an instance of com.mycompany.isis20.GenericResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("world")
    public String getJson() {
       return gson.toJson(getXml());
    }

    /**
     * PUT method for updating or creating an instance of GenericResource
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/xml")
    public void putXml(String content) {
    }
}
