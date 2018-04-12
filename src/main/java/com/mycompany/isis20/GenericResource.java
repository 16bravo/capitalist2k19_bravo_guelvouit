/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.isis20;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import generated.PallierType;
import generated.ProductType;
import generated.World;
import java.io.FileNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;

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
    private String username;

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
     * Retrieves representation of an instance of
     * com.mycompany.isis20.GenericResource
     *
     * @return an instance of world
     */
    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("world")
    public World getXml(@Context HttpServletRequest request) throws FileNotFoundException, JAXBException {
        username = request.getHeader("X-User");
        //return services.readWorldFromXml(username);
        return services.getWorld(username);
    }

    /**
     * Retrieves representation of an instance of
     * com.mycompany.isis20.GenericResource
     *
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("world")
    public String getJson(@Context HttpServletRequest request) throws FileNotFoundException, JAXBException {
        username = request.getHeader("X-User");
        //return gson.toJson(services.readWorldFromXml(username));
        return gson.toJson(services.getWorld(username));
    }

    /**
     * PUT method for updating or creating an instance of GenericResource
     *
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/json")
    @Path("product")
    public void putProduct(String content,@Context HttpServletRequest request) throws JAXBException, FileNotFoundException {
        //System.out.println(content);//afiche le XML du product
        ProductType product = new Gson().fromJson(content, ProductType.class);
        username = request.getHeader("X-User");
        services.updateProduct(username, product);
    }

    /**
     * PUT method for updating or creating an instance of GenericResource
     *
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/json")
    @Path("manager")
    public void putManager(String content,@Context HttpServletRequest request) throws JAXBException, FileNotFoundException {
        PallierType manager = new Gson().fromJson(content, PallierType.class);
        username = request.getHeader("X-User");
        services.updateManager(username, manager);
    }
    
    /**
     * PUT method for updating or creating an instance of GenericResource
     *
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/json")
    @Path("upgrade")
    public void putUpgrade(String content,@Context HttpServletRequest request) throws JAXBException, FileNotFoundException {
        
    }
}
