package com.jbmotos.app.main.service;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.jbmotos.app.main.motorcycle.Motorcycle;

import java.util.List;

@Dao
public interface ServiceScheduledDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ServiceScheduled serviceScheduled);

    @Query("SELECT * FROM schedules")
    List<ServiceScheduled> getAll();

    @Query("SELECT * FROM schedules ORDER BY timestamp")
    List<ServiceScheduled> getAllOrdered();

    @Query("SELECT * FROM schedules WHERE id = :id LIMIT 1")
    ServiceScheduled getById(int id);

    @Query("SELECT * FROM schedules WHERE motorcycleId = :motorcycleId")
    List<ServiceScheduled> getByMotorcycleId(int motorcycleId);

    @Query("SELECT * FROM schedules WHERE motorcycleId = :motorcycleId ORDER BY timestamp")
    List<ServiceScheduled> getByMotorcycleIdOrdered(int motorcycleId);

    @Update
    void update(ServiceScheduled serviceScheduled);

    @Delete
    void delete(ServiceScheduled serviceScheduled);

}