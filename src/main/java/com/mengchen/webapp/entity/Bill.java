package com.mengchen.webapp.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.CascadeType;


import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "billing")
public class Bill {

    @Id
    @GenericGenerator(name = "system-uuid", strategy = "uuid.hex")
    @GeneratedValue(generator = "system-uuid")
    @Column(name="id")
    private String bill_id;

    @Column(name="created_ts", updatable = false)
    private String created_ts;

    @Column(name="updated_ts")
    private String updated_ts;

    @Column(name="owner_id")
    private String owner_id;

    @Column(name="vendor")
    private String vendor;

    @Column(name= "bill_date")
    private String bill_date;

    @Column(name="due_date")
    private String due_date;

    @Column(name="amount_due")
    private double amount_due;

//    @Type(type="json")
    @ElementCollection
    @Column(name="categories")
    private List<String> categories;

    @Enumerated(EnumType.STRING)
    @Column(name= "payment_status")
    private PaymentStatus payment_status;

    @OneToOne(fetch = FetchType.LAZY, cascade = javax.persistence.CascadeType.ALL)
    @Cascade(value = {CascadeType.DELETE})
    @JoinColumn(name = "attachment", referencedColumnName = "id")
    @JsonManagedReference
    private File attachment;

    public Bill() {
    }

    enum PaymentStatus{
        paid, due, past_due, no_payment_required
    }

    @PrePersist
    public void prePersist() {
        created_ts = LocalDateTime.now().toString();
        updated_ts = LocalDateTime.now().toString();
    }

    @PreUpdate
    public void preUpdate() {
        updated_ts = LocalDateTime.now().toString();
    }

    public String getBill_id() {
        return bill_id;
    }

    public void setBill_id(String bill_id) {
        this.bill_id = bill_id;
    }

    public String getCreated_ts() {
        return created_ts;
    }

    public void setCreated_ts(String created_ts) {
        this.created_ts = created_ts;
    }

    public String getUpdated_ts() {
        return updated_ts;
    }

    public void setUpdated_ts(String updated_ts) {
        this.updated_ts = updated_ts;
    }

    public String getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getBill_date() {
        return bill_date;
    }

    public void setBill_date(String bill_date) {
        this.bill_date = bill_date;
    }

    public String getDue_date() {
        return due_date;
    }

    public void setDue_date(String due_date) {
        this.due_date = due_date;
    }

    public double getAmount_due() {
        return amount_due;
    }

    public void setAmount_due(double amount_due) {
        this.amount_due = amount_due;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public PaymentStatus getPayment_status () {
        return payment_status;
    }

    public void setPayment_status (PaymentStatus payment_status) {
        this.payment_status = payment_status;
    }

    public File getAttachment() {
        return attachment;
    }

    public void setAttachment(File attachment) {
        this.attachment = attachment;
    }

    @Override
    public String toString() {
        return "Bill{" +
                "bill_id='" + bill_id + '\'' +
                ", created_ts='" + created_ts + '\'' +
                ", updated_ts='" + updated_ts + '\'' +
                ", owner_id='" + owner_id + '\'' +
                ", vendor='" + vendor + '\'' +
                ", bill_date='" + bill_date + '\'' +
                ", due_date='" + due_date + '\'' +
                ", amount_due=" + amount_due +
                ", categories=" + categories +
                ", payment_status=" + payment_status +
                ", attachment='" + attachment + '\'' +
                '}';
    }
}
