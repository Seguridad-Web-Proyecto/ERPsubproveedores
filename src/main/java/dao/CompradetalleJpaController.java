/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import dao.exceptions.NonexistentEntityException;
import dao.exceptions.RollbackFailureException;
import entidades.Compradetalle;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entidades.Producto;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author jaker
 */
public class CompradetalleJpaController implements Serializable {

    public CompradetalleJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Compradetalle compradetalle) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Producto productoidFk = compradetalle.getProductoidFk();
            if (productoidFk != null) {
                productoidFk = em.getReference(productoidFk.getClass(), productoidFk.getProductoid());
                compradetalle.setProductoidFk(productoidFk);
            }
            em.persist(compradetalle);
            if (productoidFk != null) {
                productoidFk.getCompradetalleCollection().add(compradetalle);
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

    public void edit(Compradetalle compradetalle) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Compradetalle persistentCompradetalle = em.find(Compradetalle.class, compradetalle.getCompraid());
            Producto productoidFkOld = persistentCompradetalle.getProductoidFk();
            Producto productoidFkNew = compradetalle.getProductoidFk();
            if (productoidFkNew != null) {
                productoidFkNew = em.getReference(productoidFkNew.getClass(), productoidFkNew.getProductoid());
                compradetalle.setProductoidFk(productoidFkNew);
            }
            compradetalle = em.merge(compradetalle);
            if (productoidFkOld != null && !productoidFkOld.equals(productoidFkNew)) {
                productoidFkOld.getCompradetalleCollection().remove(compradetalle);
                productoidFkOld = em.merge(productoidFkOld);
            }
            if (productoidFkNew != null && !productoidFkNew.equals(productoidFkOld)) {
                productoidFkNew.getCompradetalleCollection().add(compradetalle);
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
                Integer id = compradetalle.getCompraid();
                if (findCompradetalle(id) == null) {
                    throw new NonexistentEntityException("The compradetalle with id " + id + " no longer exists.");
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
            Compradetalle compradetalle;
            try {
                compradetalle = em.getReference(Compradetalle.class, id);
                compradetalle.getCompraid();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The compradetalle with id " + id + " no longer exists.", enfe);
            }
            Producto productoidFk = compradetalle.getProductoidFk();
            if (productoidFk != null) {
                productoidFk.getCompradetalleCollection().remove(compradetalle);
                productoidFk = em.merge(productoidFk);
            }
            em.remove(compradetalle);
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

    public List<Compradetalle> findCompradetalleEntities() {
        return findCompradetalleEntities(true, -1, -1);
    }

    public List<Compradetalle> findCompradetalleEntities(int maxResults, int firstResult) {
        return findCompradetalleEntities(false, maxResults, firstResult);
    }

    private List<Compradetalle> findCompradetalleEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Compradetalle.class));
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

    public Compradetalle findCompradetalle(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Compradetalle.class, id);
        } finally {
            em.close();
        }
    }

    public int getCompradetalleCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Compradetalle> rt = cq.from(Compradetalle.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
