package com.jbmotos.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jbmotos.app.database.AppDatabase;
import com.jbmotos.app.database.DatabaseClient;
import com.jbmotos.app.main.motorcycle.AddMotorcycleActivity;
import com.jbmotos.app.main.service.Service;
import com.jbmotos.app.main.service.ServiceAdapter;
import com.jbmotos.app.main.service.ServiceOverlayActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentServices#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentServices extends Fragment implements ServiceAdapter.OnServiceClickListener {

    protected RecyclerView recyclerView;
    protected ServiceAdapter adapter;
    protected AppDatabase db;
    protected ActivityResultLauncher<Intent> serviceOverlayLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(getFragmentServicesLayoutId(), container, false);

        serviceOverlayLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        adapter.setServiceList(getServiceList());
                    }
                }
        );

        recyclerView = view.findViewById(getRecyclerViewServicesId());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        db = DatabaseClient.getInstance(getContext()).getAppDatabase();
        List<Service> serviceList = getServiceList();

        adapter = new ServiceAdapter(serviceList, this);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onServiceClick(Service service) {
        if (getActivity() instanceof MainActivity) {
            Intent intent = new Intent(getActivity(), ServiceOverlayActivity.class);
            intent.putExtra("SERVICE", service);
            serviceOverlayLauncher.launch(intent);
        }
    }

    protected int getFragmentServicesLayoutId() {
        return R.layout.fragment_services;
    }

    protected int getRecyclerViewServicesId() {
        return R.id.recyclerViewServices;
    }

    protected List<Service> getServiceList() {
        return db.serviceDao().getAllServices();
    }
}