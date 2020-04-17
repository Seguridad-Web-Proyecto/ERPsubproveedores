/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import dao.exceptions.IllegalOrphanException;
import dao.exceptions.NonexistentEntityException;
import dao.exceptions.RollbackFailureException;
import entidades.Ganancia;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entidades.Producto;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author jaker
 */
public class GananciaJpaController implements Serializable {

    public GananciaJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Ganancia ganancia) throws IllegalOrphanException, RollbackFailureException, Exception {
        List<String> illegalOrphanMessages = null;
        Producto productoidFkOrphanCheck = ganancia.getProductoidFk();
        if (productoidFkOrphanCheck != null) {
            Ganancia oldGananciaOfProductoidFk = productoidFkOrphanCheck.getGanancia();
            if (oldGananciaOfProductoidFk != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("The Producto " + productoidFkOrphanCheck + " already has an item of type Ganancia whose productoidFk column cannot be null. Please make another selection for the productoidFk field.");
            }
        }
        if (illegalOrphanMessages != null) {
            throw new IllegalOrphanException(illegalOrphanMessages);
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Producto productoidFk = ganancia.getProductoidFk();
            if (productoidFk != null) {
                productoidFk = em.getReference(productoidFk.getClass(), productoidFk.getProductoid());
                ganancia.setProductoidFk(productoidFk);
            }
            em.persist(ganancia);
            if (productoidFk != null) {
                productoidFk.setGanancia(ganancia);
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

    public void edit(Ganancia ganancia) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Ganancia persistentGanancia = em.find(Ganancia.class, ganancia.getGananciaid());
            Producto productoidFkOld = persistentGanancia.getProductoidFk();
            Producto productoidFkNew = ganancia.getProductoidFk();
            List<String> illegalOrphanMessages = null;
            if (productoidFkNew != null && !productoidFkNew.equals(productoidFkOld)) {
                Ganancia oldGananciaOfProductoidFk = productoidFkNew.getGanancia();
                if (oldGananciaOfProductoidFk != null) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("The Producto " + productoidFkNew + " already has an item of type Ganancia whose productoidFk column cannot be null. Please make another selection for the productoidFk field.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (productoidFkNew != null) {
                productoidFkNew = em.getReference(productoidFkNew.getClass(), productoidFkNew.getProductoid());
                ganancia.setProductoidFk(productoidFkNew);
            }
            ganancia = em.merge(ganancia);
            if (productoidFkOld != null && !productoidFkOld.equals(productoidFkNew)) {
                productoidFkOld.setGanancia(null);
                productoidFkOld = em.merge(productoidFkOld);
            }
            if (productoidFkNew != null && !productoidFkNew.equals(productoidFkOld)) {
                productoidFkNew.setGanancia(ganancia);
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
                Integer id = ganancia.getGananciaid();
                if (findGanancia(id) == null) {
                    throw new NonexistentEntityException("The ganancia with id " + id + " no longer exists.");
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
            Ganancia ganancia;
            try {
                ganancia = em.getReference(Ganancia.class, id);
                ganancia.getGananciaid();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The ganancia with id " + id + " no longer exists.", enfe);
            }
            Producto productoidFk = ganancia.getProductoidFk();
            if (productoidFk != null) {
                productoidFk.setGanancia(null);
                productoidFk = em.merge(productoidFk);
            }
            em.remove(ganancia);
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

    public List<Ganancia> findGananciaEntities() {
        return findGananciaEntities(true, -1, -1);
    }

    public List<Ganancia> findGananciaEntities(int maxResults, int firstResult) {
        return findGananciaEntities(false, maxResults, firstResult);
    }

    private List<Ganancia> findGananciaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Ganancia.class));
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

    public Ganancia findGanancia(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Ganancia.class, id);
        } finally {
            em.close();
        }
    }

    public int getGananciaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Ganancia> rt = cq.from(Ganancia.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
