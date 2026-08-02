import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
class Person implements Serializable {
    @Id @GeneratedValue
    private long id;
    private String name;

    public Person() {}

    public Person(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Person[id=" + id + ", name='" + name + "']";
    }
}

public class Main {
    public static void main(String[] args) {
        // Open/Create embedded database file "test.odb"
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("test.odb");
        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();
        Person p = new Person("Alice");
        em.persist(p);
        em.getTransaction().commit();

        // 2. Query and display stored objects
        TypedQuery<Person> query = em.createQuery("SELECT p FROM Person p", Person.class);
        List<Person> results = query.getResultList();

        System.out.println("--- ObjectDB Verification ---");
        for (Person person : results) {
            System.out.println(person);
        }
        em.close();
        emf.close();
    }
}