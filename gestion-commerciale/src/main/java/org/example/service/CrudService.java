package org.example.service;

import org.example.dao.GenericDAO;
import org.example.entities.Produit;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.example.dao.GenericDAO.emf;

public class CrudService<T, ID extends java.io.Serializable> {
    private final GenericDAO<T, ID> dao;

    public CrudService(Class<T> entityClass) {
        this.dao = new GenericDAO<>(entityClass);
    }

    public T getById(ID id) {
        return dao.find(id);
    }

    public List<T> getAll() {
        return dao.findAll();
    }

    public void create(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("L'entité ne doit pas être nulle");
        }
        dao.create(entity);
    }

    public T update(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("L'entité ne doit pas être nulle");
        }
        return dao.update(entity);
    }

    public void delete(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("L'entité ne doit pas être nulle");
        }
        dao.delete(entity);
    }

    // Named Query
    public List<Produit> findByCategorie(Long catId) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createNamedQuery("Produit.findByCategorie", Produit.class)
                    .setParameter("catId", catId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // Criteria API dynamique
    public List<Produit> search(String term, BigDecimal minPrix, BigDecimal maxPrix) {
        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Produit> cq = cb.createQuery(Produit.class);
            Root<Produit> root = cq.from(Produit.class);
            List<Predicate> predicates = new ArrayList<>();
            if (term != null && !term.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("libelle")), "%" + term.toLowerCase() + "%"));
            }
            if (minPrix != null) {
                predicates.add(cb.ge(root.get("prix"), minPrix));
            }
            if (maxPrix != null) {
                predicates.add(cb.le(root.get("prix"), maxPrix));
            }
            cq.where(predicates.toArray(new Predicate[0]));
            return em.createQuery(cq).getResultList();
        } finally {
            em.close();
        }
    }
    public List<Produit> findPage(int pageNumber, int pageSize) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT p FROM Produit p ORDER BY p.id", Produit.class)
                    .setFirstResult((pageNumber - 1) * pageSize)
                    .setMaxResults(pageSize)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public Produit findByIdAvecCategorie(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT p FROM Produit p JOIN FETCH p.categorie WHERE p.id = :id", Produit.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }


}