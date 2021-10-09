package com.derteuffel.solutionafrica.entities;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "commande")
public class Commande implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private Date orderDate = new Date();
    private String orderNum;
    private String description;

    @ManyToOne
    private  Boutique boutique;

    public Commande() {
    }

    public Commande(Date orderDate, String orderNum, String description) {
        this.orderDate = orderDate;
        this.orderNum = orderNum;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boutique getBoutique() {
        return boutique;
    }

    public void setBoutique(Boutique boutique) {
        this.boutique = boutique;
    }
}
