/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import dao.exceptions.IllegalOrphanException;
import dao.exceptions.NonexistentEntityException;
import dao.exceptions.RollbackFailureException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entidades.Tarjetacreditocompra;
import entidades.Facturacompra;
import entidades.Pagocompra;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author jaker
 */
public class PagocompraJpaController implements Serializable {

    public PagocompraJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Pagocompra pagocompra) throws RollbackFailureException, Exception {
        if (pagocompra.getFacturacompraCollection() == null) {
            pagocompra.setFacturacompraCollection(new ArrayList<Facturacompra>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Tarjetacreditocompra tarjetacreditoidFk = pagocompra.getTarjetacreditoidFk();
            if (tarjetacreditoidFk != null) {
                tarjetacreditoidFk = em.getReference(tarjetacreditoidFk.getClass(), tarjetacreditoidFk.getTarjetacreditocompraid());
                pagocompra.setTarjetacreditoidFk(tarjetacreditoidFk);
            }
            Collection<Facturacompra> attachedFacturacompraCollection = new ArrayList<Facturacompra>();
            for (Facturacompra facturacompraCollectionFacturacompraToAttach : pagocompra.getFacturacompraCollection()) {
                facturacompraCollectionFacturacompraToAttach = em.getReference(facturacompraCollectionFacturacompraToAttach.getClass(), facturacompraCollectionFacturacompraToAttach.getFacturacompraid());
                attachedFacturacompraCollection.add(facturacompraCollectionFacturacompraToAttach);
            }
            pagocompra.setFacturacompraCollection(attachedFacturacompraCollection);
            em.persist(pagocompra);
            if (tarjetacreditoidFk != null) {
                tarjetacreditoidFk.getPagocompraCollection().add(pagocompra);
                tarjetacreditoidFk = em.merge(tarjetacreditoidFk);
            }
            for (Facturacompra facturacompraCollectionFacturacompra : pagocompra.getFacturacompraCollection()) {
                Pagocompra oldPagoidFkOfFacturacompraCollectionFacturacompra = facturacompraCollectionFacturacompra.getPagoidFk();
                facturacompraCollectionFacturacompra.setPagoidFk(pagocompra);
                facturacompraCollectionFacturacompra = em.merge(facturacompraCollectionFacturacompra);
                if (oldPagoidFkOfFacturacompraCollectionFacturacompra != null) {
                    oldPagoidFkOfFacturacompraCollectionFacturacompra.getFacturacompraCollection().remove(facturacompraCollectionFacturacompra);
                    oldPagoidFkOfFacturacompraCollectionFacturacompra = em.merge(oldPagoidFkOfFacturacompraCollectionFacturacompra);
                }
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

    public void edit(Pagocompra pagocompra) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Pagocompra persistentPagocompra = em.find(Pagocompra.class, pagocompra.getPagocompraid());
            Tarjetacreditocompra tarjetacreditoidFkOld = persistentPagocompra.getTarjetacreditoidFk();
            Tarjetacreditocompra tarjetacreditoidFkNew = pagocompra.getTarjetacreditoidFk();
            Collection<Facturacompra> facturacompraCollectionOld = persistentPagocompra.getFacturacompraCollection();
            Collection<Facturacompra> facturacompraCollectionNew = pagocompra.getFacturacompraCollection();
            List<String> illegalOrphanMessages = null;
            for (Facturacompra facturacompraCollectionOldFacturacompra : facturacompraCollectionOld) {
                if (!facturacompraCollectionNew.contains(facturacompraCollectionOldFacturacompra)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Facturacompra " + facturacompraCollectionOldFacturacompra + " since its pagoidFk field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (tarjetacreditoidFkNew != null) {
                tarjetacreditoidFkNew = em.getReference(tarjetacreditoidFkNew.getClass(), tarjetacreditoidFkNew.getTarjetacreditocompraid());
                pagocompra.setTarjetacreditoidFk(tarjetacreditoidFkNew);
            }
            Collection<Facturacompra> attachedFacturacompraCollectionNew = new ArrayList<Facturacompra>();
            for (Facturacompra facturacompraCollectionNewFacturacompraToAttach : facturacompraCollectionNew) {
                facturacompraCollectionNewFacturacompraToAttach = em.getReference(facturacompraCollectionNewFacturacompraToAttach.getClass(), facturacompraCollectionNewFacturacompraToAttach.getFacturacompraid());
                attachedFacturacompraCollectionNew.add(facturacompraCollectionNewFacturacompraToAttach);
            }
            facturacompraCollectionNew = attachedFacturacompraCollectionNew;
            pagocompra.setFacturacompraCollection(facturacompraCollectionNew);
            pagocompra = em.merge(pagocompra);
            if (tarjetacreditoidFkOld != null && !tarjetacreditoidFkOld.equals(tarjetacreditoidFkNew)) {
                tarjetacreditoidFkOld.getPagocompraCollection().remove(pagocompra);
                tarjetacreditoidFkOld = em.merge(tarjetacreditoidFkOld);
            }
            if (tarjetacreditoidFkNew != null && !tarjetacreditoidFkNew.equals(tarjetacreditoidFkOld)) {
                tarjetacreditoidFkNew.getPagocompraCollection().add(pagocompra);
                tarjetacreditoidFkNew = em.merge(tarjetacreditoidFkNew);
            }
            for (Facturacompra facturacompraCollectionNewFacturacompra : facturacompraCollectionNew) {
                if (!facturacompraCollectionOld.contains(facturacompraCollectionNewFacturacompra)) {
                    Pagocompra oldPagoidFkOfFacturacompraCollectionNewFacturacompra = facturacompraCollectionNewFacturacompra.getPagoidFk();
                    facturacompraCollectionNewFacturacompra.setPagoidFk(pagocompra);
                    facturacompraCollectionNewFacturacompra = em.merge(facturacompraCollectionNewFacturacompra);
                    if (oldPagoidFkOfFacturacompraCollectionNewFacturacompra != null && !oldPagoidFkOfFacturacompraCollectionNewFacturacompra.equals(pagocompra)) {
                        oldPagoidFkOfFacturacompraCollectionNewFacturacompra.getFacturacompraCollection().remove(facturacompraCollectionNewFacturacompra);
                        oldPagoidFkOfFacturacompraCollectionNewFacturacompra = em.merge(oldPagoidFkOfFacturacompraCollectionNewFacturacompra);
                    }
                }
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
                Integer id = pagocompra.getPagocompraid();
                if (findPagocompra(id) == null) {
                    throw new NonexistentEntityException("The pagocompra with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Pagocompra pagocompra;
            try {
                pagocompra = em.getReference(Pagocompra.class, id);
                pagocompra.getPagocompraid();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The pagocompra with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Facturacompra> facturacompraCollectionOrphanCheck = pagocompra.getFacturacompraCollection();
            for (Facturacompra facturacompraCollectionOrphanCheckFacturacompra : facturacompraCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Pagocompra (" + pagocompra + ") cannot be destroyed since the Facturacompra " + facturacompraCollectionOrphanCheckFacturacompra + " in its facturacompraCollection field has a non-nullable pagoidFk field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Tarjetacreditocompra tarjetacreditoidFk = pagocompra.getTarjetacreditoidFk();
            if (tarjetacreditoidFk != null) {
                tarjetacreditoidFk.getPagocompraCollection().remove(pagocompra);
                tarjetacreditoidFk = em.merge(tarjetacreditoidFk);
            }
            em.remove(pagocompra);
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

    public List<Pagocompra> findPagocompraEntities() {
        return findPagocompraEntities(true, -1, -1);
    }

    public List<Pagocompra> findPagocompraEntities(int maxResults, int firstResult) {
        return findPagocompraEntities(false, maxResults, firstResult);
    }

    private List<Pagocompra> findPagocompraEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Pagocompra.class));
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

    public Pagocompra findPagocompra(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Pagocompra.class, id);
        } finally {
            em.close();
        }
    }

    public int getPagocompraCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Pagocompra> rt = cq.from(Pagocompra.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
