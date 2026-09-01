package com.jbmotos.app;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jbmotos.app.database.DatabaseClient;
import com.jbmotos.app.main.motorcycle.Motorcycle;
import com.jbmotos.app.main.motorcycle.MotorcycleAdapter;
import com.jbmotos.app.main.motorcycle.MotorcycleScheduleAdapter;
import com.jbmotos.app.main.service.Service;
import com.jbmotos.app.main.service.ServiceScheduleAdmAdapter;
import com.jbmotos.app.main.service.ServiceScheduled;
import com.jbmotos.app.session.SessionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentHome#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentHomeAdmin extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private ServiceScheduleAdmAdapter adapter;
    private SessionManager sessionManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home_admin, container, false);

        sessionManager = SessionManager.getInstance(getContext());
        recyclerView = view.findViewById(R.id.scheduleRecyclerView_adm);

        //Nome e botão sair

        ConstraintLayout loggedInLayout = view.findViewById(R.id.loggedInLayout_adm);
        TextView textViewLoading = view.findViewById(R.id.textViewLoading_adm);
        if (sessionManager.isLoggedIn()) {
            loggedInLayout.setVisibility(View.VISIBLE);
            textViewLoading.setVisibility(View.GONE);

            String username = sessionManager.getUser().getName();
            TextView textView = view.findViewById(R.id.textViewWelcome_adm);
            textView.setText("Oi, " + username + "!");
        } else {
            loggedInLayout.setVisibility(View.GONE);
            textViewLoading.setVisibility(View.VISIBLE);
        }

        Button logoutButton = view.findViewById(R.id.btnLogout_adm);
        logoutButton.setOnClickListener(item -> {
            sessionManager.logout();
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).replaceFragment(R.id.frag_services);
            }
        });

        initializeScheduleView();

        return view;
    }

    private void initializeScheduleView() {
        List<ServiceScheduled> serviceScheduledList = DatabaseClient.getInstance(getContext()).getAppDatabase().serviceScheduledDao().getAllOrdered();
        adapter = new ServiceScheduleAdmAdapter(serviceScheduledList);

        //Desativar scroll horizontal do calendário
        recyclerView.setLayoutManager(new LinearLayoutManager(
                view.getContext(),
                LinearLayoutManager.VERTICAL,
                false) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        });

        recyclerView.setAdapter(adapter);
    }
}
