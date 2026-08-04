package factories;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

public abstract class BaseManager<T> {

    private final Class<T> entityClass;

    public BaseManager(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public void Save(T entity) {
        EntityManager man = DbFactory.createManager();
        EntityTransaction tx = man.getTransaction();

        try {
            tx.begin();
            man.persist(entity);
            tx.commit();
        } catch (Exception ex) {
            if (tx.isActive()) tx.rollback();
            throw ex;
        } finally {
            man.close();
        }
    }

    public void delete(Object id) {
        EntityManager man = DbFactory.createManager();
        EntityTransaction tx = man.getTransaction();
        try {
            tx.begin();
            T entity = man.find(entityClass, id);
            if (entity != null) {
                man.remove(entity);
            }
            tx.commit();
        } catch (Exception ex) {
            if (tx.isActive()) tx.rollback();
            throw ex;
        } finally {
            man.close();
        }
    }

    public List<T> findAll() {
        EntityManager em = DbFactory.createManager();
        try {
            String jpql = "SELECT e FROM " + entityClass.getSimpleName() + " e";
            return em.createQuery(jpql, entityClass).getResultList();
        } finally {
            em.close();
        }
    }

    public T update(T entity) {
        EntityManager man = DbFactory.createManager();
        EntityTransaction tx = man.getTransaction();
        try {
            tx.begin();
            T updatedEntity = man.merge(entity);
            tx.commit();
            return updatedEntity;
        } catch (Exception ex) {
            if (tx.isActive()) tx.rollback();
            throw ex;
        } finally {
            man.close();
        }
    }
}
