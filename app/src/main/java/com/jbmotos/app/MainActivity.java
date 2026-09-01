package com.jbmotos.app;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.jbmotos.app.databinding.ActivityMainBinding;
import com.jbmotos.app.main.service.ServicesScheduleManager;
import com.jbmotos.app.session.LoginOverlayActivity;
import com.jbmotos.app.session.SessionManager;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    private ActivityResultLauncher<Intent> loginLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        SessionManager sessionManager = SessionManager.getInstance(getBaseContext().getApplicationContext());
        
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        binding.bottomNavigationView.setSelectedItemId(R.id.frag_home);
                        replaceFragment(new FragmentHome());
                    }
                }
        );

        if (sessionManager.isLoggedIn()) {
            binding.bottomNavigationView.setSelectedItemId(R.id.frag_home);
            replaceFragment(new FragmentHome());
        } else {
            binding.bottomNavigationView.setSelectedItemId(R.id.frag_services);
            replaceFragment(new FragmentStore());
        }

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.frag_home) {
                if (sessionManager.isLoggedIn()) {
                    replaceFragment(new FragmentHome());
                } else {
                    Intent intent = new Intent(this, LoginOverlayActivity.class);
                    loginLauncher.launch(intent);
                    return false;
                }
            } /*else if (itemId == R.id.frag_store) {
                replaceFragment(new FragmentStore());
            }*/ else if (itemId == R.id.frag_services) {
                replaceFragment(new FragmentServices());
            } else if (itemId == R.id.frag_contact) {
                replaceFragment(new FragmentContact());
            } else {
                return false;
            }

            return true;
        });

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void replaceFragment(int fragmentId) {
        binding.bottomNavigationView.setSelectedItemId(fragmentId);
    }
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_frameLayout, fragment);
        fragmentTransaction.commit();
    }
}