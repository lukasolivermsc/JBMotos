package com.jbmotos.app;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jbmotos.app.database.AppDatabase;
import com.jbmotos.app.database.DatabaseClient;
import com.jbmotos.app.main.product.Product;
import com.jbmotos.app.main.product.ProductAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentStore#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentStore extends Fragment {

    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private AppDatabase db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewProducts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        db = DatabaseClient.getInstance(getContext()).getAppDatabase();
        List<Product> productList = db.productDao().getAllProducts();
        List<Product> produtosTeste = getTestProductList();

        adapter = new ProductAdapter(produtosTeste);
        recyclerView.setAdapter(adapter);

        return view;
    }

    private List<Product> getTestProductList() {
        List<Product> produtosTeste = new ArrayList<>();

        produtosTeste.add(new Product("Capacete X11 Vortex", 399.90));
        produtosTeste.add(new Product("Jaqueta Texx Strike", 699.00));
        produtosTeste.add(new Product("Luvas Alpinestars SP-8", 299.90));
        produtosTeste.add(new Product("Bota ASW Racing", 499.99));
        produtosTeste.add(new Product("Óleo Motul 7100 10W40", 89.90));
        produtosTeste.add(new Product("Corrente DID 520VX3", 249.00));
        produtosTeste.add(new Product("Pneu Michelin Pilot Road 4", 799.00));
        produtosTeste.add(new Product("Pneu Pirelli Diablo Rosso III", 759.00));
        produtosTeste.add(new Product("Pastilha de Freio EBC FA213", 129.00));
        produtosTeste.add(new Product("Kit Relação VAZ Premium", 699.90));
        produtosTeste.add(new Product("Banco Conforto Pro Tork", 399.00));
        produtosTeste.add(new Product("Manopla Progrip 714", 89.00));
        produtosTeste.add(new Product("Retrovisor Esportivo Rizoma", 249.00));
        produtosTeste.add(new Product("Paralama Traseiro CRF 230", 199.00));
        produtosTeste.add(new Product("Bolsa Tanque Givi EA106B", 359.00));
        produtosTeste.add(new Product("Top Case Givi B32N", 549.00));
        produtosTeste.add(new Product("Suporte Bauleto Chapam", 299.00));
        produtosTeste.add(new Product("Protetor de Motor Scam", 599.00));
        produtosTeste.add(new Product("Disco de Freio Braking Wave", 499.00));
        produtosTeste.add(new Product("Rolamento de Roda SKF", 89.90));
        produtosTeste.add(new Product("Amortecedor Traseiro Cofap", 659.00));
        produtosTeste.add(new Product("Farol de LED Universal", 159.00));
        produtosTeste.add(new Product("Cabo de Embreagem VAZ", 79.00));
        produtosTeste.add(new Product("Guidão Fat Bar Oxxy", 289.90));
        produtosTeste.add(new Product("Roda de Liga Leve Sportive", 1499.00));
        produtosTeste.add(new Product("Corrente Regina 520ZRP", 299.00));
        produtosTeste.add(new Product("Vela Iridium NGK CR9EIX", 89.00));
        produtosTeste.add(new Product("Escapamento Esportivo Jeskap", 1199.00));
        produtosTeste.add(new Product("Sensor de Marcha Universal", 139.00));
        produtosTeste.add(new Product("Alforge Lateral Texx", 489.90));

        return produtosTeste;
    }
}