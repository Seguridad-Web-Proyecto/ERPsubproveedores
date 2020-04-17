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
import entidades.Cliente;
import entidades.Facturaventa;
import entidades.Ordenventa;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author jaker
 */
public class OrdenventaJpaController implements Serializable {

    public OrdenventaJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Ordenventa ordenventa) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Cliente clienteidFk = ordenventa.getClienteidFk();
            if (clienteidFk != null) {
                clienteidFk = em.getReference(clienteidFk.getClass(), clienteidFk.getClienteid());
                ordenventa.setClienteidFk(clienteidFk);
            }
            Facturaventa facturaidFk = ordenventa.getFacturaidFk();
            if (facturaidFk != null) {
                facturaidFk = em.getReference(facturaidFk.getClass(), facturaidFk.getFacturaventaid());
                ordenventa.setFacturaidFk(facturaidFk);
            }
            em.persist(ordenventa);
            if (clienteidFk != null) {
                clienteidFk.getOrdenventaCollection().add(ordenventa);
                clienteidFk = em.merge(clienteidFk);
            }
            if (facturaidFk != null) {
                facturaidFk.getOrdenventaCollection().add(ordenventa);
                facturaidFk = em.merge(facturaidFk);
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

    public void edit(Ordenventa ordenventa) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Ordenventa persistentOrdenventa = em.find(Ordenventa.class, ordenventa.getOrdenventaid());
            Cliente clienteidFkOld = persistentOrdenventa.getClienteidFk();
            Cliente clienteidFkNew = ordenventa.getClienteidFk();
            Facturaventa facturaidFkOld = persistentOrdenventa.getFacturaidFk();
            Facturaventa facturaidFkNew = ordenventa.getFacturaidFk();
            if (clienteidFkNew != null) {
                clienteidFkNew = em.getReference(clienteidFkNew.getClass(), clienteidFkNew.getClienteid());
                ordenventa.setClienteidFk(clienteidFkNew);
            }
            if (facturaidFkNew != null) {
                facturaidFkNew = em.getReference(facturaidFkNew.getClass(), facturaidFkNew.getFacturaventaid());
                ordenventa.setFacturaidFk(facturaidFkNew);
            }
            ordenventa = em.merge(ordenventa);
            if (clienteidFkOld != null && !clienteidFkOld.equals(clienteidFkNew)) {
                clienteidFkOld.getOrdenventaCollection().remove(ordenventa);
                clienteidFkOld = em.merge(clienteidFkOld);
            }
            if (clienteidFkNew != null && !clienteidFkNew.equals(clienteidFkOld)) {
                clienteidFkNew.getOrdenventaCollection().add(ordenventa);
                clienteidFkNew = em.merge(clienteidFkNew);
            }
            if (facturaidFkOld != null && !facturaidFkOld.equals(facturaidFkNew)) {
                facturaidFkOld.getOrdenventaCollection().remove(ordenventa);
                facturaidFkOld = em.merge(facturaidFkOld);
            }
            if (facturaidFkNew != null && !facturaidFkNew.equals(facturaidFkOld)) {
                facturaidFkNew.getOrdenventaCollection().add(ordenventa);
                facturaidFkNew = em.merge(facturaidFkNew);
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
                Integer id = ordenventa.getOrdenventaid();
                if (findOrdenventa(id) == null) {
                    throw new NonexistentEntityException("The ordenventa with id " + id + " no longer exists.");
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
            Ordenventa ordenventa;
            try {
                ordenventa = em.getReference(Ordenventa.class, id);
                ordenventa.getOrdenventaid();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The ordenventa with id " + id + " no longer exists.", enfe);
            }
            Cliente clienteidFk = ordenventa.getClienteidFk();
            if (clienteidFk != null) {
                clienteidFk.getOrdenventaCollection().remove(ordenventa);
                clienteidFk = em.merge(clienteidFk);
            }
            Facturaventa facturaidFk = ordenventa.getFacturaidFk();
            if (facturaidFk != null) {
                facturaidFk.getOrdenventaCollection().remove(ordenventa);
                facturaidFk = em.merge(facturaidFk);
            }
            em.remove(ordenventa);
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

    public List<Ordenventa> findOrdenventaEntities() {
        return findOrdenventaEntities(true, -1, -1);
    }

    public List<Ordenventa> findOrdenventaEntities(int maxResults, int firstResult) {
        return findOrdenventaEntities(false, maxResults, firstResult);
    }

    private List<Ordenventa> findOrdenventaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Ordenventa.class));
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

    public Ordenventa findOrdenventa(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Ordenventa.class, id);
        } finally {
            em.close();
        }
    }

    public int getOrdenventaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Ordenventa> rt = cq.from(Ordenventa.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
