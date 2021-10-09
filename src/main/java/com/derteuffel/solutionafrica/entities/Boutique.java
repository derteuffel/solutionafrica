package com.derteuffel.solutionafrica.entities;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "boutique")
public class Boutique implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String phone;
    private String localisation;
    private String profile;

    @ManyToOne
    private User user;

    public Boutique() {
    }

    public Boutique(String name, String phone, String localisation, String profile) {
        this.name = name;
        this.phone = phone;
        this.localisation = localisation;
        this.profile = profile;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLocalisation() {
        return localisation;
    }

    public void setLocalisation(String localisation) {
        this.localisation = localisation;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
