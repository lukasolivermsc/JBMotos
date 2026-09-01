package com.jbmotos.app.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.jbmotos.app.main.service.Service;

import java.util.concurrent.Executors;

public class DatabaseClient {

    private Context context;
    private static DatabaseClient instance;

    private AppDatabase appDatabase;

    private DatabaseClient(Context context) {
        this.context = context;
        appDatabase = Room.databaseBuilder(context, AppDatabase.class, "jb-database")
                .addCallback(new RoomDatabase.Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);

                        Executors.newSingleThreadExecutor().execute(() -> {
                            AppDatabase database = getInstance(context).getAppDatabase();

                            database.serviceDao().insert(new Service("Troca de óleo", 8000, 30, "Troca de óleo do motor"));
                            database.serviceDao().insert(new Service("Revisão geral", 15000, 90, "Inspeção completa da motocicleta"));
                            database.serviceDao().insert(new Service("Balanceamento", 5000, 20, "Balanceamento de rodas"));
                            database.serviceDao().insert(new Service("Alinhamento", 7000, 25, "Correção do alinhamento das rodas"));
                            database.serviceDao().insert(new Service("Troca de pastilha", 6000, 40, "Substituição das pastilhas de freio"));
                            database.serviceDao().insert(new Service("Alinhamento de Direção", 6000, 60, "Correção do alinhamento do guidão e roda dianteira para estabilidade e segurança."));
                            database.serviceDao().insert(new Service("Troca de Fluido de Freio", 4500, 30, "Substituição completa do fluido de freio para manter a eficiência do sistema."));
                            database.serviceDao().insert(new Service("Revisão Elétrica", 9000, 90, "Verificação de bateria, cabos, luzes e sistema de ignição."));
                            database.serviceDao().insert(new Service("Troca de Corrente e Coroa", 12000, 120, "Substituição do kit relação (corrente, coroa e pinhão) com ajuste de tensão."));
                            database.serviceDao().insert(new Service("Limpeza de Bico Injetor", 8000, 45, "Remoção de sujeira e resíduos do sistema de injeção eletrônica para melhor desempenho."));

                        });
                    }
                })
                .allowMainThreadQueries()
                .build();
    }

    public static synchronized DatabaseClient getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseClient(context.getApplicationContext());
        }
        return instance;
    }

    public AppDatabase getAppDatabase() {
        return appDatabase;
    }
}
