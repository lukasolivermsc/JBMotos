package com.jbmotos.app.session;

import android.content.Context;

import androidx.annotation.NonNull;

import com.jbmotos.app.database.DatabaseClient;
import com.jbmotos.app.main.motorcycle.Motorcycle;
import com.jbmotos.app.main.motorcycle.MotorcycleDao;

import java.util.ArrayList;
import java.util.List;

public class User_Regular extends User {

    private final List<Motorcycle> motorcycles;

    protected User_Regular(UserData data) {
        super(data);
        motorcycles = new ArrayList<Motorcycle>();
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

    public List<Motorcycle> getMotorcycles(@NonNull Context context) {
        MotorcycleDao motorcycleDao = DatabaseClient.getInstance(context).getAppDatabase().motorcycleDao();
        return motorcycleDao.getByClientUsername(getName());
    }

    public final List<String> getBrandAndModelOfAllMotorcycles(@NonNull Context context) {
        List<Motorcycle> motorcycleList = getMotorcycles(context);
        List<String> returnArray = new ArrayList<>(motorcycleList.size());
        for (Motorcycle motorcycle : motorcycleList) {
            returnArray.add(motorcycle.getBrandAndModel());
        }
        return returnArray;
    }

    public int getNumOfMotorcycles(@NonNull Context context) {
        return getMotorcycles(context).size();
    }
}
