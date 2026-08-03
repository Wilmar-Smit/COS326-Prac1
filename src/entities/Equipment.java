package entities;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Equipment implements Serializable {

    public static final String OUT_OF_SERVICE = "Out of Service";
    public static final String PENDING = "Pending";
    public static final String APPROVED = "Approved";
    public static final String REJECTED = "Rejected";

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String category;
    private String purchaseDate;
    private String replaceCost;
    private String status;

    public Equipment() {}

    public Equipment(
        String name,
        String category,
        String purchaseDate,
        String replaceCost,
        String status
    ) {
        this.name = name;
        this.category = category;
        this.purchaseDate = purchaseDate;
        this.replaceCost = replaceCost;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getReplaceCost() {
        return replaceCost;
    }

    public void setReplaceCost(String replaceCost) {
        this.replaceCost = replaceCost;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (
            status == OUT_OF_SERVICE ||
            status == APPROVED ||
            status == REJECTED ||
            status == PENDING
        ) this.status = status;
    }
}
