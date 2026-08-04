package factories;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class DbFactory {

    private static final String DB_PATH = "objectdb:lab.odb";
    private static EntityManagerFactory factory;

    private DbFactory() {}

    public static EntityManagerFactory getFactory() {
        if (factory == null || !factory.isOpen()) {
            factory = Persistence.createEntityManagerFactory(DB_PATH);
        }
        return factory;
    }

    public static EntityManager createManager() {
        return getFactory().createEntityManager();
    }

    public static void close() {
        if (factory != null && factory.isOpen()) {
            factory.close();
        }
    }
}
