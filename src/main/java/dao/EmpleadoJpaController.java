/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import dao.exceptions.IllegalOrphanException;
import dao.exceptions.NonexistentEntityException;
import dao.exceptions.RollbackFailureException;
import entidades.Empleado;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entidades.Usuario;
import entidades.Historialtrabajo;
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
public class EmpleadoJpaController implements Serializable {

    public EmpleadoJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Empleado empleado) throws RollbackFailureException, Exception {
        if (empleado.getHistorialtrabajoCollection() == null) {
            empleado.setHistorialtrabajoCollection(new ArrayList<Historialtrabajo>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Usuario usuarioidFk = empleado.getUsuarioidFk();
            if (usuarioidFk != null) {
                usuarioidFk = em.getReference(usuarioidFk.getClass(), usuarioidFk.getUsuarioid());
                empleado.setUsuarioidFk(usuarioidFk);
            }
            Collection<Historialtrabajo> attachedHistorialtrabajoCollection = new ArrayList<Historialtrabajo>();
            for (Historialtrabajo historialtrabajoCollectionHistorialtrabajoToAttach : empleado.getHistorialtrabajoCollection()) {
                historialtrabajoCollectionHistorialtrabajoToAttach = em.getReference(historialtrabajoCollectionHistorialtrabajoToAttach.getClass(), historialtrabajoCollectionHistorialtrabajoToAttach.getHistorialid());
                attachedHistorialtrabajoCollection.add(historialtrabajoCollectionHistorialtrabajoToAttach);
            }
            empleado.setHistorialtrabajoCollection(attachedHistorialtrabajoCollection);
            em.persist(empleado);
            if (usuarioidFk != null) {
                usuarioidFk.getEmpleadoCollection().add(empleado);
                usuarioidFk = em.merge(usuarioidFk);
            }
            for (Historialtrabajo historialtrabajoCollectionHistorialtrabajo : empleado.getHistorialtrabajoCollection()) {
                Empleado oldEmpleadoidFkOfHistorialtrabajoCollectionHistorialtrabajo = historialtrabajoCollectionHistorialtrabajo.getEmpleadoidFk();
                historialtrabajoCollectionHistorialtrabajo.setEmpleadoidFk(empleado);
                historialtrabajoCollectionHistorialtrabajo = em.merge(historialtrabajoCollectionHistorialtrabajo);
                if (oldEmpleadoidFkOfHistorialtrabajoCollectionHistorialtrabajo != null) {
                    oldEmpleadoidFkOfHistorialtrabajoCollectionHistorialtrabajo.getHistorialtrabajoCollection().remove(historialtrabajoCollectionHistorialtrabajo);
                    oldEmpleadoidFkOfHistorialtrabajoCollectionHistorialtrabajo = em.merge(oldEmpleadoidFkOfHistorialtrabajoCollectionHistorialtrabajo);
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

    public void edit(Empleado empleado) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Empleado persistentEmpleado = em.find(Empleado.class, empleado.getEmpleadoid());
            Usuario usuarioidFkOld = persistentEmpleado.getUsuarioidFk();
            Usuario usuarioidFkNew = empleado.getUsuarioidFk();
            Collection<Historialtrabajo> historialtrabajoCollectionOld = persistentEmpleado.getHistorialtrabajoCollection();
            Collection<Historialtrabajo> historialtrabajoCollectionNew = empleado.getHistorialtrabajoCollection();
            List<String> illegalOrphanMessages = null;
            for (Historialtrabajo historialtrabajoCollectionOldHistorialtrabajo : historialtrabajoCollectionOld) {
                if (!historialtrabajoCollectionNew.contains(historialtrabajoCollectionOldHistorialtrabajo)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Historialtrabajo " + historialtrabajoCollectionOldHistorialtrabajo + " since its empleadoidFk field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (usuarioidFkNew != null) {
                usuarioidFkNew = em.getReference(usuarioidFkNew.getClass(), usuarioidFkNew.getUsuarioid());
                empleado.setUsuarioidFk(usuarioidFkNew);
            }
            Collection<Historialtrabajo> attachedHistorialtrabajoCollectionNew = new ArrayList<Historialtrabajo>();
            for (Historialtrabajo historialtrabajoCollectionNewHistorialtrabajoToAttach : historialtrabajoCollectionNew) {
                historialtrabajoCollectionNewHistorialtrabajoToAttach = em.getReference(historialtrabajoCollectionNewHistorialtrabajoToAttach.getClass(), historialtrabajoCollectionNewHistorialtrabajoToAttach.getHistorialid());
                attachedHistorialtrabajoCollectionNew.add(historialtrabajoCollectionNewHistorialtrabajoToAttach);
            }
            historialtrabajoCollectionNew = attachedHistorialtrabajoCollectionNew;
            empleado.setHistorialtrabajoCollection(historialtrabajoCollectionNew);
            empleado = em.merge(empleado);
            if (usuarioidFkOld != null && !usuarioidFkOld.equals(usuarioidFkNew)) {
                usuarioidFkOld.getEmpleadoCollection().remove(empleado);
                usuarioidFkOld = em.merge(usuarioidFkOld);
            }
            if (usuarioidFkNew != null && !usuarioidFkNew.equals(usuarioidFkOld)) {
                usuarioidFkNew.getEmpleadoCollection().add(empleado);
                usuarioidFkNew = em.merge(usuarioidFkNew);
            }
            for (Historialtrabajo historialtrabajoCollectionNewHistorialtrabajo : historialtrabajoCollectionNew) {
                if (!historialtrabajoCollectionOld.contains(historialtrabajoCollectionNewHistorialtrabajo)) {
                    Empleado oldEmpleadoidFkOfHistorialtrabajoCollectionNewHistorialtrabajo = historialtrabajoCollectionNewHistorialtrabajo.getEmpleadoidFk();
                    historialtrabajoCollectionNewHistorialtrabajo.setEmpleadoidFk(empleado);
                    historialtrabajoCollectionNewHistorialtrabajo = em.merge(historialtrabajoCollectionNewHistorialtrabajo);
                    if (oldEmpleadoidFkOfHistorialtrabajoCollectionNewHistorialtrabajo != null && !oldEmpleadoidFkOfHistorialtrabajoCollectionNewHistorialtrabajo.equals(empleado)) {
                        oldEmpleadoidFkOfHistorialtrabajoCollectionNewHistorialtrabajo.getHistorialtrabajoCollection().remove(historialtrabajoCollectionNewHistorialtrabajo);
                        oldEmpleadoidFkOfHistorialtrabajoCollectionNewHistorialtrabajo = em.merge(oldEmpleadoidFkOfHistorialtrabajoCollectionNewHistorialtrabajo);
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
                Integer id = empleado.getEmpleadoid();
                if (findEmpleado(id) == null) {
                    throw new NonexistentEntityException("The empleado with id " + id + " no longer exists.");
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
            Empleado empleado;
            try {
                empleado = em.getReference(Empleado.class, id);
                empleado.getEmpleadoid();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The empleado with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Historialtrabajo> historialtrabajoCollectionOrphanCheck = empleado.getHistorialtrabajoCollection();
            for (Historialtrabajo historialtrabajoCollectionOrphanCheckHistorialtrabajo : historialtrabajoCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Empleado (" + empleado + ") cannot be destroyed since the Historialtrabajo " + historialtrabajoCollectionOrphanCheckHistorialtrabajo + " in its historialtrabajoCollection field has a non-nullable empleadoidFk field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Usuario usuarioidFk = empleado.getUsuarioidFk();
            if (usuarioidFk != null) {
                usuarioidFk.getEmpleadoCollection().remove(empleado);
                usuarioidFk = em.merge(usuarioidFk);
            }
            em.remove(empleado);
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

    public List<Empleado> findEmpleadoEntities() {
        return findEmpleadoEntities(true, -1, -1);
    }

    public List<Empleado> findEmpleadoEntities(int maxResults, int firstResult) {
        return findEmpleadoEntities(false, maxResults, firstResult);
    }

    private List<Empleado> findEmpleadoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Empleado.class));
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

    public Empleado findEmpleado(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Empleado.class, id);
        } finally {
            em.close();
        }
    }

    public int getEmpleadoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Empleado> rt = cq.from(Empleado.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
