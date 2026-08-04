package entities;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

// a booking is specifically for equipment

@Entity
public class Booking implements Serializable {

    public static final String ACTIVE = "Active";
    public static final String FINISHED = "Finished";

    @Id
    @GeneratedValue
    private Long id;

    private String date;
    private String startTime;
    private String endTime;
    private String purpose;
    private String status;

    @ManyToOne(optional = false)
    private Equipment bookedEQ;

    @ManyToOne(optional = false)
    private Researcher bookedBy;

    public Booking() {}

    public Booking(
        String date,
        String startTime,
        String endTime,
        String purpose,
        String status,
        Equipment bookedEQ,
        Researcher bookedBy
    ) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.purpose = purpose;
        this.status = status;
        this.bookedEQ = bookedEQ;
        this.bookedBy = bookedBy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Equipment getBookedEQ() {
        return bookedEQ;
    }

    public void setBookedEQ(Equipment bookedEQ) {
        this.bookedEQ = bookedEQ;
    }

    public Researcher getBookedBy() {
        return bookedBy;
    }

    public void setBookedBy(Researcher bookedBy) {
        this.bookedBy = bookedBy;
    }
}

// composition between a reasearcher and a piece of equipment
