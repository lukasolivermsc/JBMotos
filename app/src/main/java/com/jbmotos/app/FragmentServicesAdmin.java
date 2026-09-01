package com.jbmotos.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.jbmotos.app.main.service.Service;
import com.jbmotos.app.main.service.ServiceAddOverlayActivity;
import com.jbmotos.app.main.service.ServiceEditOverlayActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentServicesAdmin#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentServicesAdmin extends FragmentServices {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        ActivityResultLauncher<Intent> addServiceLauncher;
        addServiceLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        adapter.setServiceList(getServiceList());
                    }
                }
        );

        ImageButton addButton = view.findViewById(R.id.addServiceButton_adm);
        addButton.setOnClickListener(item-> {
            if (getActivity() instanceof MainActivity) {
                Intent intent = new Intent(getActivity(), ServiceAddOverlayActivity.class);
                addServiceLauncher.launch(intent);
            }
        });

        return view;
    }

    @Override
    public void onServiceClick(Service service) {
        if (getActivity() instanceof MainActivity) {
            Intent intent = new Intent(getActivity(), ServiceEditOverlayActivity.class);
            intent.putExtra("service_id", service.getId());
            serviceOverlayLauncher.launch(intent);
        }
    }

    @Override
    protected int getFragmentServicesLayoutId() {
        return R.layout.fragment_services_admin;
    }

    @Override
    protected int getRecyclerViewServicesId() {
        return R.id.recyclerViewServices_adm;
    }
}