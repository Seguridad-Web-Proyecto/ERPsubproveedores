/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import dao.exceptions.NonexistentEntityException;
import dao.exceptions.RollbackFailureException;
import entidades.Facturacompra;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entidades.Pagocompra;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author jaker
 */
public class FacturacompraJpaController implements Serializable {

    public FacturacompraJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Facturacompra facturacompra) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Pagocompra pagoidFk = facturacompra.getPagoidFk();
            if (pagoidFk != null) {
                pagoidFk = em.getReference(pagoidFk.getClass(), pagoidFk.getPagocompraid());
                facturacompra.setPagoidFk(pagoidFk);
            }
            em.persist(facturacompra);
            if (pagoidFk != null) {
                pagoidFk.getFacturacompraCollection().add(facturacompra);
                pagoidFk = em.merge(pagoidFk);
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

    public void edit(Facturacompra facturacompra) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Facturacompra persistentFacturacompra = em.find(Facturacompra.class, facturacompra.getFacturacompraid());
            Pagocompra pagoidFkOld = persistentFacturacompra.getPagoidFk();
            Pagocompra pagoidFkNew = facturacompra.getPagoidFk();
            if (pagoidFkNew != null) {
                pagoidFkNew = em.getReference(pagoidFkNew.getClass(), pagoidFkNew.getPagocompraid());
                facturacompra.setPagoidFk(pagoidFkNew);
            }
            facturacompra = em.merge(facturacompra);
            if (pagoidFkOld != null && !pagoidFkOld.equals(pagoidFkNew)) {
                pagoidFkOld.getFacturacompraCollection().remove(facturacompra);
                pagoidFkOld = em.merge(pagoidFkOld);
            }
            if (pagoidFkNew != null && !pagoidFkNew.equals(pagoidFkOld)) {
                pagoidFkNew.getFacturacompraCollection().add(facturacompra);
                pagoidFkNew = em.merge(pagoidFkNew);
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
                Integer id = facturacompra.getFacturacompraid();
                if (findFacturacompra(id) == null) {
                    throw new NonexistentEntityException("The facturacompra with id " + id + " no longer exists.");
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
            Facturacompra facturacompra;
            try {
                facturacompra = em.getReference(Facturacompra.class, id);
                facturacompra.getFacturacompraid();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The facturacompra with id " + id + " no longer exists.", enfe);
            }
            Pagocompra pagoidFk = facturacompra.getPagoidFk();
            if (pagoidFk != null) {
                pagoidFk.getFacturacompraCollection().remove(facturacompra);
                pagoidFk = em.merge(pagoidFk);
            }
            em.remove(facturacompra);
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

    public List<Facturacompra> findFacturacompraEntities() {
        return findFacturacompraEntities(true, -1, -1);
    }

    public List<Facturacompra> findFacturacompraEntities(int maxResults, int firstResult) {
        return findFacturacompraEntities(false, maxResults, firstResult);
    }

    private List<Facturacompra> findFacturacompraEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Facturacompra.class));
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

    public Facturacompra findFacturacompra(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Facturacompra.class, id);
        } finally {
            em.close();
        }
    }

    public int getFacturacompraCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Facturacompra> rt = cq.from(Facturacompra.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
