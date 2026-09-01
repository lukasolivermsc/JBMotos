package com.jbmotos.app.main.service;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ServiceDao {

    @Insert
    void insert(Service service);

    @Query("SELECT * FROM Service")
    List<Service> getAllServices();
}