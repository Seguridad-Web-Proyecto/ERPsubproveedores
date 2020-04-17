/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import dao.exceptions.IllegalOrphanException;
import dao.exceptions.NonexistentEntityException;
import dao.exceptions.RollbackFailureException;
import entidades.Facturaventa;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entidades.Pagoventa;
import entidades.Ordencompra;
import java.util.ArrayList;
import java.util.Collection;
import entidades.Ordenventa;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author jaker
 */
public class FacturaventaJpaController implements Serializable {

    public FacturaventaJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Facturaventa facturaventa) throws RollbackFailureException, Exception {
        if (facturaventa.getOrdencompraCollection() == null) {
            facturaventa.setOrdencompraCollection(new ArrayList<Ordencompra>());
        }
        if (facturaventa.getOrdenventaCollection() == null) {
            facturaventa.setOrdenventaCollection(new ArrayList<Ordenventa>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Pagoventa pagoidFk = facturaventa.getPagoidFk();
            if (pagoidFk != null) {
                pagoidFk = em.getReference(pagoidFk.getClass(), pagoidFk.getPagoventaid());
                facturaventa.setPagoidFk(pagoidFk);
            }
            Collection<Ordencompra> attachedOrdencompraCollection = new ArrayList<Ordencompra>();
            for (Ordencompra ordencompraCollectionOrdencompraToAttach : facturaventa.getOrdencompraCollection()) {
                ordencompraCollectionOrdencompraToAttach = em.getReference(ordencompraCollectionOrdencompraToAttach.getClass(), ordencompraCollectionOrdencompraToAttach.getOrdenventaid());
                attachedOrdencompraCollection.add(ordencompraCollectionOrdencompraToAttach);
            }
            facturaventa.setOrdencompraCollection(attachedOrdencompraCollection);
            Collection<Ordenventa> attachedOrdenventaCollection = new ArrayList<Ordenventa>();
            for (Ordenventa ordenventaCollectionOrdenventaToAttach : facturaventa.getOrdenventaCollection()) {
                ordenventaCollectionOrdenventaToAttach = em.getReference(ordenventaCollectionOrdenventaToAttach.getClass(), ordenventaCollectionOrdenventaToAttach.getOrdenventaid());
                attachedOrdenventaCollection.add(ordenventaCollectionOrdenventaToAttach);
            }
            facturaventa.setOrdenventaCollection(attachedOrdenventaCollection);
            em.persist(facturaventa);
            if (pagoidFk != null) {
                pagoidFk.getFacturaventaCollection().add(facturaventa);
                pagoidFk = em.merge(pagoidFk);
            }
            for (Ordencompra ordencompraCollectionOrdencompra : facturaventa.getOrdencompraCollection()) {
                Facturaventa oldFacturaidFkOfOrdencompraCollectionOrdencompra = ordencompraCollectionOrdencompra.getFacturaidFk();
                ordencompraCollectionOrdencompra.setFacturaidFk(facturaventa);
                ordencompraCollectionOrdencompra = em.merge(ordencompraCollectionOrdencompra);
                if (oldFacturaidFkOfOrdencompraCollectionOrdencompra != null) {
                    oldFacturaidFkOfOrdencompraCollectionOrdencompra.getOrdencompraCollection().remove(ordencompraCollectionOrdencompra);
                    oldFacturaidFkOfOrdencompraCollectionOrdencompra = em.merge(oldFacturaidFkOfOrdencompraCollectionOrdencompra);
                }
            }
            for (Ordenventa ordenventaCollectionOrdenventa : facturaventa.getOrdenventaCollection()) {
                Facturaventa oldFacturaidFkOfOrdenventaCollectionOrdenventa = ordenventaCollectionOrdenventa.getFacturaidFk();
                ordenventaCollectionOrdenventa.setFacturaidFk(facturaventa);
                ordenventaCollectionOrdenventa = em.merge(ordenventaCollectionOrdenventa);
                if (oldFacturaidFkOfOrdenventaCollectionOrdenventa != null) {
                    oldFacturaidFkOfOrdenventaCollectionOrdenventa.getOrdenventaCollection().remove(ordenventaCollectionOrdenventa);
                    oldFacturaidFkOfOrdenventaCollectionOrdenventa = em.merge(oldFacturaidFkOfOrdenventaCollectionOrdenventa);
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

    public void edit(Facturaventa facturaventa) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Facturaventa persistentFacturaventa = em.find(Facturaventa.class, facturaventa.getFacturaventaid());
            Pagoventa pagoidFkOld = persistentFacturaventa.getPagoidFk();
            Pagoventa pagoidFkNew = facturaventa.getPagoidFk();
            Collection<Ordencompra> ordencompraCollectionOld = persistentFacturaventa.getOrdencompraCollection();
            Collection<Ordencompra> ordencompraCollectionNew = facturaventa.getOrdencompraCollection();
            Collection<Ordenventa> ordenventaCollectionOld = persistentFacturaventa.getOrdenventaCollection();
            Collection<Ordenventa> ordenventaCollectionNew = facturaventa.getOrdenventaCollection();
            List<String> illegalOrphanMessages = null;
            for (Ordencompra ordencompraCollectionOldOrdencompra : ordencompraCollectionOld) {
                if (!ordencompraCollectionNew.contains(ordencompraCollectionOldOrdencompra)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Ordencompra " + ordencompraCollectionOldOrdencompra + " since its facturaidFk field is not nullable.");
                }
            }
            for (Ordenventa ordenventaCollectionOldOrdenventa : ordenventaCollectionOld) {
                if (!ordenventaCollectionNew.contains(ordenventaCollectionOldOrdenventa)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Ordenventa " + ordenventaCollectionOldOrdenventa + " since its facturaidFk field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (pagoidFkNew != null) {
                pagoidFkNew = em.getReference(pagoidFkNew.getClass(), pagoidFkNew.getPagoventaid());
                facturaventa.setPagoidFk(pagoidFkNew);
            }
            Collection<Ordencompra> attachedOrdencompraCollectionNew = new ArrayList<Ordencompra>();
            for (Ordencompra ordencompraCollectionNewOrdencompraToAttach : ordencompraCollectionNew) {
                ordencompraCollectionNewOrdencompraToAttach = em.getReference(ordencompraCollectionNewOrdencompraToAttach.getClass(), ordencompraCollectionNewOrdencompraToAttach.getOrdenventaid());
                attachedOrdencompraCollectionNew.add(ordencompraCollectionNewOrdencompraToAttach);
            }
            ordencompraCollectionNew = attachedOrdencompraCollectionNew;
            facturaventa.setOrdencompraCollection(ordencompraCollectionNew);
            Collection<Ordenventa> attachedOrdenventaCollectionNew = new ArrayList<Ordenventa>();
            for (Ordenventa ordenventaCollectionNewOrdenventaToAttach : ordenventaCollectionNew) {
                ordenventaCollectionNewOrdenventaToAttach = em.getReference(ordenventaCollectionNewOrdenventaToAttach.getClass(), ordenventaCollectionNewOrdenventaToAttach.getOrdenventaid());
                attachedOrdenventaCollectionNew.add(ordenventaCollectionNewOrdenventaToAttach);
            }
            ordenventaCollectionNew = attachedOrdenventaCollectionNew;
            facturaventa.setOrdenventaCollection(ordenventaCollectionNew);
            facturaventa = em.merge(facturaventa);
            if (pagoidFkOld != null && !pagoidFkOld.equals(pagoidFkNew)) {
                pagoidFkOld.getFacturaventaCollection().remove(facturaventa);
                pagoidFkOld = em.merge(pagoidFkOld);
            }
            if (pagoidFkNew != null && !pagoidFkNew.equals(pagoidFkOld)) {
                pagoidFkNew.getFacturaventaCollection().add(facturaventa);
                pagoidFkNew = em.merge(pagoidFkNew);
            }
            for (Ordencompra ordencompraCollectionNewOrdencompra : ordencompraCollectionNew) {
                if (!ordencompraCollectionOld.contains(ordencompraCollectionNewOrdencompra)) {
                    Facturaventa oldFacturaidFkOfOrdencompraCollectionNewOrdencompra = ordencompraCollectionNewOrdencompra.getFacturaidFk();
                    ordencompraCollectionNewOrdencompra.setFacturaidFk(facturaventa);
                    ordencompraCollectionNewOrdencompra = em.merge(ordencompraCollectionNewOrdencompra);
                    if (oldFacturaidFkOfOrdencompraCollectionNewOrdencompra != null && !oldFacturaidFkOfOrdencompraCollectionNewOrdencompra.equals(facturaventa)) {
                        oldFacturaidFkOfOrdencompraCollectionNewOrdencompra.getOrdencompraCollection().remove(ordencompraCollectionNewOrdencompra);
                        oldFacturaidFkOfOrdencompraCollectionNewOrdencompra = em.merge(oldFacturaidFkOfOrdencompraCollectionNewOrdencompra);
                    }
                }
            }
            for (Ordenventa ordenventaCollectionNewOrdenventa : ordenventaCollectionNew) {
                if (!ordenventaCollectionOld.contains(ordenventaCollectionNewOrdenventa)) {
                    Facturaventa oldFacturaidFkOfOrdenventaCollectionNewOrdenventa = ordenventaCollectionNewOrdenventa.getFacturaidFk();
                    ordenventaCollectionNewOrdenventa.setFacturaidFk(facturaventa);
                    ordenventaCollectionNewOrdenventa = em.merge(ordenventaCollectionNewOrdenventa);
                    if (oldFacturaidFkOfOrdenventaCollectionNewOrdenventa != null && !oldFacturaidFkOfOrdenventaCollectionNewOrdenventa.equals(facturaventa)) {
                        oldFacturaidFkOfOrdenventaCollectionNewOrdenventa.getOrdenventaCollection().remove(ordenventaCollectionNewOrdenventa);
                        oldFacturaidFkOfOrdenventaCollectionNewOrdenventa = em.merge(oldFacturaidFkOfOrdenventaCollectionNewOrdenventa);
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
                Integer id = facturaventa.getFacturaventaid();
                if (findFacturaventa(id) == null) {
                    throw new NonexistentEntityException("The facturaventa with id " + id + " no longer exists.");
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
            Facturaventa facturaventa;
            try {
                facturaventa = em.getReference(Facturaventa.class, id);
                facturaventa.getFacturaventaid();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The facturaventa with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Ordencompra> ordencompraCollectionOrphanCheck = facturaventa.getOrdencompraCollection();
            for (Ordencompra ordencompraCollectionOrphanCheckOrdencompra : ordencompraCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Facturaventa (" + facturaventa + ") cannot be destroyed since the Ordencompra " + ordencompraCollectionOrphanCheckOrdencompra + " in its ordencompraCollection field has a non-nullable facturaidFk field.");
            }
            Collection<Ordenventa> ordenventaCollectionOrphanCheck = facturaventa.getOrdenventaCollection();
            for (Ordenventa ordenventaCollectionOrphanCheckOrdenventa : ordenventaCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Facturaventa (" + facturaventa + ") cannot be destroyed since the Ordenventa " + ordenventaCollectionOrphanCheckOrdenventa + " in its ordenventaCollection field has a non-nullable facturaidFk field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Pagoventa pagoidFk = facturaventa.getPagoidFk();
            if (pagoidFk != null) {
                pagoidFk.getFacturaventaCollection().remove(facturaventa);
                pagoidFk = em.merge(pagoidFk);
            }
            em.remove(facturaventa);
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

    public List<Facturaventa> findFacturaventaEntities() {
        return findFacturaventaEntities(true, -1, -1);
    }

    public List<Facturaventa> findFacturaventaEntities(int maxResults, int firstResult) {
        return findFacturaventaEntities(false, maxResults, firstResult);
    }

    private List<Facturaventa> findFacturaventaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Facturaventa.class));
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

    public Facturaventa findFacturaventa(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Facturaventa.class, id);
        } finally {
            em.close();
        }
    }

    public int getFacturaventaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Facturaventa> rt = cq.from(Facturaventa.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
