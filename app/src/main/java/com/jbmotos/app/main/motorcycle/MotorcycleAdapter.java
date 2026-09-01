package com.jbmotos.app.main.motorcycle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jbmotos.app.R;

import java.util.List;

public class MotorcycleAdapter extends RecyclerView.Adapter<MotorcycleAdapter.MotorcycleViewHolder>{

    private final List<Motorcycle> motorcycleList;

    public MotorcycleAdapter(List<Motorcycle> motorcycles) {
        this.motorcycleList = motorcycles;
    }

    @NonNull
    @Override
    public MotorcycleAdapter.MotorcycleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_motorcycle, parent, false);
        return new MotorcycleAdapter.MotorcycleViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MotorcycleAdapter.MotorcycleViewHolder holder, int position) {
        Motorcycle motorcycle = motorcycleList.get(position);
        holder.textViewName.setText(motorcycle.getName());
        holder.textViewBrand.setText(motorcycle.getBrand());
    }

    @Override
    public int getItemCount() {
        return motorcycleList.size();
    }

    public static class MotorcycleViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        TextView textViewBrand;

        public MotorcycleViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewBrand = itemView.findViewById(R.id.textViewBrand);
        }
    }
}
