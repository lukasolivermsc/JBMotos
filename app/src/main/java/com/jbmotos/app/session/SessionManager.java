package com.jbmotos.app.session;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

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
            user = User.CreateUser(gson.fromJson(userJson, User.UserData.class));
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
            String json = gson.toJson(user.getData());
            editor.putString(KEY_USER, json);
            editor.apply();
        }
    }

    public void login(User.UserData data) {
        if (isLoggedIn()) return;
        user = User.CreateUser(data);
        saveUser();
    }

    public boolean isLoggedIn() {
        return user != null;
    }

    public void logout() {
        user = null;
        editor.clear();
        editor.apply();
    }

    public User getUser() {
        return user;
    }

    public User_Regular getUserIfRegular() {
        if (user instanceof User_Regular) {
            return (User_Regular) user;
        }
        return null;
    }

    public User_Admin getUserIfAdmin() {
        if (user instanceof User_Admin) {
            return (User_Admin) user;
        }
        return null;
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
