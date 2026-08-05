package factories;

import entities.Equipment;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

/**
 * EquipmentManager
 */
public class EquipmentManager extends BaseManager<Equipment> {

    public EquipmentManager() {
        super(Equipment.class);
    }

    public Equipment searchEquipment(Long id) {
        EntityManager man = DbFactory.createManager();
        try {
            return man.find(Equipment.class, id);
        } finally {
            man.close();
        }
    }

    public double CostSummary() {
        List<Equipment> allEq = this.findAll();

        double cost = 0;
        for (Equipment eq : allEq) {
            cost += eq.getReplaceCost();
        }
        return cost;
    }

    public List<Equipment> findAllEQ(boolean includeOutOfService) {
        EntityManager man = DbFactory.createManager();
        try {
            String jpql =
                "SELECT e FROM " + Equipment.class.getSimpleName() + " e";

            if (!includeOutOfService) {
                jpql += " WHERE e.status <> :outOfServiceStatus";
            }

            TypedQuery<Equipment> query = man.createQuery(
                jpql,
                Equipment.class
            );

            if (!includeOutOfService) {
                query.setParameter(
                    "outOfServiceStatus",
                    Equipment.OUT_OF_SERVICE
                );
            }

            return query.getResultList();
        } finally {
            man.close();
        }
    }
}
