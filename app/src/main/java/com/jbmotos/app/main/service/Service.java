package com.jbmotos.app.main.service;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "services")
public class Service implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    /** Preço em centavos */
    private int price = -1;
    private String description = "";
    /** Duração em minutos */
    private int duration = 0;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    /**
     * @param price Preço em centavos
     * @param duration Duração em minutos
     */
    public Service(String name, int price, int duration, String description) {
        this.name = name;
        this.price = price;
        this.duration = duration;
        this.description = description;
    }

    /**
     * @param price Preço em centavos
     * @param duration Duração em minutos
     */
    @Ignore
    public Service(String name, int price, int duration) {
        this.name = name;
        this.price = price;
        this.duration = duration;
    }

    @Ignore
    public Service(Service other) {
        this.id = other.id;
        this.name = other.name;
        this.price = other.price;
        this.duration = other.duration;
        this.description = other.description;
    }

    @Ignore
    public Service(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Service that = (Service)o;
        return name.equals(that.name);
    }

    @SuppressLint("DefaultLocale")
    public String getFormattedPrice() {
        return String.format("R$ %d,%02d", getPrice() / 100, getPrice() % 100);
    }


    // --- Parcelable Implementation ---

    @Ignore
    protected Service(Parcel in) {
        id = in.readInt();
        name = in.readString();
        price = in.readInt();
        description = in.readString();
        duration = in.readInt();
    }

    public static final Creator<Service> CREATOR = new Creator<Service>() {
        @Override
        public Service createFromParcel(Parcel in) {
            return new Service(in);
        }

        @Override
        public Service[] newArray(int size) {
            return new Service[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(price);
        dest.writeString(description);
        dest.writeInt(duration);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
