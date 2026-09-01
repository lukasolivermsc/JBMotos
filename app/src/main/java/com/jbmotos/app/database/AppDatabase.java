package com.jbmotos.app.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.jbmotos.app.main.motorcycle.Motorcycle;
import com.jbmotos.app.main.motorcycle.MotorcycleDao;
import com.jbmotos.app.main.service.Service;
import com.jbmotos.app.main.service.ServiceDao;
import com.jbmotos.app.main.service.ServiceScheduled;
import com.jbmotos.app.main.service.ServiceScheduledDao;

@Database(entities = {Service.class, ServiceScheduled.class, Motorcycle.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ServiceDao serviceDao();
    public abstract ServiceScheduledDao serviceScheduledDao();
    public abstract MotorcycleDao motorcycleDao();
}
