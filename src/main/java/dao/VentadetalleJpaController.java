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
import entidades.Producto;
import entidades.Ventadetalle;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author jaker
 */
public class VentadetalleJpaController implements Serializable {

    public VentadetalleJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Ventadetalle ventadetalle) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Producto productoidFk = ventadetalle.getProductoidFk();
            if (productoidFk != null) {
                productoidFk = em.getReference(productoidFk.getClass(), productoidFk.getProductoid());
                ventadetalle.setProductoidFk(productoidFk);
            }
            em.persist(ventadetalle);
            if (productoidFk != null) {
                productoidFk.getVentadetalleCollection().add(ventadetalle);
                productoidFk = em.merge(productoidFk);
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

    public void edit(Ventadetalle ventadetalle) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Ventadetalle persistentVentadetalle = em.find(Ventadetalle.class, ventadetalle.getVentaid());
            Producto productoidFkOld = persistentVentadetalle.getProductoidFk();
            Producto productoidFkNew = ventadetalle.getProductoidFk();
            if (productoidFkNew != null) {
                productoidFkNew = em.getReference(productoidFkNew.getClass(), productoidFkNew.getProductoid());
                ventadetalle.setProductoidFk(productoidFkNew);
            }
            ventadetalle = em.merge(ventadetalle);
            if (productoidFkOld != null && !productoidFkOld.equals(productoidFkNew)) {
                productoidFkOld.getVentadetalleCollection().remove(ventadetalle);
                productoidFkOld = em.merge(productoidFkOld);
            }
            if (productoidFkNew != null && !productoidFkNew.equals(productoidFkOld)) {
                productoidFkNew.getVentadetalleCollection().add(ventadetalle);
                productoidFkNew = em.merge(productoidFkNew);
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
                Integer id = ventadetalle.getVentaid();
                if (findVentadetalle(id) == null) {
                    throw new NonexistentEntityException("The ventadetalle with id " + id + " no longer exists.");
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
            Ventadetalle ventadetalle;
            try {
                ventadetalle = em.getReference(Ventadetalle.class, id);
                ventadetalle.getVentaid();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The ventadetalle with id " + id + " no longer exists.", enfe);
            }
            Producto productoidFk = ventadetalle.getProductoidFk();
            if (productoidFk != null) {
                productoidFk.getVentadetalleCollection().remove(ventadetalle);
                productoidFk = em.merge(productoidFk);
            }
            em.remove(ventadetalle);
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

    public List<Ventadetalle> findVentadetalleEntities() {
        return findVentadetalleEntities(true, -1, -1);
    }

    public List<Ventadetalle> findVentadetalleEntities(int maxResults, int firstResult) {
        return findVentadetalleEntities(false, maxResults, firstResult);
    }

    private List<Ventadetalle> findVentadetalleEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Ventadetalle.class));
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

    public Ventadetalle findVentadetalle(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Ventadetalle.class, id);
        } finally {
            em.close();
        }
    }

    public int getVentadetalleCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Ventadetalle> rt = cq.from(Ventadetalle.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
