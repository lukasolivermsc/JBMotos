package com.jbmotos.app.main.service;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jbmotos.app.R;
import com.jbmotos.app.database.AppDatabase;
import com.jbmotos.app.database.DatabaseClient;
import com.jbmotos.app.main.motorcycle.Motorcycle;
import com.jbmotos.app.main.motorcycle.MotorcycleDao;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ServiceScheduleAdmAdapter extends RecyclerView.Adapter<ServiceScheduleAdmAdapter.ServiceScheduledViewHolder>{

    private final List<ServiceScheduled> serviceScheduledList;

    public ServiceScheduleAdmAdapter(@NonNull List<ServiceScheduled> serviceScheduledList) {
        this.serviceScheduledList = serviceScheduledList;
    }

    @NonNull
    @Override
    public ServiceScheduledViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_motorcycle_schedule_adm, parent, false);
        return new ServiceScheduledViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceScheduledViewHolder holder, int position) {
        AppDatabase appDatabase = DatabaseClient.getInstance(holder.itemView.getContext()).getAppDatabase();
        ServiceDao serviceDao = appDatabase.serviceDao();
        MotorcycleDao motorcycleDao = appDatabase.motorcycleDao();

        ServiceScheduled serviceScheduled = serviceScheduledList.get(position);
        Service service = serviceDao.getById(serviceScheduled.getServiceId());
        Motorcycle motorcycle = motorcycleDao.getById(serviceScheduled.getMotorcycleId());

        holder.textViewServiceName.setText(service.getName());
        holder.textViewDate.setText(serviceScheduled.getDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        holder.textViewCustomerUsername.setText(motorcycle.getClientUsername());
        holder.textViewBrandAndModel.setText(motorcycle.getBrandAndModel());
    }

    @Override
    public int getItemCount() {
        return serviceScheduledList.size();
    }

    public static class ServiceScheduledViewHolder extends RecyclerView.ViewHolder {
        TextView textViewServiceName;
        TextView textViewCustomerUsername;
        TextView textViewDate;
        TextView textViewBrandAndModel;

        public ServiceScheduledViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewServiceName = itemView.findViewById(R.id.textViewName);
            textViewCustomerUsername = itemView.findViewById(R.id.textViewCustomer);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewBrandAndModel = itemView.findViewById(R.id.textViewMotorcycle);
        }
    }
}
