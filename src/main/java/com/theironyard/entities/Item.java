package com.theironyard.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue
    private int id;

    @Column(nullable = false)
    private String title;

    @Column
    private String brand;

    @Column
    private String model;

    @Column
    private String upc;

    @Column
    private String asin;

    @Column
    private String imageUrl;

    @JsonIgnore
    @Column
    private java.sql.Date date;  // JPA not yet supporting Java.time.LocalDate

    public Item(String title, String brand, String model, String upc,  String asin, String imageUrl) {
        this.title = title;
        this.brand = brand;
        this.model = model;
        this.upc = upc;
        this.asin = asin;
        this.imageUrl = imageUrl;
        this.date = new java.sql.Date(new java.util.Date().getTime());
    }

    public Item() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUpc() {
        return upc;
    }

    public void setUpc(String upc) {
        this.upc = upc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAsin() {
        return asin;
    }

    public void setAsin(String asin) {
        this.asin = asin;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
