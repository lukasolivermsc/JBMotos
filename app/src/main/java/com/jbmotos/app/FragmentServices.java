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

    private RecyclerView recyclerView;
    private ServiceAdapter adapter;
    private AppDatabase db;

    private ActivityResultLauncher<Intent> serviceOverlayLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_services, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewServices);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        db = DatabaseClient.getInstance(getContext()).getAppDatabase();
        List<Service> serviceList = db.serviceDao().getAllServices();
        List<Service> servicosTeste = getTestServiceList();

        adapter = new ServiceAdapter(servicosTeste, this);
        recyclerView.setAdapter(adapter);

        //Botão de confirmar

        serviceOverlayLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                    }
                }
        );

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

    private List<Service> getTestServiceList() {
        List<Service> servicosTeste = new ArrayList<>();

        servicosTeste.add(new Service("Troca de óleo", 8990, 30));
        servicosTeste.add(new Service("Troca de pneu dianteiro", 7990, 20));
        servicosTeste.add(new Service("Troca de pneu traseiro", 8990, 25));
        servicosTeste.add(new Service("Alinhamento de rodas", 5900, 45));
        servicosTeste.add(new Service("Balanceamento de rodas", 4900, 30));
        servicosTeste.add(new Service("Revisão geral", 29990, 120));
        servicosTeste.add(new Service("Troca de pastilha de freio", 9900, 40));
        servicosTeste.add(new Service("Troca de relação (corrente, coroa e pinhão)", 19990, 90));
        servicosTeste.add(new Service("Troca de cabo de embreagem", 6990, 50));
        servicosTeste.add(new Service("Troca de cabo de acelerador", 6990, 40));
        servicosTeste.add(new Service("Troca de vela de ignição", 4990, 20));
        servicosTeste.add(new Service("Instalação de baú", 7990, 60));
        servicosTeste.add(new Service("Instalação de alarme", 19990, 90));
        servicosTeste.add(new Service("Limpeza de bico injetor", 14990, 75));
        servicosTeste.add(new Service("Troca de amortecedor traseiro", 11990, 60));
        servicosTeste.add(new Service("Instalação de protetor de motor", 9990, 45));
        servicosTeste.add(new Service("Troca de disco de freio", 14990, 80));
        servicosTeste.add(new Service("Troca de fluido de freio", 5990, 30));
        servicosTeste.add(new Service("Lavagem detalhada premium", 12990, 120));
        servicosTeste.add(new Service("Inspeção elétrica completa", 15990, 90));

        return servicosTeste;
    }
}