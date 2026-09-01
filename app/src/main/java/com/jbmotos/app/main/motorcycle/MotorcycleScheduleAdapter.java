package com.jbmotos.app.main.motorcycle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jbmotos.app.R;
import com.jbmotos.app.main.service.Service;
import com.jbmotos.app.main.service.ServiceScheduled;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MotorcycleScheduleAdapter extends RecyclerView.Adapter<MotorcycleScheduleAdapter.MotorcycleScheduleViewHolder>{

    private List<Motorcycle> motorcycleList;

    public MotorcycleScheduleAdapter(List<Motorcycle> motorcycles) {
        this.motorcycleList = motorcycles;
    }

    public void updateData(List<Motorcycle> motorcycleList) {
        this.motorcycleList = motorcycleList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MotorcycleScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_motorcycle_schedule_list, parent, false);
        return new MotorcycleScheduleViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MotorcycleScheduleViewHolder holder, int position) {
        Motorcycle motorcycle = motorcycleList.get(position);

        holder.scheduleRecyclerView.setLayoutManager(
                new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.VERTICAL, false));

        List<ServiceScheduled> orderedServices = motorcycle.getServicesScheduledOrdered();
        if (orderedServices.isEmpty()) {
            orderedServices = new ArrayList<>(1);
            orderedServices.add(new ServiceScheduled(new Service("Nada agendado")));
        }
        holder.scheduleRecyclerView.setAdapter(
                new MotorcycleVerticalScheduleAdapter(orderedServices));
    }

    @Override
    public int getItemCount() {
        return motorcycleList.size();
    }

    public static class MotorcycleScheduleViewHolder extends RecyclerView.ViewHolder {
        RecyclerView scheduleRecyclerView;

        public MotorcycleScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            scheduleRecyclerView = itemView.findViewById(R.id.scheduleList);
        }
    }



    //Lista vertical

    private static class MotorcycleVerticalScheduleAdapter extends RecyclerView.Adapter<MotorcycleVerticalScheduleViewHolder>{

        private List<ServiceScheduled> serviceList;

        public MotorcycleVerticalScheduleAdapter(List<ServiceScheduled> services) {
            this.serviceList = services;
        }

        @NonNull
        @Override
        public MotorcycleVerticalScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_motorcycle_schedule, parent, false);
            return new MotorcycleVerticalScheduleViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MotorcycleVerticalScheduleViewHolder holder, int position) {
            ServiceScheduled service = serviceList.get(position);
            if (service != null) {
                holder.Name.setText(service.getService().getName());
                if (service.getDate() != LocalDateTime.MIN) {
                    holder.Date.setText(service.getDate().format(
                            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                    );
                }
            }
        }

        @Override
        public int getItemCount() {
            return serviceList.size();
        }
    }

    private static class MotorcycleVerticalScheduleViewHolder extends RecyclerView.ViewHolder {
        TextView Name;
        TextView Date;

        public MotorcycleVerticalScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            Name = itemView.findViewById(R.id.textViewName);
            Date = itemView.findViewById(R.id.textViewDate);
        }
    }
}
