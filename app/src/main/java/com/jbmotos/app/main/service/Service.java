package com.jbmotos.app.main.service;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Service implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private final String name;

    /** Preço em centavos */
    private int price = -1;
    private String description;

    /** Duração em minutos */
    private int duration = 0;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getDuration() {
        return duration;
    }

    public String getDescription() {
        return description;
    }

    /**
     * @param price Preço em centavos
     * @param duration Duração em minutos
     */
    public Service(int id, String name, int price, int duration, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.duration = duration;
        this.description = description;
    }

    @Ignore
    public Service(Service service) {
        id = service.id;
        name = service.name;
        price = service.price;
        duration = service.duration;
        description = service.description;
    }

    /**
     * @param price Preço em centavos
     * @param duration Duração em minutos
     */
    @Ignore
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

    /**
     * @param price Preço em centavos
     * @param duration Duração em minutos
     */
    @Ignore
    public Service(int id, String name, int price, int duration) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.duration = duration;
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



    // Parcelable implementation
    protected Service(Parcel in) {
        name = in.readString();
        price = in.readInt();
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
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(price);
        dest.writeInt(duration);
    }
}
