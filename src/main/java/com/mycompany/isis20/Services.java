/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.isis20;

import generated.PallierType;
import generated.ProductType;
import generated.World;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.*;

/**
 *
 * @author cbrav
 */
public class Services {

    public World readWorldFromXml(String username) throws FileNotFoundException {

        try {
            JAXBContext cont = JAXBContext.newInstance(World.class);
            Unmarshaller u = cont.createUnmarshaller();
            InputStream input = null;
            try {
                input = new FileInputStream(username + "-world.xml");
            } catch (Exception e) {
                input = getClass().getClassLoader().getResourceAsStream("world.xml");
            }

            World world = (World) u.unmarshal(input);
            
            return world;
        } catch (JAXBException ex) {
            Logger.getLogger(Services.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void saveWorldToXml(String username, World world) throws JAXBException, FileNotFoundException {
        try {
            JAXBContext cont = JAXBContext.newInstance(World.class);
            Marshaller m = cont.createMarshaller();

            //System.out.println("adresse (saveWorld) : "+adr);
            FileOutputStream output = new FileOutputStream(username + "-world.xml");
            m.marshal(world, output);
        } catch (JAXBException ex) {
            Logger.getLogger(Services.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public World getWorld(String username) throws FileNotFoundException, JAXBException{
        World world = readWorldFromXml(username);
        updateScore(world);
        saveWorldToXml(username,world);
        return world;
    }

    private ProductType findProductById(World world, int id) {
        for (ProductType pt : world.getProducts().getProduct()) {
            if (pt.getId() == id) {
                return pt;
            }
        }
        return null;
    }

    private PallierType findManagerByName(World world, String name) {
        for (PallierType pt : world.getManagers().getPallier()) {
            if (pt.getName().equals(name)) {
                return pt;
            }
        }
        return null;
    }

    // prend en paramètre le pseudo du joueur et le produit
    // sur lequel une action a eu lieu (lancement manuel de production ou 
    // achat d’une certaine quantité de produit) 
    // renvoie false si l’action n’a pas pu être traitée
    public Boolean updateProduct(String username, ProductType newproduct) throws JAXBException, FileNotFoundException {
        // aller chercher le monde qui correspond au joueur
        World world = getWorld(username);
        // trouver dans ce monde, le produit équivalent à celui passé en paramètre
        ProductType product = findProductById(world, newproduct.getId());
        if (product == null) {
            System.out.println("sortie : product null");
            return false;
        }
        // calculer la variation de quantité. Si elle est positive c'est que le joueur a acheté une certaine quantité de ce produit sinon c’est qu’il s’agit d’un lancement de production.
        int qtchange = newproduct.getQuantite() - product.getQuantite();
        //System.out.println("qtchange = np " + newproduct.getQuantite() + " - p " + product.getQuantite()+" = "+qtchange);

        if (qtchange > 0) {
            //achat
            // soustraire de l'argent du joueur le cout de la quantité achetée et mettre à jour la quantité de product
            world.setMoney(world.getMoney() - product.getCout() * ((1 - Math.pow(product.getCroissance(), qtchange)) / (1 - product.getCroissance())));
            product.setQuantite(product.getQuantite() + qtchange);
            product.setCout(product.getCout() * Math.pow(product.getCroissance(), qtchange));

            //gérer les unlocks
            for (PallierType p : product.getPalliers().getPallier()) {
                if (p.isUnlocked() == false && p.getSeuil() <= product.getQuantite()) {
                    p.setUnlocked(true);
                    if (p.getTyperatio().equals("GAIN")) {
                        product.setRevenu(product.getRevenu() * p.getRatio());
                    } else if (p.getTyperatio().equals("VITESSE")) {
                        product.setVitesse((int) Math.round(product.getVitesse() / p.getRatio()));
                        product.setTimeleft((long) Math.round(product.getTimeleft() / p.getRatio()));
                    } else if (p.getTyperatio().equals("ANGE")) {
                        //pas defini dans produit, lier aux angelupgrades etc
                    }
                }
            }
        } else {
            //production
            // initialiser product.timeleft à product.vitesse pour lancer la production
            product.setTimeleft(product.getVitesse());
            /*world.setMoney(world.getMoney() + product.getQuantite() * product.getRevenu() * 1000);//enlever le x1000
             world.setScore(world.getScore() + product.getQuantite() * product.getRevenu());*/
        }
        // sauvegarder les changements du monde
        saveWorldToXml(username, world);
        return true;
    }

    // prend en paramètre le pseudo du joueur et le manager acheté.
    // renvoie false si l’action n’a pas pu être traitée 
    public Boolean updateManager(String username, PallierType newmanager) throws JAXBException, FileNotFoundException {
        // aller chercher le monde qui correspond au joueur
        World world = getWorld(username);
        // trouver dans ce monde, le manager équivalent à celui passé en paramètre
        PallierType manager = findManagerByName(world, newmanager.getName());
        if (manager == null) {
            return false;
        }
        // débloquer ce manager
        manager.setUnlocked(true);
        // trouver le produit correspondant au manager
        ProductType product = findProductById(world, manager.getIdcible());
        if (product == null) {
            return false;
        }
        System.out.println(product);
        // débloquer le manager de ce produit
        product.setManagerUnlocked(true);
        // soustraire de l'argent du joueur le cout du manager
        world.setMoney(world.getMoney() - manager.getSeuil());
        // sauvegarder les changements au monde
        saveWorldToXml(username, world);
        return true;
    }

    private void updateScore(World world) {
        //Combien de temps le jeu a été mis "en veille"
        long tempsEcoule = System.currentTimeMillis() - world.getLastupdate();
        for (ProductType p : world.getProducts().getProduct()) {
            //Si le manager du produit a été débloqué (2 cas : le produit a pu être produit ou non dans le temps de veille)
            if (p.isManagerUnlocked()) {
                long t = tempsEcoule - p.getTimeleft();
                if (t >= 0) {
                    //le produit a terminé sa production
                    //Calcul de la nombre de productions pdt tps de veille
                    int n = (int) Math.floor(t / p.getVitesse());
                    //Mise à jour de l'argent et du score
                    world.setMoney(world.getMoney() + p.getRevenu() * n * p.getQuantite());//enelever le x1000
                    world.setScore(world.getScore() + p.getRevenu() * n * p.getQuantite());
                    p.setTimeleft(n * p.getVitesse() - t);
                } else {
                    //le produit n'a toujours pas fini sa production
                    p.setTimeleft(p.getTimeleft() - tempsEcoule);
                }
            } else {
                //pas de manager, le produit arrête sa production direct quand il a fini mais on doit pouvoir voir la production d'un long produit qd mm
                if (tempsEcoule > p.getTimeleft() && p.getTimeleft() > 0) {
                    //Le temps est ecoule
                    //Mise a jour des scores
                    world.setMoney(world.getMoney() + p.getRevenu() * p.getQuantite());//enlever le x1000
                    world.setScore(world.getScore() + p.getRevenu() * p.getQuantite());
                    p.setTimeleft(0);
                } else if(tempsEcoule < p.getTimeleft()){
                    //On met a jour le temps restant -> maj de la barre ?
                    p.setTimeleft(p.getTimeleft()-tempsEcoule);
                }
            }
        }
        //Mise à jour
        world.setLastupdate(System.currentTimeMillis());
    }
}
