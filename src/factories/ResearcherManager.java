package factories;

import entities.Researcher;
import java.util.List;
import javax.persistence.EntityManager;

/**
 * researcherManager
 */
public class ResearcherManager extends BaseManager<Researcher> {

    // has the crud operations
    public ResearcherManager() {
        super(Researcher.class);
    }

    public Researcher SearchResearcher(Long id) {
        EntityManager man = DbFactory.createManager();
        try {
            return man.find(Researcher.class, id);
        } finally {
            man.close();
        }
    }
}

// covered functions
// Register researcher basemanager.save
// Search researcher ->
