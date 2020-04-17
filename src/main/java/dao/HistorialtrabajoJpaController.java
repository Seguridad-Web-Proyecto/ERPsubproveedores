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
import entidades.Cargo;
import entidades.Empleado;
import entidades.Historialtrabajo;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author jaker
 */
public class HistorialtrabajoJpaController implements Serializable {

    public HistorialtrabajoJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Historialtrabajo historialtrabajo) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Cargo cargoidFk = historialtrabajo.getCargoidFk();
            if (cargoidFk != null) {
                cargoidFk = em.getReference(cargoidFk.getClass(), cargoidFk.getCargoid());
                historialtrabajo.setCargoidFk(cargoidFk);
            }
            Empleado empleadoidFk = historialtrabajo.getEmpleadoidFk();
            if (empleadoidFk != null) {
                empleadoidFk = em.getReference(empleadoidFk.getClass(), empleadoidFk.getEmpleadoid());
                historialtrabajo.setEmpleadoidFk(empleadoidFk);
            }
            em.persist(historialtrabajo);
            if (cargoidFk != null) {
                cargoidFk.getHistorialtrabajoCollection().add(historialtrabajo);
                cargoidFk = em.merge(cargoidFk);
            }
            if (empleadoidFk != null) {
                empleadoidFk.getHistorialtrabajoCollection().add(historialtrabajo);
                empleadoidFk = em.merge(empleadoidFk);
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

    public void edit(Historialtrabajo historialtrabajo) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Historialtrabajo persistentHistorialtrabajo = em.find(Historialtrabajo.class, historialtrabajo.getHistorialid());
            Cargo cargoidFkOld = persistentHistorialtrabajo.getCargoidFk();
            Cargo cargoidFkNew = historialtrabajo.getCargoidFk();
            Empleado empleadoidFkOld = persistentHistorialtrabajo.getEmpleadoidFk();
            Empleado empleadoidFkNew = historialtrabajo.getEmpleadoidFk();
            if (cargoidFkNew != null) {
                cargoidFkNew = em.getReference(cargoidFkNew.getClass(), cargoidFkNew.getCargoid());
                historialtrabajo.setCargoidFk(cargoidFkNew);
            }
            if (empleadoidFkNew != null) {
                empleadoidFkNew = em.getReference(empleadoidFkNew.getClass(), empleadoidFkNew.getEmpleadoid());
                historialtrabajo.setEmpleadoidFk(empleadoidFkNew);
            }
            historialtrabajo = em.merge(historialtrabajo);
            if (cargoidFkOld != null && !cargoidFkOld.equals(cargoidFkNew)) {
                cargoidFkOld.getHistorialtrabajoCollection().remove(historialtrabajo);
                cargoidFkOld = em.merge(cargoidFkOld);
            }
            if (cargoidFkNew != null && !cargoidFkNew.equals(cargoidFkOld)) {
                cargoidFkNew.getHistorialtrabajoCollection().add(historialtrabajo);
                cargoidFkNew = em.merge(cargoidFkNew);
            }
            if (empleadoidFkOld != null && !empleadoidFkOld.equals(empleadoidFkNew)) {
                empleadoidFkOld.getHistorialtrabajoCollection().remove(historialtrabajo);
                empleadoidFkOld = em.merge(empleadoidFkOld);
            }
            if (empleadoidFkNew != null && !empleadoidFkNew.equals(empleadoidFkOld)) {
                empleadoidFkNew.getHistorialtrabajoCollection().add(historialtrabajo);
                empleadoidFkNew = em.merge(empleadoidFkNew);
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
                Integer id = historialtrabajo.getHistorialid();
                if (findHistorialtrabajo(id) == null) {
                    throw new NonexistentEntityException("The historialtrabajo with id " + id + " no longer exists.");
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
            Historialtrabajo historialtrabajo;
            try {
                historialtrabajo = em.getReference(Historialtrabajo.class, id);
                historialtrabajo.getHistorialid();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The historialtrabajo with id " + id + " no longer exists.", enfe);
            }
            Cargo cargoidFk = historialtrabajo.getCargoidFk();
            if (cargoidFk != null) {
                cargoidFk.getHistorialtrabajoCollection().remove(historialtrabajo);
                cargoidFk = em.merge(cargoidFk);
            }
            Empleado empleadoidFk = historialtrabajo.getEmpleadoidFk();
            if (empleadoidFk != null) {
                empleadoidFk.getHistorialtrabajoCollection().remove(historialtrabajo);
                empleadoidFk = em.merge(empleadoidFk);
            }
            em.remove(historialtrabajo);
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

    public List<Historialtrabajo> findHistorialtrabajoEntities() {
        return findHistorialtrabajoEntities(true, -1, -1);
    }

    public List<Historialtrabajo> findHistorialtrabajoEntities(int maxResults, int firstResult) {
        return findHistorialtrabajoEntities(false, maxResults, firstResult);
    }

    private List<Historialtrabajo> findHistorialtrabajoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Historialtrabajo.class));
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

    public Historialtrabajo findHistorialtrabajo(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Historialtrabajo.class, id);
        } finally {
            em.close();
        }
    }

    public int getHistorialtrabajoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Historialtrabajo> rt = cq.from(Historialtrabajo.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
