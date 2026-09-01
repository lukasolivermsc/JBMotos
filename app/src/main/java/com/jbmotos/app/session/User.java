package com.jbmotos.app.session;

import com.jbmotos.app.main.motorcycle.Motorcycle;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String name;
    private List<Motorcycle> motorcycles;

    public User(String name) {
        this.name = name;
        motorcycles = new ArrayList<Motorcycle>();
    }

    public String getName() {
        return name;
    }

    public void addMotorcycle(Motorcycle motorcycle) {
        if (motorcycle == null) return;
        motorcycles.add(motorcycle);
    }

    public boolean removeMotorcycle(int index) {
        if (index < 0 || index >= motorcycles.size()) return false;
        motorcycles.remove(index);
        return true;
    }

    public List<Motorcycle> getMotorcycles() {
        return motorcycles;
    }

    public List<Motorcycle> copyMotorcycles() {
        List<Motorcycle> copies = new ArrayList<>();
        for (Motorcycle motorcycle : motorcycles) {
            copies.add(new Motorcycle(motorcycle));
        }
        return copies;
    }

    public final List<String> getBrandAndModelOfAllMotorcycles() {
        List<String> returnArray = new ArrayList<>(motorcycles.size());
        for (Motorcycle motorcycle : motorcycles) {
            returnArray.add(motorcycle.getBrandAndModel());
        }
        return returnArray;
    }

    public int getNumOfMotorcycles() {
        return motorcycles.size();
    }
}
