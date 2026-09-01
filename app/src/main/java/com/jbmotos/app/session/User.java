package com.jbmotos.app.session;

public class User {

    private final UserData data;

    protected User(UserData data) {
        this.data = data;
    }

    public static User CreateUser(UserData data) {
        if (data.isAdmin()) {
            return new User_Admin(data);
        } else {
            return new User_Regular(data);
        }
    }

    public UserData getData() {
        return data;
    }

    public String getName() {
        return data.name;
    }

    public static class UserData {
        private final String name;

        public UserData(String name) {
            if (name.equalsIgnoreCase("Admin") || name.equalsIgnoreCase("adm"))  {
                this.name = "Admin";
            } else {
                this.name = name;
            }
        }

        public String getName() {
            return name;
        }

        public boolean isAdmin() {
            return name.equals("Admin");
        }
    }
}
