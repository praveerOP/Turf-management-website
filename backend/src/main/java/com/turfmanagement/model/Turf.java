package com.turfmanagement.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class Turf implements Serializable {
    private String id;
    private String name;
    private String type; // football, cricket, tennis, etc.
    private String size; // small, medium, large
    private BigDecimal pricePerHour;
    private boolean isAvailable;
    private String description;
    private String imageUrl;

    public Turf() {}

    public Turf(String id, String name, String type, String size, BigDecimal pricePerHour, boolean isAvailable, String description, String imageUrl) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.size = size;
        this.pricePerHour = pricePerHour;
        this.isAvailable = isAvailable;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public BigDecimal getPricePerHour() { return pricePerHour; }
    public void setPricePerHour(BigDecimal pricePerHour) { this.pricePerHour = pricePerHour; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    @Override
    public String toString() {
        return "Turf{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", size='" + size + '\'' +
                ", pricePerHour=" + pricePerHour +
                ", isAvailable=" + isAvailable +
                ", description='" + description + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
} 