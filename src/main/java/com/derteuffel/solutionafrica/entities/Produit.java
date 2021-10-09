package com.derteuffel.solutionafrica.entities;

import com.derteuffel.solutionafrica.enums.Devise;
import com.derteuffel.solutionafrica.enums.ProduitCategories;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;

@Entity
@Table(name = "produit")
@Data
public class Produit implements Serializable{

    @Id
    @GeneratedValue
    private Long id;

    private String name;
     private Double price;
     private String pictureUrl;
     private Devise devise;
     private ProduitCategories category;
     private ArrayList<String> colors;
     @ManyToOne
     private Boutique boutique;

    public Produit() {
    }

    public Produit(String name, Double price, String pictureUrl, ArrayList<String> colors) {
        this.name = name;
        this.price = price;
        this.pictureUrl = pictureUrl;
        this.colors = colors;
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public ArrayList<String> getColors() {
        return colors;
    }

    public void setColors(ArrayList<String> colors) {
        this.colors = colors;
    }

    public ProduitCategories getCategory() {
        return category;
    }

    public void setCategory(ProduitCategories category) {
        this.category = category;
    }

    public Devise getDevise() {
        return devise;
    }

    public void setDevise(Devise devise) {
        this.devise = devise;
    }

    public Boutique getBoutique() {
        return boutique;
    }

    public void setBoutique(Boutique boutique) {
        this.boutique = boutique;
    }
}
