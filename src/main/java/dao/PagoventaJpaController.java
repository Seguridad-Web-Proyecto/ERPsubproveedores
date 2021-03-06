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
import entidades.Tarjetacreditoventa;
import entidades.Facturaventa;
import entidades.Pagoventa;
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
public class PagoventaJpaController implements Serializable {

    public PagoventaJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Pagoventa pagoventa) throws RollbackFailureException, Exception {
        if (pagoventa.getFacturaventaCollection() == null) {
            pagoventa.setFacturaventaCollection(new ArrayList<Facturaventa>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Tarjetacreditoventa tarjetacreditoidFk = pagoventa.getTarjetacreditoidFk();
            if (tarjetacreditoidFk != null) {
                tarjetacreditoidFk = em.getReference(tarjetacreditoidFk.getClass(), tarjetacreditoidFk.getTarjetacreditoventaid());
                pagoventa.setTarjetacreditoidFk(tarjetacreditoidFk);
            }
            Collection<Facturaventa> attachedFacturaventaCollection = new ArrayList<Facturaventa>();
            for (Facturaventa facturaventaCollectionFacturaventaToAttach : pagoventa.getFacturaventaCollection()) {
                facturaventaCollectionFacturaventaToAttach = em.getReference(facturaventaCollectionFacturaventaToAttach.getClass(), facturaventaCollectionFacturaventaToAttach.getFacturaventaid());
                attachedFacturaventaCollection.add(facturaventaCollectionFacturaventaToAttach);
            }
            pagoventa.setFacturaventaCollection(attachedFacturaventaCollection);
            em.persist(pagoventa);
            if (tarjetacreditoidFk != null) {
                tarjetacreditoidFk.getPagoventaCollection().add(pagoventa);
                tarjetacreditoidFk = em.merge(tarjetacreditoidFk);
            }
            for (Facturaventa facturaventaCollectionFacturaventa : pagoventa.getFacturaventaCollection()) {
                Pagoventa oldPagoidFkOfFacturaventaCollectionFacturaventa = facturaventaCollectionFacturaventa.getPagoidFk();
                facturaventaCollectionFacturaventa.setPagoidFk(pagoventa);
                facturaventaCollectionFacturaventa = em.merge(facturaventaCollectionFacturaventa);
                if (oldPagoidFkOfFacturaventaCollectionFacturaventa != null) {
                    oldPagoidFkOfFacturaventaCollectionFacturaventa.getFacturaventaCollection().remove(facturaventaCollectionFacturaventa);
                    oldPagoidFkOfFacturaventaCollectionFacturaventa = em.merge(oldPagoidFkOfFacturaventaCollectionFacturaventa);
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

    public void edit(Pagoventa pagoventa) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Pagoventa persistentPagoventa = em.find(Pagoventa.class, pagoventa.getPagoventaid());
            Tarjetacreditoventa tarjetacreditoidFkOld = persistentPagoventa.getTarjetacreditoidFk();
            Tarjetacreditoventa tarjetacreditoidFkNew = pagoventa.getTarjetacreditoidFk();
            Collection<Facturaventa> facturaventaCollectionOld = persistentPagoventa.getFacturaventaCollection();
            Collection<Facturaventa> facturaventaCollectionNew = pagoventa.getFacturaventaCollection();
            List<String> illegalOrphanMessages = null;
            for (Facturaventa facturaventaCollectionOldFacturaventa : facturaventaCollectionOld) {
                if (!facturaventaCollectionNew.contains(facturaventaCollectionOldFacturaventa)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Facturaventa " + facturaventaCollectionOldFacturaventa + " since its pagoidFk field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (tarjetacreditoidFkNew != null) {
                tarjetacreditoidFkNew = em.getReference(tarjetacreditoidFkNew.getClass(), tarjetacreditoidFkNew.getTarjetacreditoventaid());
                pagoventa.setTarjetacreditoidFk(tarjetacreditoidFkNew);
            }
            Collection<Facturaventa> attachedFacturaventaCollectionNew = new ArrayList<Facturaventa>();
            for (Facturaventa facturaventaCollectionNewFacturaventaToAttach : facturaventaCollectionNew) {
                facturaventaCollectionNewFacturaventaToAttach = em.getReference(facturaventaCollectionNewFacturaventaToAttach.getClass(), facturaventaCollectionNewFacturaventaToAttach.getFacturaventaid());
                attachedFacturaventaCollectionNew.add(facturaventaCollectionNewFacturaventaToAttach);
            }
            facturaventaCollectionNew = attachedFacturaventaCollectionNew;
            pagoventa.setFacturaventaCollection(facturaventaCollectionNew);
            pagoventa = em.merge(pagoventa);
            if (tarjetacreditoidFkOld != null && !tarjetacreditoidFkOld.equals(tarjetacreditoidFkNew)) {
                tarjetacreditoidFkOld.getPagoventaCollection().remove(pagoventa);
                tarjetacreditoidFkOld = em.merge(tarjetacreditoidFkOld);
            }
            if (tarjetacreditoidFkNew != null && !tarjetacreditoidFkNew.equals(tarjetacreditoidFkOld)) {
                tarjetacreditoidFkNew.getPagoventaCollection().add(pagoventa);
                tarjetacreditoidFkNew = em.merge(tarjetacreditoidFkNew);
            }
            for (Facturaventa facturaventaCollectionNewFacturaventa : facturaventaCollectionNew) {
                if (!facturaventaCollectionOld.contains(facturaventaCollectionNewFacturaventa)) {
                    Pagoventa oldPagoidFkOfFacturaventaCollectionNewFacturaventa = facturaventaCollectionNewFacturaventa.getPagoidFk();
                    facturaventaCollectionNewFacturaventa.setPagoidFk(pagoventa);
                    facturaventaCollectionNewFacturaventa = em.merge(facturaventaCollectionNewFacturaventa);
                    if (oldPagoidFkOfFacturaventaCollectionNewFacturaventa != null && !oldPagoidFkOfFacturaventaCollectionNewFacturaventa.equals(pagoventa)) {
                        oldPagoidFkOfFacturaventaCollectionNewFacturaventa.getFacturaventaCollection().remove(facturaventaCollectionNewFacturaventa);
                        oldPagoidFkOfFacturaventaCollectionNewFacturaventa = em.merge(oldPagoidFkOfFacturaventaCollectionNewFacturaventa);
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
                Integer id = pagoventa.getPagoventaid();
                if (findPagoventa(id) == null) {
                    throw new NonexistentEntityException("The pagoventa with id " + id + " no longer exists.");
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
            Pagoventa pagoventa;
            try {
                pagoventa = em.getReference(Pagoventa.class, id);
                pagoventa.getPagoventaid();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The pagoventa with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Facturaventa> facturaventaCollectionOrphanCheck = pagoventa.getFacturaventaCollection();
            for (Facturaventa facturaventaCollectionOrphanCheckFacturaventa : facturaventaCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Pagoventa (" + pagoventa + ") cannot be destroyed since the Facturaventa " + facturaventaCollectionOrphanCheckFacturaventa + " in its facturaventaCollection field has a non-nullable pagoidFk field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Tarjetacreditoventa tarjetacreditoidFk = pagoventa.getTarjetacreditoidFk();
            if (tarjetacreditoidFk != null) {
                tarjetacreditoidFk.getPagoventaCollection().remove(pagoventa);
                tarjetacreditoidFk = em.merge(tarjetacreditoidFk);
            }
            em.remove(pagoventa);
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

    public List<Pagoventa> findPagoventaEntities() {
        return findPagoventaEntities(true, -1, -1);
    }

    public List<Pagoventa> findPagoventaEntities(int maxResults, int firstResult) {
        return findPagoventaEntities(false, maxResults, firstResult);
    }

    private List<Pagoventa> findPagoventaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Pagoventa.class));
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

    public Pagoventa findPagoventa(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Pagoventa.class, id);
        } finally {
            em.close();
        }
    }

    public int getPagoventaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Pagoventa> rt = cq.from(Pagoventa.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
