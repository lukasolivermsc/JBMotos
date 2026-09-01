package com.jbmotos.app.main.motorcycle;

import androidx.annotation.NonNull;

import com.jbmotos.app.main.service.Service;
import com.jbmotos.app.main.service.ServiceScheduled;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Motorcycle {
    private String name;
    private String brand;
    private List<ServiceScheduled> servicesScheduled = new ArrayList<>();

    public String getName() {
        return name;
    }

    public String getBrand() {
        return brand;
    }

    public String getBrandAndModel() {
        return getBrand() + " - " + getName();
    }

    public List<ServiceScheduled> getServicesScheduled() {
        return Collections.unmodifiableList(servicesScheduled);
    }

    public List<ServiceScheduled> getServicesScheduledOrdered() {
        return servicesScheduled.stream()
                .sorted(Comparator.comparing(ServiceScheduled::getDate))
                .collect(Collectors.toList());
    }

    public Motorcycle(String name, String brand) {
        this.name = name;
        this.brand = brand;
    }

    public Motorcycle(Motorcycle toCopy) {
        name = toCopy.name;
        brand = toCopy.brand;
    }

    public static Motorcycle createEmpty() {
        return new Motorcycle("Sem motos", "registradas");
    }

    public boolean isServiceScheduled(Service service) {
        for (ServiceScheduled schedule : servicesScheduled) {
            if (schedule.getService().equals(service)) {
                return true;
            }
        }
        return false;
    }

    public boolean scheduleService(ServiceScheduled toSchedule) {
        if (isServiceScheduled(toSchedule.getService())) {
            return false;
        }
        servicesScheduled.add(new ServiceScheduled(toSchedule));
        return true;
        /*System.out.println("Serviço registrado na " + getBrandAndModel() + ":");
        for (ServiceScheduled serviceScheduled : servicesScheduled) {
            System.out.println(serviceScheduled.getService().getName());
        }*/
    }

    public boolean scheduleService(Service service, LocalDateTime dateTime) {
        if (isServiceScheduled(service)) {
            return false;
        }
        servicesScheduled.add(new ServiceScheduled(service, dateTime));
        return true;
        /*System.out.println("Serviço registrado na " + getBrandAndModel() + ":");
        for (ServiceScheduled serviceScheduled : servicesScheduled) {
            System.out.println(serviceScheduled.getService().getName());
        }*/
    }

    @NonNull
    @Override
    public String toString() {
        return getBrandAndModel();
    }
}
