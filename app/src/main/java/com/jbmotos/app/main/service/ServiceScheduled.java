package com.jbmotos.app.main.service;

import java.time.LocalDateTime;

public class ServiceScheduled {
    private final Service service;
    private final LocalDateTime date;

    public Service getService() {
        return service;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public ServiceScheduled(ServiceScheduled toCopy) {
        service = toCopy.getService();
        date = toCopy.getDate();
    }

    public ServiceScheduled(Service service, LocalDateTime date) {
        this.service = service;
        this.date = date;
    }

    public ServiceScheduled(Service service) {
        this.service = service;
        date = LocalDateTime.MIN;
    }
}
