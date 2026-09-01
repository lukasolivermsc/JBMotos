package com.jbmotos.app.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.jbmotos.app.main.product.Product;
import com.jbmotos.app.main.product.ProductDao;
import com.jbmotos.app.main.service.Service;
import com.jbmotos.app.main.service.ServiceDao;

@Database(entities = {Product.class, Service.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ProductDao productDao();
    public abstract ServiceDao serviceDao();
}
