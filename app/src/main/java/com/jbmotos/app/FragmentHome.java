package com.jbmotos.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jbmotos.app.main.motorcycle.AddMotorcycleActivity;
import com.jbmotos.app.main.motorcycle.Motorcycle;
import com.jbmotos.app.main.motorcycle.MotorcycleAdapter;
import com.jbmotos.app.main.motorcycle.MotorcycleScheduleAdapter;
import com.jbmotos.app.main.service.ServicesScheduleManager;
import com.jbmotos.app.session.SessionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentHome#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentHome extends Fragment {

    private View view;
    private RecyclerView recyclerView, recyclerView2;
    private MotorcycleAdapter adapter;
    private MotorcycleScheduleAdapter adapter2;
    private SessionManager sessionManager;
    private ViewModelHome viewModel;
    private SnapHelper snapHelper;

    LinearLayout indicatorLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        sessionManager = SessionManager.getInstance(getContext());
        viewModel = new ViewModelProvider(requireActivity()).get(ViewModelHome.class);

        //Nome e botão sair

        ConstraintLayout loggedInLayout = view.findViewById(R.id.loggedInLayout);
        TextView textViewLoading = view.findViewById(R.id.textViewLoading);
        if (sessionManager.isLoggedIn()) {
            loggedInLayout.setVisibility(View.VISIBLE);
            textViewLoading.setVisibility(View.GONE);

            String username = sessionManager.getUser().getName();
            TextView textView = view.findViewById(R.id.textViewWelcome);
            textView.setText("Oi, " + username + "!");
        } else {
            loggedInLayout.setVisibility(View.GONE);
            textViewLoading.setVisibility(View.VISIBLE);
        }

        Button logoutButton = view.findViewById(R.id.btnLogout);
        logoutButton.setOnClickListener(item-> {
            sessionManager.logout();
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).replaceFragment(R.id.frag_services);
            }
        });

        //Mostrador de motos

        indicatorLayout = view.findViewById(R.id.motorcycleIndicatorLayout);
        recyclerView = view.findViewById(R.id.recyclerViewMotorcycles);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        recyclerView2 = view.findViewById(R.id.scheduleRecyclerView);
        recyclerView2.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView2.setClickable(false);

        initializeMotorcycleTabs();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    View centerView = snapHelper.findSnapView(layoutManager);
                    int pos = layoutManager.getPosition(centerView);
                    updateIndicators(pos);
                    viewModel.currentIndicatorPosition = pos;
                    recyclerView2.scrollToPosition(viewModel.currentIndicatorPosition);
                }
            }
        });

        //Adicionar e remover motos

        ActivityResultLauncher<Intent> addMotorcycleLauncher;
        addMotorcycleLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        viewModel.currentIndicatorPosition = sessionManager.getUser().getNumOfMotorcycles() - 1;
                        initializeMotorcycleTabs();
                    }
                }
        );

        ImageButton addButton = view.findViewById(R.id.btnAddMotorcycle);
        addButton.setOnClickListener(item-> {
            if (getActivity() instanceof MainActivity) {
                Intent intent = new Intent(getActivity(), AddMotorcycleActivity.class);
                addMotorcycleLauncher.launch(intent);
            }
        });

        ImageButton removeButton = view.findViewById(R.id.btnRemoveMotorcycle);
        removeButton.setOnClickListener(item-> {
            if (sessionManager.removeMotorcycleFromUserAndSave(viewModel.currentIndicatorPosition)) {
                viewModel.currentIndicatorPosition = Math.max(0,
                    Math.min(
                        sessionManager.getUser().getNumOfMotorcycles() - 1,
                        viewModel.currentIndicatorPosition
                    )
                );
                ServicesScheduleManager.getInstance().setScheduledServicesViaUser(sessionManager.getUser());
                initializeMotorcycleTabs();
            }
        });

        return view;
    }

    private void initializeMotorcycleTabs() {
        List<Motorcycle> motorcycleList = sessionManager.getUser().getMotorcycles();
        if (motorcycleList.isEmpty()) {
            motorcycleList = new ArrayList<>(1);
            motorcycleList.add(Motorcycle.createEmpty());
        }
        adapter = new MotorcycleAdapter(motorcycleList);
        recyclerView.setAdapter(adapter);
        if (adapter2 == null) {
            adapter2 = new MotorcycleScheduleAdapter(motorcycleList);

            //Desativar scroll horizontal do calendário
            recyclerView2.setLayoutManager(new LinearLayoutManager(
                    view.getContext(),
                    LinearLayoutManager.HORIZONTAL,
                    false) {
                @Override
                public boolean canScrollHorizontally() {
                    return false;
                }
            });
        }
        else {
            adapter2.updateData(motorcycleList);
        }
        recyclerView2.setAdapter(adapter2);

        setupMotorcycleIndicators(motorcycleList.size());
        recyclerView.scrollToPosition(viewModel.currentIndicatorPosition);
        recyclerView2.scrollToPosition(viewModel.currentIndicatorPosition);
        updateIndicators(viewModel.currentIndicatorPosition);
    }

    private void setupMotorcycleIndicators(int count) {
        ImageView[] indicators = new ImageView[count];
        indicatorLayout.removeAllViews();

        for (int i = 0; i < count; i++) {
            indicators[i] = new ImageView(getContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(
                    getContext(), R.drawable.indicatordot_inactive));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 0);
            indicatorLayout.addView(indicators[i], params);
        }

        if (count > 0) {
            indicators[0].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.indicatordot_active));
        }
    }

    private void updateIndicators(int index) {
        for (int i = 0; i < indicatorLayout.getChildCount(); i++) {
            ImageView dot = (ImageView) indicatorLayout.getChildAt(i);
            dot.setImageDrawable(ContextCompat.getDrawable(
                    getContext(), i == index ? R.drawable.indicatordot_active : R.drawable.indicatordot_inactive
            ));
        }
    }
}