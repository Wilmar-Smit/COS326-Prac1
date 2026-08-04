package factories;

import entities.Equipment;
import java.util.List;
import javax.persistence.EntityManager;

/**
 * EquipmentManager
 */
public class EquipmentManager extends BaseManager<Equipment> {

    EquipmentManager() {
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
}
