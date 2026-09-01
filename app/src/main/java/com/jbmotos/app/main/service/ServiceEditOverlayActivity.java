package com.jbmotos.app.main.service;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.jbmotos.app.R;
import com.jbmotos.app.database.AppDatabase;
import com.jbmotos.app.database.DatabaseClient;

import java.text.NumberFormat;
import java.util.Locale;

public class ServiceEditOverlayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_service_edit_overlay);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.editServiceOverlayLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        int serviceId = getIntent().getIntExtra("service_id", -1);
        AppDatabase db = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase();
        Service service = db.serviceDao().getById(serviceId);

        EditText editServiceName = findViewById(R.id.editServiceName);
        EditText editServicePrice = findViewById(R.id.editServicePrice);
        NumberPicker hoursPicker = findViewById(R.id.pickerHours);
        NumberPicker minutesPicker = findViewById(R.id.pickerMinutes);

        editServiceName.setText(service.getName());
        editServicePrice.setText(service.getFormattedPrice());

        editServicePrice.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(current)) {
                    editServicePrice.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[R$,.\\s]", "");

                    if (!cleanString.isEmpty()) {
                        double parsed = Double.parseDouble(cleanString) / 100;
                        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
                        String formatted = format.format(parsed);

                        current = formatted;
                        editServicePrice.setText(formatted);
                        editServicePrice.setSelection(formatted.length());
                    }

                    editServicePrice.addTextChangedListener(this);
                }
            }
        });;

        hoursPicker.setMinValue(0);
        hoursPicker.setMaxValue(12);
        hoursPicker.setValue(0);
        hoursPicker.setWrapSelectorWheel(false);

        String[] minuteValues = new String[12];
        for (int i = 0; i < 12; i++) {
            minuteValues[i] = String.valueOf(i * 5);
        }
        minutesPicker.setMinValue(0);
        minutesPicker.setMaxValue(minuteValues.length - 1);
        minutesPicker.setDisplayedValues(minuteValues);
        minutesPicker.setWrapSelectorWheel(false);

        hoursPicker.setValue(service.getDuration() / 60);
        minutesPicker.setValue(service.getDuration() % 60);

        Button saveButton = findViewById(R.id.btnSaveService);
        saveButton.setOnClickListener(v -> {
            String name = editServiceName.getText().toString();
            String price = editServicePrice.getText().toString();
            int hours = hoursPicker.getValue();
            int minutes = Integer.parseInt(minuteValues[minutesPicker.getValue()]);
            int totalMinutes = hours * 60 + minutes;

            if (!(name.isEmpty() || price.isEmpty()) && totalMinutes > 0) {
                String cleanString = price.replaceAll("[R$,.\\s]", "");
                int priceInCents;
                if (!cleanString.isEmpty()) {
                    priceInCents = Integer.parseInt(cleanString);
                } else {
                    priceInCents = 0;
                }

                if (priceInCents > 0) {
                    new Thread(() -> {
                        if (serviceId == -1) {
                            throw new IllegalArgumentException("Service ID inválido: nenhum ID foi passado para edição.");
                        } else {
                            Service s = db.serviceDao().getById(serviceId);
                            if (s != null) {
                                s.setName(name);
                                s.setPrice(priceInCents);
                                s.setDuration(totalMinutes);
                                db.serviceDao().update(s);
                                setResult(RESULT_OK);
                                finish();
                            } else {
                                throw new IllegalStateException("Serviço com ID " + serviceId + " não encontrado no banco.");
                            }
                        }

                        runOnUiThread(this::finish);
                    }).start();
                }
            }
        });

        Button deleteButton = findViewById(R.id.btnDeleteService);
        deleteButton.setOnClickListener(v -> {
            new Thread(() -> {
                if (serviceId == -1) {
                    throw new IllegalArgumentException("Service ID inválido: nenhum ID foi passado para deletar.");
                } else {
                    Service s = db.serviceDao().getById(serviceId);
                    if (s != null) {
                        db.serviceDao().delete(s);
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        throw new IllegalStateException("Serviço com ID " + serviceId + " não encontrado no banco.");
                    }
                }

                runOnUiThread(this::finish);
            }).start();
        });

        ImageButton closeButton = findViewById(R.id.btnCloseEditService);
        closeButton.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }
}