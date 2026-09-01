package com.jbmotos.app.main.service;

import android.util.Pair;

import androidx.annotation.NonNull;

import com.jbmotos.app.main.motorcycle.Motorcycle;
import com.jbmotos.app.session.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServicesScheduleManager {
    private static ServicesScheduleManager instance;
    private final List<ServiceScheduled> scheduledServices;

    private ServicesScheduleManager() {
        scheduledServices = new ArrayList<>();
    }

    public static synchronized ServicesScheduleManager getInstance() {
        if (instance == null) {
            instance = new ServicesScheduleManager();
        }
        return instance;
    }

    public void setScheduledServicesViaUser(User user) {
        scheduledServices.clear();
        for (Motorcycle motorcycle : user.getMotorcycles()) {
            for (ServiceScheduled serviceScheduled : motorcycle.getServicesScheduled()) {
                scheduleService(serviceScheduled);
            }
        }
    }

    public void resetScheduledServices() {
        scheduledServices.clear();
    }

    public void scheduleService(@NonNull ServiceScheduled toSchedule) {
        scheduledServices.add(toSchedule);
    }

    public boolean unScheduleService(ServiceScheduled serviceScheduled) {
        if (serviceScheduled == null) return false;

        for (int i = 0; i < scheduledServices.size(); i++) {
            if (scheduledServices.get(i) == serviceScheduled) {
                scheduledServices.remove(i);
                return true;
            }
        }
        return false;
    }

    public Map<LocalDate, List<Pair<LocalTime, LocalTime>>> getUnavailableDateTimes() {
        Map<LocalDate, List<Pair<LocalTime, LocalTime>>> unavailableDates = new HashMap<>();

        for (ServiceScheduled scheduledService : scheduledServices) {
            LocalDate date = scheduledService.getDate().toLocalDate();
            LocalTime startTime = scheduledService.getDate().toLocalTime();
            LocalTime endTime = scheduledService.getDate()
                    .plusMinutes(scheduledService.getService().getDuration())
                    .toLocalTime();
            Pair<LocalTime, LocalTime> timeSlot = Pair.create(startTime, endTime);

            //Adicionar na lista do dia
            unavailableDates.computeIfAbsent(date, k -> new ArrayList<>()).add(timeSlot);
        }
        return unavailableDates;
    }
}

