package com.jbmotos.app.session;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.jbmotos.app.main.motorcycle.Motorcycle;
import com.jbmotos.app.main.service.ServiceScheduled;
import com.jbmotos.app.main.service.ServicesScheduleManager;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SessionManager {
    private static final String PREF_NAME = "user_session";
    private static final String KEY_USER = "user_data";
    private static SessionManager instance;
    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;
    private User user;

    private SessionManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
        loadUser();
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context);
        }
        return instance;
    }

    private void loadUser() {
        String userJson = prefs.getString(KEY_USER, null);
        if (userJson != null) {
            Gson gson = createGson();
            user = gson.fromJson(userJson, User.class);
            ServicesScheduleManager.getInstance().setScheduledServicesViaUser(user);
        }
    }

    private Gson createGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    public void saveUser() {
        if (user != null) {
            Gson gson = createGson();
            String json = gson.toJson(user);
            editor.putString(KEY_USER, json);
            editor.apply();
        }
    }

    public void login(String username) {
        if (isLoggedIn()) return;
        user = new User(username);
        saveUser();
    }

    public boolean isLoggedIn() {
        return user != null;
    }

    public void logout() {
        ServicesScheduleManager.getInstance().resetScheduledServices();
        user = null;
        editor.clear();
        editor.apply();
    }

    public User getUser() {
        return user;
    }

    public void addMotorcycleToUserAndSave(Motorcycle motorcycle) {
        user.addMotorcycle(motorcycle);
        saveUser();
    }

    public boolean removeMotorcycleFromUserAndSave(int index) {
        if (user.removeMotorcycle(index)) {
            saveUser();
            return true;
        }
        return false;
    }



    private static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
        private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        @Override
        public void write(JsonWriter out, LocalDateTime value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(formatter.format(value));
            }
        }

        @Override
        public LocalDateTime read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            String dateStr = in.nextString();
            return LocalDateTime.parse(dateStr, formatter);
        }
    }
}
