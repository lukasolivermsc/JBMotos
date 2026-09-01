package com.jbmotos.app.main.motorcycle;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.jbmotos.app.R;
import com.jbmotos.app.database.DatabaseClient;
import com.jbmotos.app.session.SessionManager;

public class AddMotorcycleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_motorcycle);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.addMotorcyleOverlayLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button saveButton = findViewById(R.id.btnSaveMotorcycle);
        saveButton.setOnClickListener(v -> {
            EditText nameE = findViewById(R.id.editMotorcycleName);
            EditText brandE = findViewById(R.id.editMotorcycleBrand);
            String name = nameE.getText().toString();
            String brand = brandE.getText().toString();

            if (!(name.isEmpty() || brand.isEmpty())) {
                SessionManager sessionManager = SessionManager.getInstance(getApplicationContext());
                MotorcycleDao motorcycleDao = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().motorcycleDao();
                motorcycleDao.insert(new Motorcycle(name, brand, sessionManager.getUser().getName()));
                setResult(RESULT_OK);
                finish();
            }
        });

        ImageButton closeButton = findViewById(R.id.btnCloseAddMotorcycle);
        closeButton.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }
}