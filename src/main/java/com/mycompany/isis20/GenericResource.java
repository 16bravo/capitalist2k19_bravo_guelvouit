package com.mycompany.isis20;

import com.google.gson.Gson;
import generated.PallierType;
import generated.ProductType;
import generated.World;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Web Service
 *
 * @author cbrav
 */
@RestController
@RequestMapping("/webresources/generic")
public class GenericResource {

    private final Services services = new Services();
    private final Gson gson = new Gson();

    private static String user(HttpServletRequest request) {
        return request.getHeader("X-User");
    }

    @GetMapping(value = "/world", produces = MediaType.APPLICATION_XML_VALUE)
    public World getXml(HttpServletRequest request) throws FileNotFoundException, JAXBException {
        return services.getWorld(user(request));
    }

    @GetMapping(value = "/world", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getJson(HttpServletRequest request) throws FileNotFoundException, JAXBException {
        return gson.toJson(services.getWorld(user(request)));
    }

    @PutMapping(value = "/product", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void putProduct(@RequestBody String content, HttpServletRequest request) throws JAXBException, FileNotFoundException {
        ProductType product = gson.fromJson(content, ProductType.class);
        services.updateProduct(user(request), product);
    }

    @PutMapping(value = "/manager", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void putManager(@RequestBody String content, HttpServletRequest request) throws JAXBException, FileNotFoundException {
        PallierType manager = gson.fromJson(content, PallierType.class);
        services.updateManager(user(request), manager);
    }

    @PutMapping(value = "/upgrade", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void putUpgrade(@RequestBody String content, HttpServletRequest request) throws JAXBException, FileNotFoundException {
        PallierType upgrade = gson.fromJson(content, PallierType.class);
        services.updateUpgrade(user(request), upgrade);
    }
}
