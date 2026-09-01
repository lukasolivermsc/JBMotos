package com.jbmotos.app.main.service;

import android.content.Context;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.jbmotos.app.database.AppDatabase;
import com.jbmotos.app.database.DatabaseClient;
import com.jbmotos.app.main.motorcycle.Motorcycle;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity(
    tableName = "schedules",
    foreignKeys = {
            @ForeignKey(
                entity = Service.class,
                parentColumns = "id",
                childColumns = "serviceId",
                onDelete = ForeignKey.CASCADE
            ),
            @ForeignKey(
                entity = Motorcycle.class,
                parentColumns = "id",
                childColumns = "motorcycleId",
                onDelete = ForeignKey.CASCADE
            )
    },
    indices = {@Index("serviceId"), @Index("motorcycleId")}
)
public class ServiceScheduled {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int serviceId;
    private int motorcycleId;
    /** Timestamp em epoch millis */
    private long timestamp;

    public ServiceScheduled() {

    }

    public ServiceScheduled(int serviceId, int motorcycleId, LocalDateTime dateTime) {
        this.serviceId = serviceId;
        this.motorcycleId = motorcycleId;
        this.timestamp = dateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    @Ignore
    public ServiceScheduled(Service service, Motorcycle motorcycle, LocalDateTime dateTime) {
        this.serviceId = service.getId();
        this.motorcycleId = motorcycle.getId();
        this.timestamp = dateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    @Ignore
    public ServiceScheduled(Service service) {
        this.serviceId = service.getId();
        this.motorcycleId = -1;
        this.timestamp = -1;
    }

    public LocalDateTime getDateTime() {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneOffset.UTC);
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.timestamp = dateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getServiceId() { return serviceId; }
    public void setServiceId(int serviceId) { this.serviceId = serviceId; }

    public int getMotorcycleId() { return motorcycleId; }
    public void setMotorcycleId(int motorcycleId) { this.motorcycleId = motorcycleId; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }



    public static Map<LocalDate, List<Pair<LocalTime, LocalTime>>> getUnavailableDateTimes(@NonNull Context context) {
        Map<LocalDate, List<Pair<LocalTime, LocalTime>>> unavailableDates = new HashMap<>();

        AppDatabase appDatabase = DatabaseClient.getInstance(context).getAppDatabase();
        List<ServiceScheduled> serviceScheduledList = appDatabase.serviceScheduledDao().getAll();

        for (ServiceScheduled scheduledService : serviceScheduledList) {
            LocalDate date = scheduledService.getDateTime().toLocalDate();
            LocalTime startTime = scheduledService.getDateTime().toLocalTime();
            LocalTime endTime = scheduledService.getDateTime()
                    .plusMinutes(appDatabase.serviceDao().getById(scheduledService.getServiceId()).getDuration())
                    .toLocalTime();
            Pair<LocalTime, LocalTime> timeSlot = Pair.create(startTime, endTime);

            //Adicionar na lista do dia
            unavailableDates.computeIfAbsent(date, k -> new ArrayList<>()).add(timeSlot);
        }
        return unavailableDates;
    }

}
