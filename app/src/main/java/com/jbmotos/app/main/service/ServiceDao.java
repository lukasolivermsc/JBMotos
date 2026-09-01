package com.jbmotos.app.main.service;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ServiceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Service service);

    @Query("SELECT * FROM services")
    List<Service> getAll();

    @Query("SELECT * FROM services WHERE id = :id LIMIT 1")
    Service getById(int id);

    @Update
    void update(Service service);

    @Delete
    void delete(Service service);

    @Query("SELECT * FROM services")
    List<Service> getAllServices();
}