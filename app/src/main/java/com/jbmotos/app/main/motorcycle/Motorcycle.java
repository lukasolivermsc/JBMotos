package com.jbmotos.app.main.motorcycle;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "motorcycles")
public class Motorcycle {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private String brand;
    /** Username do dono da moto */
    private String clientUsername;

    public Motorcycle(String name, String brand, String clientUsername) {
        this.name = name;
        this.brand = brand;
        this.clientUsername = clientUsername;
    }

    @Ignore
    private Motorcycle() {
        name = "";
        brand = "";
    }

    public static Motorcycle createEmpty() {
        return new Motorcycle();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getBrandAndModel() { return brand + " - " + name; }

    public String getClientUsername() { return clientUsername; }
    public void setClientUsername(String clientUsername) { this.clientUsername = clientUsername; }

    @NonNull
    @Override
    public String toString() {
        return getBrandAndModel();
    }
}
