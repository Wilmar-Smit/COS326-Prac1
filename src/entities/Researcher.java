package entities;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Researcher implements Serializable {

    @Id
    @GeneratedValue
    private Long researchId;

    private String fullName;
    private String department;
    private String email;

    public Researcher() {}

    public Researcher(String fullName, String department, String email) {
        this.fullName = fullName;
        this.department = department;
        this.email = email;
    }

    public Long getResearchId() {
        return researchId;
    }

    public void setResearchId(Long researchId) {
        this.researchId = researchId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
