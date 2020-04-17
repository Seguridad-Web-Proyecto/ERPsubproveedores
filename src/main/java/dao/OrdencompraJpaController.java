/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import dao.exceptions.NonexistentEntityException;
import dao.exceptions.RollbackFailureException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entidades.Facturaventa;
import entidades.Ordencompra;
import entidades.Proveedor;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author jaker
 */
public class OrdencompraJpaController implements Serializable {

    public OrdencompraJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Ordencompra ordencompra) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Facturaventa facturaidFk = ordencompra.getFacturaidFk();
            if (facturaidFk != null) {
                facturaidFk = em.getReference(facturaidFk.getClass(), facturaidFk.getFacturaventaid());
                ordencompra.setFacturaidFk(facturaidFk);
            }
            Proveedor proveedoridFk = ordencompra.getProveedoridFk();
            if (proveedoridFk != null) {
                proveedoridFk = em.getReference(proveedoridFk.getClass(), proveedoridFk.getProveedorid());
                ordencompra.setProveedoridFk(proveedoridFk);
            }
            em.persist(ordencompra);
            if (facturaidFk != null) {
                facturaidFk.getOrdencompraCollection().add(ordencompra);
                facturaidFk = em.merge(facturaidFk);
            }
            if (proveedoridFk != null) {
                proveedoridFk.getOrdencompraCollection().add(ordencompra);
                proveedoridFk = em.merge(proveedoridFk);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Ordencompra ordencompra) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Ordencompra persistentOrdencompra = em.find(Ordencompra.class, ordencompra.getOrdenventaid());
            Facturaventa facturaidFkOld = persistentOrdencompra.getFacturaidFk();
            Facturaventa facturaidFkNew = ordencompra.getFacturaidFk();
            Proveedor proveedoridFkOld = persistentOrdencompra.getProveedoridFk();
            Proveedor proveedoridFkNew = ordencompra.getProveedoridFk();
            if (facturaidFkNew != null) {
                facturaidFkNew = em.getReference(facturaidFkNew.getClass(), facturaidFkNew.getFacturaventaid());
                ordencompra.setFacturaidFk(facturaidFkNew);
            }
            if (proveedoridFkNew != null) {
                proveedoridFkNew = em.getReference(proveedoridFkNew.getClass(), proveedoridFkNew.getProveedorid());
                ordencompra.setProveedoridFk(proveedoridFkNew);
            }
            ordencompra = em.merge(ordencompra);
            if (facturaidFkOld != null && !facturaidFkOld.equals(facturaidFkNew)) {
                facturaidFkOld.getOrdencompraCollection().remove(ordencompra);
                facturaidFkOld = em.merge(facturaidFkOld);
            }
            if (facturaidFkNew != null && !facturaidFkNew.equals(facturaidFkOld)) {
                facturaidFkNew.getOrdencompraCollection().add(ordencompra);
                facturaidFkNew = em.merge(facturaidFkNew);
            }
            if (proveedoridFkOld != null && !proveedoridFkOld.equals(proveedoridFkNew)) {
                proveedoridFkOld.getOrdencompraCollection().remove(ordencompra);
                proveedoridFkOld = em.merge(proveedoridFkOld);
            }
            if (proveedoridFkNew != null && !proveedoridFkNew.equals(proveedoridFkOld)) {
                proveedoridFkNew.getOrdencompraCollection().add(ordencompra);
                proveedoridFkNew = em.merge(proveedoridFkNew);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = ordencompra.getOrdenventaid();
                if (findOrdencompra(id) == null) {
                    throw new NonexistentEntityException("The ordencompra with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Ordencompra ordencompra;
            try {
                ordencompra = em.getReference(Ordencompra.class, id);
                ordencompra.getOrdenventaid();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The ordencompra with id " + id + " no longer exists.", enfe);
            }
            Facturaventa facturaidFk = ordencompra.getFacturaidFk();
            if (facturaidFk != null) {
                facturaidFk.getOrdencompraCollection().remove(ordencompra);
                facturaidFk = em.merge(facturaidFk);
            }
            Proveedor proveedoridFk = ordencompra.getProveedoridFk();
            if (proveedoridFk != null) {
                proveedoridFk.getOrdencompraCollection().remove(ordencompra);
                proveedoridFk = em.merge(proveedoridFk);
            }
            em.remove(ordencompra);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Ordencompra> findOrdencompraEntities() {
        return findOrdencompraEntities(true, -1, -1);
    }

    public List<Ordencompra> findOrdencompraEntities(int maxResults, int firstResult) {
        return findOrdencompraEntities(false, maxResults, firstResult);
    }

    private List<Ordencompra> findOrdencompraEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Ordencompra.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Ordencompra findOrdencompra(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Ordencompra.class, id);
        } finally {
            em.close();
        }
    }

    public int getOrdencompraCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Ordencompra> rt = cq.from(Ordencompra.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
