package org.example;

import org.example.entities.Categorie;
import org.example.entities.Commande;
import org.example.entities.Produit;
import org.example.service.CrudService;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.example.dao.GenericDAO.emf;

public class Main {
    public static void main(String[] args) {
        CrudService<Categorie, Long> catService = new CrudService<>(Categorie.class);
        CrudService<Produit, Long> produitService = new CrudService<>(Produit.class);

        Categorie cat = new Categorie("Informatique " + System.currentTimeMillis());
        catService.create(cat);

        Produit p1 = new Produit("Clavier", new BigDecimal("49.99"), cat);
        Produit p2 = new Produit("Souris", new BigDecimal("19.99"), cat);
        produitService.create(p1);
        produitService.create(p2);

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Produit m1 = em.find(Produit.class, p1.getId());
            Produit m2 = em.find(Produit.class, p2.getId());

            Commande cmd = new Commande();
            cmd.setDateCommande(LocalDate.now());
            cmd.addProduit(m1, 3);
            cmd.addProduit(m2, 5);

            em.persist(cmd);
            tx.commit();
            System.out.println("Commande créée avec ID = " + cmd.getId());
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}