package com.jbmotos.app.main.motorcycle;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.jbmotos.app.main.service.Service;
import com.jbmotos.app.main.service.ServiceScheduled;

import java.util.List;

@Dao
public interface MotorcycleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Motorcycle motorcycle);

    @Query("SELECT * FROM motorcycles")
    List<Motorcycle> getAll();

    @Query("SELECT * FROM motorcycles WHERE id = :id LIMIT 1")
    Motorcycle getById(int id);

    @Query("SELECT * FROM motorcycles WHERE clientUsername = :clientUsername")
    List<Motorcycle> getByClientUsername(String clientUsername);

    @Update
    void update(Motorcycle motorcycle);

    @Delete
    void delete(Motorcycle motorcycle);

}